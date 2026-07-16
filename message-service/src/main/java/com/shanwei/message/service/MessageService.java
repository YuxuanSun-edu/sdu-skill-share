package com.shanwei.message.service;

import com.shanwei.message.entity.Message;

import java.util.List;
import java.util.Map;

public interface MessageService {
    Message saveNotify(Map<String, Object> payload);

    List<Message> all();

    List<Message> list(Long userId);

    Message read(Long id);

    void delete(Long id);
}
