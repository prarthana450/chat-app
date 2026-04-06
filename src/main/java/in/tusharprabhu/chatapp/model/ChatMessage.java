package in.tusharprabhu.chatapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a chat message in the chat application.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {

    private String content;
    private String sender;
    private MessageType type;
    private String receiver;
    private String status; 

    /**
     * Enum representing the type of the chat message.
     */
    public enum MessageType {
        CHAT,
        LEAVE,
        JOIN,
        STATUS
    }
}