package com.shanwei.order.config;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String ORDER_NOTIFY_QUEUE = "shanwei.order.notify";
    public static final String COMMENT_NOTIFY_QUEUE = "shanwei.comment.notify";

    @Bean
    public Queue orderNotifyQueue() {
        return new Queue(ORDER_NOTIFY_QUEUE, true);
    }

    @Bean
    public Queue commentNotifyQueue() {
        return new Queue(COMMENT_NOTIFY_QUEUE, true);
    }
}
