package in.tusharprabhu.chatapp.config;

import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import in.tusharprabhu.chatapp.model.ChatMessage;
import in.tusharprabhu.chatapp.service.UserStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessageSendingOperations messagingTemplate;
    private final UserStatusService userStatusService;

    @EventListener
    public void handleWebSocketDisconnectListener(
            SessionDisconnectEvent event
    ) {

        StompHeaderAccessor headerAccessor =
                StompHeaderAccessor.wrap(event.getMessage());

        Map<String, Object> sessionAttributes =
                headerAccessor.getSessionAttributes();

        if (sessionAttributes != null) {

            String username =
                    (String) sessionAttributes.get("username");

            if (username != null) {

                log.info("User disconnected: {}", username);

                userStatusService.userDisconnected(username);

                var chatMessage =
                        ChatMessage.builder()
                                .type(ChatMessage.MessageType.LEAVE)
                                .sender(username)
                                .build();

                messagingTemplate.convertAndSend(
                        "/topic/public",
                        chatMessage
                );
                
            }
        }
    }
}