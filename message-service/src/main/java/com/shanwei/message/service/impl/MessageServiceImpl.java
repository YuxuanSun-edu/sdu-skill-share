package com.shanwei.message.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.shanwei.common.BizException;
import com.shanwei.message.entity.Message;
import com.shanwei.message.mapper.MessageMapper;
import com.shanwei.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageMapper messageMapper;

    @Override
    public Message saveNotify(Map<String, Object> payload) {
        Message message = new Message();
        message.setToUser(Long.valueOf(String.valueOf(payload.get("toUser"))));
        message.setTitle(String.valueOf(payload.get("title")));
        message.setContent(String.valueOf(payload.get("content")));
        message.setStatus("UNREAD");
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);
        return message;
    }

    @Override
    public List<Message> all() {
        return messageMapper.selectList(new LambdaQueryWrapper<Message>().orderByDesc(Message::getCreateTime));
    }

    @Override
    public List<Message> list(Long userId) {
        return messageMapper.selectList(new LambdaQueryWrapper<Message>()
                .eq(Message::getToUser, userId)
                .orderByDesc(Message::getCreateTime));
    }

    @Override
    public Message read(Long id) {
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BizException("消息不存在");
        }
        message.setStatus("READ");
        messageMapper.updateById(message);
        return message;
    }

    @Override
    public void delete(Long id) {
        Message message = messageMapper.selectById(id);
        if (message == null) {
            throw new BizException("消息不存在");
        }
        messageMapper.deleteById(id);
    }
}
