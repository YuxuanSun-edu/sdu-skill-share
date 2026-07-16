package com.shanwei.message.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("message")
public class Message {
    private Long id;
    private Long toUser;
    private String title;
    private String content;
    private String status;
    private LocalDateTime createTime;
}
