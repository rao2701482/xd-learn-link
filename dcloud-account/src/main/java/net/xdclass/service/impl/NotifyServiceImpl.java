package net.xdclass.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.component.SmsComponent;
import net.xdclass.config.SmsConfig;
import net.xdclass.constant.RedisKey;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.SendCodeEnum;
import net.xdclass.service.NotifyService;
import net.xdclass.util.CheckUtil;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

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
public class NotifyServiceImpl implements NotifyService {

    /**
     * 10分钟有效
     */
    private static final int CODE_EXPIRED = 1000 * 60 * 10;

    @Autowired
    private SmsComponent smsComponent;

    @Autowired
    private SmsConfig smsConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public JsonData sendCode(SendCodeEnum sendCodeEnum, String to) {

        // [防止验证码短时间内重复发送]: 发送验证码前, 得防止同一个号码在1min内被重复发送, 且发送内容的有效期为10min
        String cacheKey = String.format(RedisKey.CHECK_CODE_KEY, sendCodeEnum.name(), to);

        String code = CommonUtil.getRandomCode(6);
        //生成拼接好验证码
        String value = code + "_" + CommonUtil.getCurrentTimestamp();
        redisTemplate.opsForValue().set(cacheKey, value, CODE_EXPIRED, TimeUnit.MILLISECONDS);

        if(CheckUtil.isEmail(to)){
            //发送邮箱验证码  TODO

        }else if(CheckUtil.isPhone(to)){

            //发送手机验证码
//            smsComponent.send(to,smsConfig.getTemplateId(),code);
            log.info("假装发送了验证码 {}",code);
        }
        return JsonData.buildSuccess();

    }

    @Override
    public boolean checkCode(SendCodeEnum sendCodeEnum, String to, String code) {

        String cacheKey = String.format(RedisKey.CHECK_CODE_KEY, SendCodeEnum.USER_REGISTER.name(), to);

        String cacheValue = redisTemplate.opsForValue().get(cacheKey);

        if (StringUtils.isNotBlank(cacheValue)) {
            String cacheCode = cacheValue.split("_")[0];
            if (cacheCode.equalsIgnoreCase(code)) {
                redisTemplate.delete(cacheKey);
                return true;
            }
        }

        return false;
    }


}
