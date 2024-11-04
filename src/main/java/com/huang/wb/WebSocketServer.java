package com.huang.wb;

import cn.hutool.json.JSONUtil;
import com.huang.wb.dimain.ChatMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ServerEndpoint("/wb/{unicode}")
public class WebSocketServer {
    /**
     * 线程安全的静态变量，用来记录当前在线连接数。
     */
    private static final AtomicInteger onlineNum = new AtomicInteger();
    /**
     *  所有在线用户
     *  线程安全Set。用来存放每个用户对应的WebSocketServer对象。
     */
    @Getter
    private static final ConcurrentHashMap<String, Session> sessionPools = new ConcurrentHashMap<>();
    /**
     * 发送消息
     * @param session session
     * @param message message
     * @throws IOException e
     */
    public void sendChatMessage(Session session, String message) throws IOException {
        synchronized (session) {
            session.getBasicRemote().sendText(message);
        }
    }
    /**
     * 给指定用户发送信息
     * @param unicode userName
     * @param message message
     */

    public void sendInfo(String unicode, ChatMessage message){
        String josnStr = JSONUtil.toJsonStr(message);
        Session session = sessionPools.get(unicode);
        if(session == null) {
            message.setContent("【系统通知】对方目前离线，无法实时收到您的消息。");
            message.setReceiver(message.getReceiver());
            message.setTime(LocalDateTime.now());
            josnStr = JSONUtil.toJsonStr(message);
            session = sessionPools.get(message.getSender());
        }
        try {
            sendChatMessage(session, josnStr);
        }catch (Exception e){
            log.error("发送消息失败：", e);
        }
    }
    /**
     * 群发消息
     * @param message message
     */
    public void broadcast(String message){
        for (Session session: sessionPools.values()) {
            try {
                sendChatMessage(session, message);
            } catch(Exception e){
                log.error("群发异常：{}", e.getMessage());
            }
        }
    }
    /**
     * 建立连接成功调用
     * @param session session
     * @param unicode userName
     */
    @OnOpen
    public void onOpen(Session session, @PathParam(value = "unicode") String unicode){
        Session oldSession = sessionPools.get(unicode);
        if(oldSession == null) {
            sessionPools.put(unicode, session);
            log.info(unicode + "加入聊天室！当前人数为" + sessionPools.size());
            // 广播上线消息
//            ChatMessage msg = new ChatMessage(
//                    0,
//                    "id=" + userId + "加入webSocket！当前人数为" + sessionPools.size(),
//                    -999
//            );
//            broadcast(JSONUtil.toJsonStr(msg));
        }else{
            log.info(unicode+"已登录");
        }
    }
    //关闭连接时调用
    @OnClose
    public void onClose(@PathParam(value = "unicode") String unicode){
        sessionPools.remove(unicode);
        log.info(unicode + "断开连接！当前人数为" + sessionPools.size());
        // 广播下线消息
//        ChatMessage msg = new ChatMessage(
//                0,
//                "id=" + userId + ",断开webSocket连接！当前人数为" + sessionPools.size(),
//                -999
//        );
//        broadcast(JSONUtil.toJsonStr(msg));
    }
    /**
     * 收到客户端信息后，根据接收人的username把消息推下去或者群发
     *  to=-1群发消息
     * @param message message
     * @throws IOException
     */
    @OnMessage
    public void onMessage(String message) throws IOException{
        ChatMessage msg = JSONUtil.toBean(message, ChatMessage.class);
        msg.setTime(LocalDateTime.now());
        // 接受者为-999为群发， 否则单发
        if (msg.getReceiver().equals(-999)) {
            broadcast(JSONUtil.toJsonStr(msg));
        } else {
            sendInfo(msg.getReceiver(),msg);
        }
    }
    /**
     * 错误时调用
     * @param session  session
     * @param throwable throwable
     */
    @OnError
    public void onError(Session session, Throwable throwable){
        log.error("发生错误");
        throwable.getCause().printStackTrace();
    }
    /**
     * 在线数 + 1
     */
    public static void addOnlineCount(){
        onlineNum.incrementAndGet();
    }
    /**
     * 在线数 - 1
     */
    public static void subOnlineCount() {
        onlineNum.decrementAndGet();
    }
}
