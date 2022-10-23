package net.xdclass.component;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.config.SmsConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class SmsComponent {

    /**
     * 发送地址
     */
    private static final String URL_TEMPLATE = "http://jmsms.market.alicloudapi.com/sms/send?mobile=%s&templateId=%s&value=%s";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SmsConfig smsConfig;

    public void send(String to, String templateId, String value) {
        String url = String.format(URL_TEMPLATE, to, templateId, value);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization","APPCODE "+smsConfig.getAppCode());
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        //        restTemplate.postForObject(url, entity, String.class);
//        ResponseEntity<String> response = restTemplate.postForObject(url, entity, String.class);


        log.info("url = {}, body = {}", url, response.getBody());

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("发送短信验证成功");
        } else {
            log.error("发送短信验证码失败:{}", response.getBody());
        }

    }

}
