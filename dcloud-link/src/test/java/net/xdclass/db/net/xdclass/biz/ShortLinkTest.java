package net.xdclass.db.net.xdclass.biz;

import com.google.common.hash.Hashing;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.LinkApplication;
import net.xdclass.component.ShortLinkComponent;
import net.xdclass.manager.ShortLinkManager;
import net.xdclass.model.ShortLinkDO;
import net.xdclass.util.CommonUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LinkApplication.class)
@Slf4j
public class ShortLinkTest {

    @Autowired
    private ShortLinkComponent shortLinkComponent;

    @Test
    public void testMurmurHash(){

        for(int i=0; i<5; i++){

            String originalUrl = "https://xdclass.net?id="+ CommonUtil.generateUUID()+"pwd="+CommonUtil.getStringNumRandom(7);

            long murmur3_32 = Hashing.murmur3_32().hashUnencodedChars(originalUrl).padToLong();
            log.info("murmur3_32={}",murmur3_32);

        }

    }

    /**
     * 测试短链平台
     */
    @Test
    public void testCreateShortLink() {

        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int num1 = random.nextInt(10);
            int num2 = random.nextInt(10000000);
            int num3 = random.nextInt(10000000);
            String originalUrl = num1 + "xdclass" + num2 + ".net" + num3;
            String shortLinkCode = shortLinkComponent.createShortLinkCode(originalUrl);
            log.info("originalUrl:" + originalUrl + ", shortLinkCode=" + shortLinkCode);
        }
    }

    @Autowired
    private ShortLinkManager shortLinkManager;

    @Test
    public void testSaveShortLink() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int num1 = random.nextInt(10);
            int num2 = random.nextInt(100000000);
            int num3 = random.nextInt(100000000);
            String originalUrl = num1 + "xdclass" + num2 + ".net" + num3;
            String shortLinkCode = shortLinkComponent.createShortLinkCode(originalUrl);

            ShortLinkDO shortLinkDO = new ShortLinkDO();
            shortLinkDO.setCode(shortLinkCode);
            shortLinkDO.setAccountNo(Long.valueOf(num3));
            shortLinkDO.setSign(CommonUtil.MD5(originalUrl));
            shortLinkDO.setDel(0);

            shortLinkManager.addShortLink(shortLinkDO);

        }


    }

    @Test
    public void testFind(){

        ShortLinkDO shortLinCode = shortLinkManager.findByShortLinCode("04DzCFLa");
        log.info(shortLinCode.toString());

    }

}
