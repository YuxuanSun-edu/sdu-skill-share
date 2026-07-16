package com.shanwei.message.mq;

import com.shanwei.message.config.RabbitConfig;
import com.shanwei.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class NotifyListener {
    private final MessageService messageService;

    @RabbitListener(queues = RabbitConfig.ORDER_NOTIFY_QUEUE)
    public void onOrderNotify(Map<String, Object> payload) {
        messageService.saveNotify(payload);
    }

    @RabbitListener(queues = RabbitConfig.COMMENT_NOTIFY_QUEUE)
    public void onCommentNotify(Map<String, Object> payload) {
        messageService.saveNotify(payload);
    }
}
