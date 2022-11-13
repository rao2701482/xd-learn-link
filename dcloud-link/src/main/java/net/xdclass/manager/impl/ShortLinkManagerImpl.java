package net.xdclass.manager.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.manager.ShortLinkManager;
import net.xdclass.mapper.ShortLinkMapper;
import net.xdclass.model.ShortLinkDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@Component
@Slf4j
public class ShortLinkManagerImpl implements ShortLinkManager {

    @Autowired
    private ShortLinkMapper shortLinkMapper;

    @Override
    public int addShortLink(ShortLinkDO shortLinkDO) {
        return shortLinkMapper.insert(shortLinkDO);
    }

    @Override
    public ShortLinkDO findByShortLinCode(String shortLinkCode) {

        ShortLinkDO shortLinkDO = shortLinkMapper.selectOne(
                new QueryWrapper<ShortLinkDO>().eq("code", shortLinkCode));
        return shortLinkDO;
    }

    @Override
    public int del(String shortLinkCode, Long accountNo) {

        ShortLinkDO shortLinkDO = new ShortLinkDO();
        shortLinkDO.setDel(1);

        int rows = shortLinkMapper.update(shortLinkDO,
                new QueryWrapper<ShortLinkDO>().eq("code", shortLinkCode).eq("account_no", accountNo));
        return rows;
    }
}
