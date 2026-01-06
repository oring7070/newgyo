package kr.co.newgyo.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageResponse {
    private String sender;    // 보낸 사람 (User 또는 Bot)
    private String content;   // 메시지 내용

    // getter, setter, 기본 생성자 필수
    public ChatMessageResponse() {}

    public ChatMessageResponse(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }
}
