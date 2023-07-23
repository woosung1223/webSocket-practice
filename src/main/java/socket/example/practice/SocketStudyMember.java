package socket.example.practice;

import java.io.IOException;
import lombok.Getter;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Getter
public class SocketStudyMember {

    private Long id;
    private Long memberId;
    private Long studyId;

    private WebSocketSession webSocketSession;

    public SocketStudyMember(Long memberId, Long studyId, WebSocketSession webSocketSession) {
        this.memberId = memberId;
        this.studyId = studyId;
        this.webSocketSession = webSocketSession;
    }

    public void sendMessage(TextMessage message) throws IOException {
        webSocketSession.sendMessage(message);
    }

    public String getWebSocketId() {
        return webSocketSession.getId();
    }
}
