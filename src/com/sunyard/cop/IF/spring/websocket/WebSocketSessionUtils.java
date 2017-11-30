/**
 * 
 */
package com.sunyard.cop.IF.spring.websocket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * websocket 工具类
 * @author YZ
 * 2017年1月16日  上午10:15:27
 */
public class WebSocketSessionUtils {

	private static Logger logger = LoggerFactory.getLogger(WebSocketSessionUtils.class);

    private static Map<String, WebSocketSession> clients = new ConcurrentHashMap<>();

    /**
     * 保存一个连接
     * @param webSocketId
     * @param session
     */
    public static void add(String webSocketId, String loginTag, WebSocketSession session){
        clients.put(getKey(webSocketId, loginTag), session);
    }

    /**
     * 获取一个连接
     * @param webSocketId
     * @return
     */
    public static WebSocketSession get(String webSocketId, String loginTag){
        return clients.get(getKey(webSocketId, loginTag));
    }

    /**
     * 移除一个连接
     * @param webSocketId
     */
    public static void remove(String webSocketId, String loginTag) throws IOException {
        clients.remove(getKey(webSocketId, loginTag));
    }

    /**
     * 组装sessionId
     * @param webSocketId
     * @return
     */
    public static String getKey(String webSocketId, String loginTag) {
        return webSocketId+"_"+loginTag;
    }

    /**
     * 判断是否有效连接
     * 判断是否存在
     * 判断连接是否开启
     * 无效的进行清除
     * @param webSocketId
     * @return
     */
    public static boolean hasConnection(String webSocketId, String loginTag) {
        String key = getKey(webSocketId, loginTag);
        if (clients.containsKey(key)) {
            return true;
        }
        return false;
    }

    /**
     * 获取连接数的数量
     * @return
     */
    public static int getSize() {
        return clients.size();
    }

    /**
     * 发送消息到客户端
     * @param webSocketId
     * @param message
     * @throws Exception
     */
    public static void sendMessage(String webSocketId, String loginTag, String message) throws Exception {
        if (!hasConnection(webSocketId, loginTag)) {
            throw new NullPointerException(getKey(webSocketId, loginTag) + " 连接不存在");
        }
        WebSocketSession session = get(webSocketId, loginTag);
        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
        	logger.error("Websoket发送消息异常: " + getKey(webSocketId, loginTag));
        	logger.error(e.getMessage(), e);
            clients.remove(getKey(webSocketId, loginTag));
        }
    }
    
    /**
     * 发送消息给所有在线的
     * @param message
     * @throws Exception
     */
    public static void sendMessageAllOnline(String message) throws Exception {
        if (clients.size() == 0) {
            throw new NullPointerException("当前没有用户在线");
        }
        try {
        	for (String key : clients.keySet()) { 
        		WebSocketSession session = clients.get(key); 
        		session.sendMessage(new TextMessage(message));
        	}
        } catch (IOException e) {
        	logger.error("Websoket发送消息异常: "+ message);
        	logger.error(e.getMessage(), e);
        }
    }
    
    /**
     * 发送消息给指定用户
     * @param webSocketId
     * @param message
     * @throws Exception
     */
    public static void sendMessageToAppoint(ArrayList list, String message) throws Exception {
    	if (clients.size() == 0) {
            throw new NullPointerException("当前没有用户在线");
        }
    	if(list.isEmpty()){
    		logger.warn("发送消息对象为空");
    		throw new NullPointerException("没有指定对象");
    	}
        try {
        	for (String key : clients.keySet()) { 
        		for(int i = 0, n = list.size(); i < n; i++){
        			String arrId = String.valueOf(list.get(i));
        			if(key.equals(arrId)){
        				WebSocketSession session = clients.get(key); 
                		session.sendMessage(new TextMessage(message));
        			}
        		}
        	}
        } catch (IOException e) {
        	logger.error("Websocket发送消息异常");
        	logger.error(e.getMessage(), e);
        }
    }
}
