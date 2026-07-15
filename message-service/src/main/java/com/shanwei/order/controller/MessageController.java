package com.shanwei.message.controller;

import com.shanwei.common.ApiResponse;
import com.shanwei.message.entity.Message;
import com.shanwei.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/message")
public class MessageController {
    private final MessageService messageService;

    @GetMapping
    public ApiResponse<List<Message>> list(@RequestParam Long userId) {
        return ApiResponse.success(messageService.list(userId));
    }

    @GetMapping("/admin/all")
    public ApiResponse<List<Message>> all() {
        return ApiResponse.success(messageService.all());
    }

    @PutMapping("/{id}/read")
    public ApiResponse<Message> read(@PathVariable Long id) {
        return ApiResponse.success(messageService.read(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        messageService.delete(id);
        return ApiResponse.success();
    }
}
