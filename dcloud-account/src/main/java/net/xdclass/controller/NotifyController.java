package net.xdclass.controller;

import com.google.code.kaptcha.Producer;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.controller.request.SendCodeRequest;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.enums.SendCodeEnum;
import net.xdclass.service.NotifyService;
import net.xdclass.util.CommonUtil;
import net.xdclass.util.JsonData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 小滴课堂,愿景：让技术不再难学
 *
 * @Description
 * @Author 二当家小D
 * @Remark 有问题直接联系我，源码-笔记-技术交流群
 * @Version 1.0
 **/

@RestController
@RequestMapping("/api/account/v1")
@Slf4j
public class NotifyController {

    @Autowired
    private Producer captchaProducer;

    @Autowired
    private NotifyService notifyService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 验证码过期时间
     */
    private static final long CAPTCHA_CODE_EXPIRED = 1000 * 10 *  60;

    /**
     * 获取验证码
     * @param request
     * @param response
     */
    @GetMapping("/captcha")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) {

        String captchaText = captchaProducer.createText();
        log.info("验证码内容: {}", captchaText);

        // 把生成的验证码存入缓存, 在用户校验时需要被使用
        redisTemplate.opsForValue().set(getCaptchaKey(request), captchaText, CAPTCHA_CODE_EXPIRED, TimeUnit.MILLISECONDS);

        BufferedImage bufferedImage = captchaProducer.createImage(captchaText);

        try (ServletOutputStream outputStream = response.getOutputStream()) {

            ImageIO.write(bufferedImage, "jpg", outputStream);
            outputStream.flush();

        } catch (IOException e) {
            log.error("获取流出错:{}",e.getMessage(), e);
        }

    }

    /**
     * 发送验证码接口
     * @return
     */
    @PostMapping("/send_code")
    public JsonData sendCode(@RequestBody SendCodeRequest sendCodeRequest, HttpServletRequest request) {

        // [防刷校验]: 发送邮件或短信之前, 得先校验当前请求的验证码是否正确

        String key = getCaptchaKey(request);

        String cacheCaptcha = redisTemplate.opsForValue().get(key);

        String captcha = sendCodeRequest.getCaptcha();

        if(captcha!=null && cacheCaptcha !=null && cacheCaptcha.equalsIgnoreCase(captcha)){
            //成功
            redisTemplate.delete(key);
            JsonData jsonData = notifyService.sendCode(SendCodeEnum.USER_REGISTER,sendCodeRequest.getTo());
            return jsonData;
        }else {
            return JsonData.buildResult(BizCodeEnum.CODE_CAPTCHA_ERROR);
        }
    }

    private String getCaptchaKey(HttpServletRequest request) {
        String ip = CommonUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        String key = "account-service:captcha:" + CommonUtil.MD5(ip + userAgent);
        log.info("验证码key:{}", key);
        return key;
    }


//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Async
//    public void testSend() {
//        try {
//            TimeUnit.MILLISECONDS.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        long beginTime = CommonUtil.getCurrentTimestamp();
//        ResponseEntity<String> forEntity = restTemplate.getForEntity("http://old.xdclass.net", String.class);
//        String body = forEntity.getBody();
//        long endTime = CommonUtil.getCurrentTimestamp();
//        log.info("耗时={},body={}",endTime-beginTime,body);
//
//    }



}
