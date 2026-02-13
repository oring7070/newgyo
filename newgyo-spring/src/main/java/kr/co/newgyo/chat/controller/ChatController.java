package kr.co.newgyo.chat.controller;

import kr.co.newgyo.chat.dto.ChatMessageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final RestTemplate restTemplate;

    @Value("${app.chat.key}")
    private String hostName;
    // 클라이언트가 챗봇을 킬때 동작할 메서스 작성
    // 예상 질문표? 필요시

    // 클라이언트가 /app/chat.send 로 보내면 이 메서드 실행
    // 파이썬 을 통해 ai api 사용
    @MessageMapping("/chat.send")
    @SendToUser("/queue/reply")
    public ChatMessageResponse sendMessage(@Payload ChatMessageResponse chatMessage) {
        // 여기서 Python으로 전달하는 로직 추가할 예정
        // Python 서버로 HTTP POST 요청
        String pythonUrl = hostName +"/api/chatAi";  // Python 서버 주소

        // 요청 바디 (JSON)
        Map<String, String> request = new HashMap<>();
        request.put("sender", chatMessage.getSender());
        request.put("content", chatMessage.getContent());

        try {
            // Python으로부터 응답 받기
            ResponseEntity<Map> response = restTemplate.postForEntity(pythonUrl, request, Map.class);
            Map<String, String> pythonResponse = response.getBody();

            // 응답 처리
            ChatMessageResponse messageResponse = new ChatMessageResponse();
            messageResponse.setContent(pythonResponse.getOrDefault("content", "Python 응답 오류"));
            messageResponse.setSender("AI Bot");  // 또는 pythonResponse.get("sender")
            return messageResponse;

        } catch (Exception e) {
            e.printStackTrace();
            ChatMessageResponse errorResponse = new ChatMessageResponse();
            errorResponse.setContent("Python 서버 연결 실패");
            errorResponse.setSender("System");
            return errorResponse;
        }

    }

}
