package com.hss.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.google.common.collect.Maps;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket监听连接工具类
 */
@Component
@ServerEndpoint(value = "/connectWebSocket/{userName}")
public class WebSocket {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 在线人数
     */
    public static int onlineNumber = 0;
    /**
     * 以用户的姓名为key，WebSocket为对象保存起来
     */
    private static Map<String, WebSocket> clients = new ConcurrentHashMap<String, WebSocket>();
    /**
     * 会话
     */
    private Session session;
    /**
     * 用户名称
     */
    private String userName;

    /**
     * 建立连接
     *
     * @param session
     */
    @OnOpen
    public void onOpen(@PathParam("userName") String userName, Session session)
    {
        onlineNumber++;
        logger.info("现在来连接的客户id："+session.getId()+"用户名："+userName);
        this.userName = userName;
        this.session = session;
        //  logger.info("有新连接加入！ 当前在线人数" + onlineNumber);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
            //先给所有人发送通知，说我上线了
            Map<String,Object> map1 = Maps.newHashMap();
            map1.put("messageType",1);
            map1.put("userName",userName);
            sendMessageAll(JSON.toJSONString(map1),userName);

            //把自己的信息加入到map当中去
            clients.put(userName, this);
            logger.info("有连接关闭！ 当前在线人数" + clients.size());
            //给自己发一条消息：告诉自己现在都有谁在线
            Map<String,Object> map2 = Maps.newHashMap();
            map2.put("messageType",3);
            //移除掉自己
            Set<String> set = clients.keySet();
            map2.put("onlineUsers",set);
            sendMessageTo(JSON.toJSONString(map2),userName);
        }
        catch (IOException e){
            logger.info(userName+"上线的时候通知所有人发生了错误");
        }
    }

    /**
     * 放生异常
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        logger.info("服务端发生了错误"+error.getMessage());
        error.printStackTrace();
    }

    /**
     * 连接关闭
     */
    @OnClose
    public void onClose()
    {
        onlineNumber--;
        //webSockets.remove(this);
        clients.remove(userName);
        try {
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
            Map<String,Object> map1 = Maps.newHashMap();
            map1.put("messageType",2);
            map1.put("onlineUsers",clients.keySet());
            map1.put("userName",userName);
            sendMessageAll(JSON.toJSONString(map1),userName);
        }
        catch (IOException e){
            logger.info(userName+"下线的时候通知所有人发生了错误");
        }
        //logger.info("有连接关闭！ 当前在线人数" + onlineNumber);
        logger.info("有连接关闭！ 当前在线人数" + clients.size());
    }

    /**
     * 收到客户端的消息
     *
     * @param message 消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session)
    {
        try {
            logger.info("来自客户端消息：" + message+"客户端的id是："+session.getId());

            System.out.println("------------  :"+message);

            JSONObject jsonObject = JSON.parseObject(message);
            String textMessage = jsonObject.getString("message");
            String fromuserName = jsonObject.getString("userName");
            String touserName = jsonObject.getString("to");
            //如果不是发给所有，那么就发给某一个人
            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
            Map<String,Object> map1 = Maps.newHashMap();
            map1.put("messageType",4);
            map1.put("textMessage",textMessage);
            map1.put("fromuserName",fromuserName);
            if(touserName.equals("All")){
                map1.put("touserName","所有人");
                sendMessageAll(JSON.toJSONString(map1),fromuserName);
            }
            else{
                map1.put("touserName",touserName);
                System.out.println("开始推送消息给"+touserName);
                sendMessageTo(JSON.toJSONString(map1),touserName);
            }
        }
        catch (Exception e){

            e.printStackTrace();
            logger.info("发生了错误了");
        }

    }

    //私发消息
    public void sendMessageTo(String message, String TouserName) throws IOException {
        for (WebSocket item : clients.values()) {
            //    System.out.println("在线人员名单  ："+item.userName.toString());
            if (item.userName.equals(TouserName) ) {
                item.session.getAsyncRemote().sendText(message);
                break;
            }
        }
    }

    //发送给所有人
    public void sendMessageAll(String message,String FromuserName) throws IOException {
        for (WebSocket item : clients.values()) {
            item.session.getAsyncRemote().sendText(message);
        }
    }

    //获取在线人数
    public static synchronized int getOnlineCount() {
        return onlineNumber;
    }
}
