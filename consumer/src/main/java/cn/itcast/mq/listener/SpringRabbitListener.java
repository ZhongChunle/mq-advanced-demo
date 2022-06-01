package cn.itcast.mq.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SpringRabbitListener {

    @RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String msg) {
        log.debug("消费者接收到simple.queue的消息：【" + msg + "】");
        System.out.println(1/0);
        log.info("消费者处理消息成功");
    }

    /**
     * 创建死信交换机【消费者】
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "dl.queue",durable = "true"),
            exchange = @Exchange(name = "dl.direct"),
            key = "dl"
    ))
    public void listenDlQueue(String msg){
        log.info("消费者接收到了dl.queue的延迟消息："+msg);
    }

    /**
     * 基于注解的开发使用插件
     * 交换机的差别就是设置了【delayed = "true"】属性
     * @param msg
     */
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "delay.queue",durable = "true"),
//            exchange = @Exchange(name = "delay.direct",delayed = "true"),
//            key = "delay"
//    ))
//    public void listDelayExchange(String msg){
//        log.info("消费者接收到了delay.queue的延迟消息："+msg);
//    }

    /**
     * 创建交换机
     * @return
     */
    @Bean
    public DirectExchange delayedExchange() {
        return ExchangeBuilder
                .directExchange("delay.queue") // 指定交换机类型和名称
                .delayed() // 设置delay属性为true
                .durable(true) // 持久化
                .build();
    }

    /**
     * 创建队列
     */
    @Bean
    public org.springframework.amqp.core.Queue delayedQueue(){
        return new org.springframework.amqp.core.Queue("delay.direct");
    }

    /**
     * 绑定交换机和队列
     */
    @Bean
    public Binding delayedBinding(){
        return BindingBuilder.bind(delayedQueue()).to(delayedExchange()).with("delay");
    }

    /**
     * 监听延迟消息
     * @param msg
     */
    @RabbitListener(queues = "delay.queue")
    public void listDelayExchange(String msg){
        log.info("消费者接收到了delay.queue的延迟消息："+msg);
    }
}
