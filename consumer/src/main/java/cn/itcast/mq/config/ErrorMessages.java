package cn.itcast.mq.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 项目名称：mq-advanced-demo
 * 描述：覆盖默认的重试策略
 *
 * @author zhong
 * @date 2022-06-01 10:17
 */
@Configuration
public class ErrorMessages {

    /**
     * 定义交换机
     */
    @Bean
    public DirectExchange erroeMessages() {
        return new DirectExchange("error.direct");
    }

    /**
     * 定义队列
     */
    @Bean
    public Queue errorQueue() {
        return new Queue("error.queue");
    }

    /**
     * 绑定交换机和队列
     */
    @Bean
    public Binding errorMessagesBinding() {
        return BindingBuilder.bind(errorQueue()).to(erroeMessages()).with("error");
    }

    /**
     * 异常处理机制
     * @param rabbitTemplate
     * @return
     */
    @Bean
    public MessageRecoverer republishMessageRecoverer(RabbitTemplate rabbitTemplate){
        return new RepublishMessageRecoverer(rabbitTemplate, "error.direct", "error");
    }
}
