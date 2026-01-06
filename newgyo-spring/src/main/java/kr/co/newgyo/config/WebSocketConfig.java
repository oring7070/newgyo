package kr.co.newgyo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 클라이언트가 구독할 prefix (브로드캐스트 메시지 받을 때)
        registry.enableSimpleBroker("/topic", "/queue");

        // 클라이언트가 서버로 메시지 보낼 때 사용할 prefix
        registry.setApplicationDestinationPrefixes("/app");

        // 1:1 메시지 보낼 때 사용자별 prefix (선택)
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // WebSocket 핸드셰이크 엔드포인트 등록
        // 클라이언트가 /ws-chat 으로 연결
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")  // CORS 허용 (개발 시)
                .withSockJS();                  // SockJS fallback 지원 (구형 브라우저)
    }
}
