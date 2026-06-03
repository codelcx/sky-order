package com.sky.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint("/ws/{sid}")
@Slf4j
public class WebSocketServer {

    // 每次有新的客户端连接，WebSocket 容器都会创建一个新的 WebSocketServer 实例
    // 所以必须要用static修饰才能保证消息推送成功
    private final static Map<String, Session> SESSIONS = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session, @PathParam("sid") String sid) {
        // 关联 session 和 sid
        SESSIONS.put(sid, session);
        log.info("建立连接，sessionId={}", session.getId());
    }

    @OnMessage
    public void onMessage(String message, @PathParam("sid") String sid) {
        log.info("收到来自客户端：{}的信息:{}", sid, message);
    }

    @OnClose
    public void onClose(@PathParam("sid") String sid)  {
        log.info("连接断开:{}", sid);
        SESSIONS.remove(sid);
    }

    public void sendToAllClient(String message) {
        Collection<Session> session = SESSIONS.values();
        for (Session s : session) {
            try {
                // 服务器向客户端发送消息
                log.info("推送消息{}", message);
                s.getBasicRemote().sendText(message);
                log.info("消息推送成功");
            } catch (Exception e) {
                log.error("send message error: ", e);
            }
        }
    }
}
