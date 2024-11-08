package com.huang.wb.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private long id;//id
    private String content;//内容
    private String sender;//发送者
    private String receiver;//接受者
    private String[] receivers;//多个接收者
    private int status;//状态，已读，未读，撤回 0，1，2
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime time;//发送时间
}
