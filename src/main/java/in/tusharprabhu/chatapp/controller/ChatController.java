package in.tusharprabhu.chatapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import in.tusharprabhu.chatapp.model.ChatMessage;
import in.tusharprabhu.chatapp.service.UserStatusService;

/**
 * Controller class for handling chat-related functionality.
 */
@Controller
public class ChatController {

    @Autowired
    private UserStatusService userStatusService;

    /**
     * Registers a user for chat.
     */
    @Autowired
        private SimpMessagingTemplate messagingTemplate;
    @MessageMapping("/chat.register")
    @SendTo("/topic/public")

        public ChatMessage register(
        @Payload ChatMessage chatMessage,
        SimpMessageHeaderAccessor headerAccessor) {

    String sender = chatMessage.getSender();

    if (sender == null) return chatMessage;

    if (headerAccessor.getSessionAttributes() != null) {
        headerAccessor
                .getSessionAttributes()
                .put("username", sender);
    }

    userStatusService.userConnected(sender);

    System.out.println(sender + " is ONLINE");

    ChatMessage statusMessage =
            new ChatMessage();

    statusMessage.setSender(sender);

    statusMessage.setType(
            ChatMessage.MessageType.STATUS
    );

    statusMessage.setStatus("ONLINE");

    messagingTemplate.convertAndSend(
            "/topic/public",
            statusMessage
    );

    return chatMessage;
}

    /**
     * Sends a chat message and checks receiver status.
     */
    @MessageMapping("/chat.send")
    @SendTo("/topic/public")
    public ChatMessage sendMessage(
            @Payload ChatMessage chatMessage) {

        String receiver = chatMessage.getReceiver();

        if (receiver != null) {

            if (userStatusService.isUserOnline(receiver)) {

                System.out.println(
                        "Receiver " + receiver + " is ONLINE"
                );

            } else {

                System.out.println(
                        "Receiver " + receiver + " is OFFLINE"
                );

            }
        }

        return chatMessage;
    }

    /**
     * Adds user when joining chat.
     */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(
            @Payload ChatMessage chatMessage,
            SimpMessageHeaderAccessor headerAccessor) {

        // Store username in session
        headerAccessor.getSessionAttributes()
                .put("username", chatMessage.getSender());
        

        // Mark user online
        userStatusService.userConnected(
                chatMessage.getSender()
        );

        System.out.println(
                chatMessage.getSender() + " joined chat"
        );

        return chatMessage;
    }
}