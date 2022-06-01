package cn.itcast.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 项目名称：mq-advanced-demo
 * 描述：TTL延迟创建、
 *
 * @author zhong
 * @date 2022-06-01 11:17
 */
@Configuration
public class TTLMessageConfig {
    /**
     * 创建交换机
     */
    @Bean
    public DirectExchange ttlDirectExchange() {
        return new DirectExchange("ttl.direct");
    }

    /**
     * 创建队列，指定延迟时间
     */
    @Bean
    public Queue ttlQueue(){
        return QueueBuilder
                .durable("ttl.queue")
                .ttl(10000)
                .deadLetterExchange("dl.direct") // 绑定的死信交换机
                .deadLetterRoutingKey("dl")
                .build();
    }

    /**
     * 绑定交换机和队列
     */
    @Bean
    public Binding ttlBinding(){
        return BindingBuilder.bind(ttlQueue()).to(ttlDirectExchange()).with("ttl");
    }

}
