package net.xdclass.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.component.ShortLinkComponent;
import net.xdclass.config.RabbitMQConfig;
import net.xdclass.controller.request.ShortLinkAddRequest;
import net.xdclass.enums.DomainTypeEnum;
import net.xdclass.enums.EventMessageType;
import net.xdclass.enums.ShortLinkStateEnum;
import net.xdclass.interceptor.LoginInterceptor;
import net.xdclass.manager.DomainManager;
import net.xdclass.manager.GroupCodeMappingManager;
import net.xdclass.manager.LinkGroupManager;
import net.xdclass.manager.ShortLinkManager;
import net.xdclass.model.*;
import net.xdclass.service.ShortLinkService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.IDUtil;
import net.xdclass.util.JsonData;
import net.xdclass.util.JsonUtil;
import net.xdclass.vo.ShortLinkVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Service
@Slf4j
public class ShortLinkServiceImpl implements ShortLinkService {

    @Autowired
    private ShortLinkManager shortLinkManager;


    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitMQConfig rabbitMQConfig;

    @Autowired
    private DomainManager domainManager;

    @Autowired
    private LinkGroupManager linkGroupManager;


    @Autowired
    private ShortLinkComponent shortLinkComponent;

    @Autowired
    private GroupCodeMappingManager groupCodeMappingManager;

    @Override
    public ShortLinkVO parseShortLinkCode(String shortLinkCode) {

        ShortLinkDO shortLinkDO = shortLinkManager.findByShortLinCode(shortLinkCode);
        if(shortLinkDO == null){
            return null;
        }
        ShortLinkVO shortLinkVO = new ShortLinkVO();
        BeanUtils.copyProperties(shortLinkDO,shortLinkVO);
        return shortLinkVO;
    }


    @Override
    public JsonData createShortLink(ShortLinkAddRequest request) {

        Long accountNo = LoginInterceptor.threadLocal.get().getAccountNo();

        String newOriginalUrl = CommonUtil.addUrlPrefix(request.getOriginalUrl());
        request.setOriginalUrl(newOriginalUrl);

        EventMessage eventMessage = EventMessage.builder().accountNo(accountNo)
                .content(JsonUtil.obj2Json(request))
                .messageId(IDUtil.geneSnowFlakeID().toString())
                .eventMessageType(EventMessageType.SHORT_LINK_ADD.name())
                .build();

        rabbitTemplate.convertAndSend(rabbitMQConfig.getShortLinkEventExchange(), rabbitMQConfig.getShortLinkAddRoutingKey(),eventMessage);

        return JsonData.buildSuccess();
    }

    /**
     * 处理短链新增逻辑
     * <p>
     * //判断短链域名是否合法
     * //判断组名是否合法
     * //生成长链摘要
     * //生成短链码
     * //加锁
     * //查询短链码是否存在
     * //构建短链对象
     * //保存数据库
     *
     * @param eventMessage
     * @return
     */
    @Override
    public boolean handlerAddShortLink(EventMessage eventMessage) {

        Long accountNo = eventMessage.getAccountNo();
        String messageType = eventMessage.getEventMessageType();

        ShortLinkAddRequest addRequest = JsonUtil.json2Obj(eventMessage.getContent(), ShortLinkAddRequest.class);
        //短链域名校验
        DomainDO domainDO = checkDomain(addRequest.getDomainType(), addRequest.getDomainId(), accountNo);
        //校验组是否合法
        LinkGroupDO linkGroupDO = checkLinkGroup(addRequest.getGroupId(), accountNo);

        //长链摘要
        String originalUrlDigest = CommonUtil.MD5(addRequest.getOriginalUrl());

        //生成短链码
        String shortLinkCode = shortLinkComponent.createShortLinkCode(addRequest.getOriginalUrl());

        //TODO 加锁


        //先判断是否短链码被占用
        ShortLinkDO ShortLinCodeDOInDB = shortLinkManager.findByShortLinCode(shortLinkCode);


        if(ShortLinCodeDOInDB == null){

            //C端处理
            if (EventMessageType.SHORT_LINK_ADD_LINK.name().equalsIgnoreCase(messageType)) {
                ShortLinkDO shortLinkDO = ShortLinkDO.builder()
                        .accountNo(accountNo)
                        .code(shortLinkCode)
                        .title(addRequest.getTitle())
                        .originalUrl(addRequest.getOriginalUrl())
                        .domain(domainDO.getValue())
                        .groupId(linkGroupDO.getId())
                        .expired(addRequest.getExpired())
                        .sign(originalUrlDigest)
                        .state(ShortLinkStateEnum.ACTIVE.name())
                        .del(0)
                        .build();
                shortLinkManager.addShortLink(shortLinkDO);
                return true;

            } else if (EventMessageType.SHORT_LINK_ADD_MAPPING.name().equalsIgnoreCase(messageType)) {

                //B端处理
                GroupCodeMappingDO groupCodeMappingDO = GroupCodeMappingDO.builder()
                        .accountNo(accountNo)
                        .code(shortLinkCode)
                        .title(addRequest.getTitle())
                        .originalUrl(addRequest.getOriginalUrl())
                        .domain(domainDO.getValue())
                        .groupId(linkGroupDO.getId())
                        .expired(addRequest.getExpired())
                        .sign(originalUrlDigest)
                        .state(ShortLinkStateEnum.ACTIVE.name())
                        .del(0)
                        .build();

                groupCodeMappingManager.add(groupCodeMappingDO);
                return true;

            }
        }
        return false;
    }

    /**
     * 校验域名
     *
     * @param domainType
     * @param domainId
     * @param accountNo
     * @return
     */
    private DomainDO checkDomain(String domainType, Long domainId, Long accountNo) {

        DomainDO domainDO;

        if (DomainTypeEnum.CUSTOM.name().equalsIgnoreCase(domainType)) {
            domainDO = domainManager.findById(domainId, accountNo);

        } else {
            domainDO = domainManager.findByDomainTypeAndID(domainId, DomainTypeEnum.OFFICIAL);
        }
        Assert.notNull(domainDO, "短链域名不合法");
        return domainDO;
    }

    /**
     * 校验组名
     *
     * @param groupId
     * @param accountNo
     * @return
     */
    private LinkGroupDO checkLinkGroup(Long groupId, Long accountNo) {

        LinkGroupDO linkGroupDO = linkGroupManager.detail(groupId, accountNo);
        Assert.notNull(linkGroupDO, "组名不合法");
        return linkGroupDO;
    }

}
