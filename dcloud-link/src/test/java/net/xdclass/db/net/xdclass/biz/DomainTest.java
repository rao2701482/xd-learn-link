package net.xdclass.db.net.xdclass.biz;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.LinkApplication;
import net.xdclass.manager.DomainManager;
import net.xdclass.model.DomainDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
@Slf4j
public class DomainTest {

    @Autowired
    private DomainManager domainManager;

    @Test
    public void testListDomain() {


        List<DomainDO> domainDOS = domainManager.listOfficialDomain();

        log.info(domainDOS.toString());

    }




}
