package net.xdclass.db.net.xdclass.biz;

import lombok.extern.slf4j.Slf4j;
import net.xdclass.AccountApplication;
import net.xdclass.mapper.TrafficMapper;
import net.xdclass.model.TrafficDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AccountApplication.class)
@Slf4j
public class TrafficTest {


    @Autowired
    private TrafficMapper trafficMapper;


    @Test
    public void testSaveTraffic() {

        Random random = new Random();
        for (int i = 0; i < 1; i++) {

            TrafficDO trafficDO = new TrafficDO();
            trafficDO.setAccountNo(Long.valueOf(random.nextInt(100)));
            trafficMapper.insert(trafficDO);
        }

    }


}
