package socket.example.practice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@Log4j2
public class SocketHandler extends TextWebSocketHandler {

    private static final List<SocketStudyMember> socketStudyMembers = new ArrayList<>();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message)
            throws Exception {
        String payload = message.getPayload();
        log.info("payload : " + payload);
        for (SocketStudyMember each : socketStudyMembers) {
            each.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Long memberId = Long.parseLong(session.getHandshakeHeaders().get("memberId").get(0));
        Long studyId = Long.parseLong(session.getHandshakeHeaders().get("studyId").get(0));
        SocketStudyMember socketStudyMember = new SocketStudyMember(memberId, studyId, session);
        socketStudyMembers.add(socketStudyMember);

        log.info(session + " 클라이언트 접속");

        TextMessage textMessage = new TextMessage(memberId + "님이 입장하셨습니다.");
        socketStudyMembers.stream()
                .filter(each -> Objects.equals(each.getStudyId(), studyId))
                .forEach(each -> {
                    try {
                        each.getWebSocketSession().sendMessage(textMessage);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        log.info(session + " 클라이언트 접속 해제");
        SocketStudyMember found = socketStudyMembers.stream()
                .filter(socketStudyMember -> socketStudyMember.getWebSocketId()
                        .equals(session.getId()))
                .findAny()
                .orElseThrow();
        socketStudyMembers.remove(found);
    }
}
