/**
 * 
 */
package com.sunyard.cop.IF.spring.websocket;

import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;
import org.springframework.web.socket.handler.TextWebSocketHandler;


/**
 * 功能说明：WebSocket处理器
 * 可以继承 {@link TextWebSocketHandler}/{@link BinaryWebSocketHandler}，
 * 或者简单的实现{@link WebSocketHandler}接口
 * @author YZ
 * 2017年1月16日  上午10:20:27
 */
public class WebSocketHandler extends TextWebSocketHandler{

	private Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    /**
     * 建立连接
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    	Map map = session.getAttributes();
        String webSocketId = (String)map.get("webSocketId");
        String loginTag = (String)map.get("loginTag");
        if(WebSocketSessionUtils.hasConnection(webSocketId, loginTag)){        	
        	WebSocketSessionUtils.sendMessage(webSocketId, loginTag, "您的账号已在其他地点登陆，请注意账号密码安全");
        	WebSocketSession closeSession = WebSocketSessionUtils.get(webSocketId, loginTag);
        	closeSession.close();
        }
        WebSocketSessionUtils.add(webSocketId, loginTag, session);
        logger.info(webSocketId+"_"+loginTag+" 加入Websocket连接");
    }

    /**
     * 收到客户端消息
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
    	Map map = session.getAttributes();
        String webSocketId = (String)map.get("webSocketId");
        String loginTag = (String)map.get("loginTag");
        String bankNo = (String)map.get("bankNo");
        String systemNo = (String)map.get("systemNo");
        String projectNo = (String)map.get("projectNo");
        String msg=message.getPayload().toString();
        JSONObject json = JSONObject.fromObject(msg);  
        String type=json.getString("type");
        if("online_".equalsIgnoreCase(type)){
        	String mes=json.getString("mssage");
        	WebSocketSessionUtils.sendMessageAllOnline(mes);
        }else if("chat_one".equalsIgnoreCase(type)){//一对一聊天
        	String chatTo=json.getString("chatTo");       
        	if(WebSocketSessionUtils.hasConnection(chatTo, "2")||WebSocketSessionUtils.hasConnection(chatTo, "1")){
        		String chatcontent=json.getString("chatcontent");
        		String chatId=insertChatHistory(json,bankNo,systemNo,projectNo);
        		json.put("chatId", chatId);
        		if(WebSocketSessionUtils.hasConnection(chatTo, "1")){
            		WebSocketSessionUtils.sendMessage(chatTo, "1", json.toString());
        		}
        		if(WebSocketSessionUtils.hasConnection(chatTo, "2")){
            		WebSocketSessionUtils.sendMessage(chatTo, "2", json.toString());
        		}
        	}else{//不在线，保存到消息列表（标为未读）
        		insertChatHistory(json,bankNo,systemNo,projectNo);
        	}
        	
        }else if("chat_group".equalsIgnoreCase(type)){
  
        	 this.insertGroupHistory(json, bankNo, systemNo, projectNo);
        }
        
        
    }

    /**
     * 出现异常
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    	Map map = session.getAttributes();
        String webSocketId = (String)map.get("webSocketId");
        String loginTag = (String)map.get("loginTag");
        logger.error("Websocket 连接异常: " + WebSocketSessionUtils.getKey(webSocketId, loginTag));
        logger.error(exception.getMessage(), exception);

        WebSocketSessionUtils.remove(webSocketId, loginTag);
    }

    /**
     * 连接关闭
     * @param session
     * @param closeStatus
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	Map map = session.getAttributes();
        String webSocketId = (String)map.get("webSocketId");
        String loginTag = (String)map.get("loginTag");
        WebSocketSessionUtils.remove(webSocketId, loginTag);
        logger.info(webSocketId+"_"+loginTag+" 断开Websocket连接");
        if(WebSocketSessionUtils.hasConnection(webSocketId, "1")||WebSocketSessionUtils.hasConnection(webSocketId, "2")){        	
        }else{
        	String msg="close_"+webSocketId;
            if (WebSocketSessionUtils.getSize()!=0){
            	WebSocketSessionUtils.sendMessageAllOnline(msg);
            }
        }
    }

    /**
     * 是否分段发送消息
     * @return
     */
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
   
    /**\
     * 插入数据库（sm_history_tb）标记消息为未读
     * @param json
     * @param bankNo
     * @param systemNo
     * @param projectNo
     * @return
     */
	private String insertChatHistory(JSONObject json,String  bankNo,String systemNo,String projectNo){	
//		ChatHistory chathistory=new ChatHistory();
//        String sendTime=json.getString("sendTime");
//        String chatId=sendTime+SunIFCommonUtil.getRandomStrings(10);
//        chathistory.setChatId(chatId);
//        chathistory.setChatFrom(json.getString("chatFrom"));
//        chathistory.setChatTo(json.getString("chatTo"));
//        chathistory.setChatcontent(json.getString("chatcontent"));
//        chathistory.setStatus("0");
//        chathistory.setSendTime(sendTime);
//        chathistory.setBankNo(bankNo);
//        chathistory.setProjectNo(projectNo);
//        chathistory.setSystemNo(systemNo);
//        chatHistoryService.insertHistory(chathistory);
//        return chatId;
        return json.getString("sendTime")+getRandomStrings(10);
	}
	
	/**
	 * 群消息插入数据库（sm_group_history_tb）,将消息发送给群里面其他成员
	 */
	private void insertGroupHistory(JSONObject json,String  bankNo,String systemNo,String projectNo){
//		 GroupHistory groupHistory = new GroupHistory();
//		 String sendTime=json.getString("sendTime");
//	     String chatId=sendTime+SunIFCommonUtil.getRandomStrings(10);
//	     groupHistory.setChatId(chatId);
//	     groupHistory.setChatFrom(json.getString("chatFrom"));
//	     groupHistory.setGroupId(json.getString("chatTo"));
//	     groupHistory.setChatContent(json.getString("chatcontent"));
//	     groupHistory.setSendTime(sendTime);
//	     groupHistory.setBankNo(bankNo);
//	     groupHistory.setProjectNo(projectNo);
//	     groupHistory.setSystemNo(systemNo);
//	     grouopHistoryService.insertGroupHistory(groupHistory);
	}
	
	public static String getRandomStrings(int length)
	  {
	    StringBuffer result = new StringBuffer(length);
	    int i = 0;
	    while (i < length) {
	      int f = (int)(Math.random() * 3.0D);
	      if (f == 0)
	    	  result.append((int)(65.0D + Math.random() * 26.0D));
	      else if (f == 1)
	    	  result.append((int)(97.0D + Math.random() * 26.0D));
	      else {
	    	  result.append((int)(48.0D + Math.random() * 10.0D));
	      }
	      i++;
	    }
	    return result.toString();
	}
}
