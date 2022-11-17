package net.xdclass.listener;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import net.xdclass.enums.BizCodeEnum;
import net.xdclass.exception.BizException;
import net.xdclass.model.EventMessage;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

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
// 如果在一个微服务中只有消费者, 服务的交换机和队列声明在另外一个服务中(懒加载), 则直接进行队列消费会出现异常, 需要使用下面的方式保证队列存在
//@RabbitListener(queues = "short_link.add.link.queue")
@RabbitListener(queuesToDeclare = { @Queue("short_link.add.link.queue") })
public class ShortLinkAddLinkMQListener {



    @RabbitHandler
    public void shortLinkHandler(EventMessage eventMessage, Message message, Channel channel) throws IOException {
        log.info("监听到消息ShortLinkAddLinkMQListener message消息内容:{}",message);
        long tag = message.getMessageProperties().getDeliveryTag();
        try{

            //TODO 处理业务逻辑

        }catch (Exception e){

            //处理业务异常，还有进行其他操作，比如记录失败原因
            log.error("消费失败:{}",eventMessage);
            throw new BizException(BizCodeEnum.MQ_CONSUME_EXCEPTION);
        }
        log.info("消费成功:{}",eventMessage);
        //确认消息消费成功
        channel.basicAck(tag,false);

    }



}
