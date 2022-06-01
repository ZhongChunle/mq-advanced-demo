package cn.itcast.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CommonConfig {

    /**
     * 创建持久化交换机
     * @return
     */
    @Bean
    public DirectExchange simpleDirect() {
        // 第一个参数：交换机名称  第二个参数：是否开启持久化  第三个参数：是否自动删除
        // return new DirectExchange("simpl.direct",true,false);
        return new DirectExchange("simpl.direct");
    }

    /**
     * 创建持久化的队列
     */
    @Bean
    public Queue simpleQueue() {
        return QueueBuilder.durable("simple.queue").build();
    }
}
