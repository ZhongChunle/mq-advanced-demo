package cn.itcast.mq.spring;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.nio.charset.StandardCharsets;
import java.util.UUID;


@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Test
    public void testSendMessage2SimpleQueue() throws InterruptedException {
        String routingKey = "simple.test";
        // 1、准备消息
        String message = "hello, spring amqp!";
        // 2、准备correlationData
        // 2.1、消息id
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 2.2、准备ConfirmCallback
        correlationData.getFuture().addCallback(confirm -> {
            // 判断结果
            if(confirm.isAck()){
                // ACK
                log.info("消息投递到交换机成功！消息ID：{}",correlationData.getId());
            }else{
                // NCAK
                log.info("消息投递到交换机失败，消息ID：{}"+correlationData.getId());
            }
        }, throwable -> {
            // 记录日志
            log.info("消息发送失败："+ throwable);
            // 重发消息
        });
        // 3、发送消息
        rabbitTemplate.convertAndSend("zcl.topic", routingKey, message);
    }

    /**
     * 发送消息持久化
     */
    @Test
    public void testDueableMessage(){
        // (MessageDeliveryMode.PERSISTENT：设置消息持久
        Message build = MessageBuilder.withBody("hellow spring".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT).build();
        // 发送消息，指定通道
        rabbitTemplate.convertAndSend("simple.queue",build);
    }

    /**
     * 发送消息到延时队列中
     */
    @Test
    public void testTTLMessage(){
        // (MessageDeliveryMode.PERSISTENT：设置消息持久
        Message build = MessageBuilder.withBody("hellow ttl".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                // .setExpiration("5000") // 设置消息的延时时间
                .build();
        // 发送消息，指定通道
        rabbitTemplate.convertAndSend("ttl.direct","ttl",build);
        log.info("消息已经成功发送了");
    }

    /**
     * 发送插件的延时时间
     */
    @Test
    public void testSendDelayMessage() {
        // 1、准备消息
        Message build = MessageBuilder.withBody("hellow delay messages".getBytes(StandardCharsets.UTF_8))
                .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                .setHeader("x-delay",5000) // 设置插件消息的延时时间
                .build();

        // 2、准备correlationData
        CorrelationData correlationData = new CorrelationData(UUID.randomUUID().toString());
        // 3、发送消息
        rabbitTemplate.convertAndSend("delay.direct","delay",build,correlationData);
        log.info("消息已经成功发送了");
    }
}
