/**
 * 
 */
package com.sunyard.cop.IF.spring.websocket;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * 功能说明：websocket连接的拦截器
 * 有两种方式
 *          一种是实现接口HandshakeInterceptor，实现beforeHandshake和afterHandshake函数
 *          一种是继承HttpSessionHandshakeInterceptor，重载beforeHandshake和afterHandshake函数
 * 参照spring官方文档中的继承HttpSessionHandshakeInterceptor的方式
 * @author YZ
 * 2017年1月16日  上午10:23:50
 */
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    private Logger logger = LoggerFactory.getLogger(WebSocketHandshakeInterceptor.class);
    
    public WebSocketHandshakeInterceptor() {
    	System.out.println("aaaaaaaaaaaaaaaa");
    }
    
    /**
     * 从请求中获取唯一标记参数，填充到数据传递容器attributes
     * @param serverHttpRequest
     * @param serverHttpResponse
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    	if (getSession(serverHttpRequest) != null) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) serverHttpRequest;
            HttpServletRequest request = servletRequest.getServletRequest();
            if(request.getParameter("webSocketId") != null && request.getParameter("loginTag") != null){
            	String webSocketId = request.getParameter("webSocketId");
            	String loginTag = request.getParameter("loginTag");
            	String bankNo=request.getParameter("bankNo");
            	String systemNo=request.getParameter("systemNo");
            	String projectNo=request.getParameter("projectNo");
            	attributes.put("webSocketId", webSocketId);
                attributes.put("loginTag", loginTag);
                attributes.put("bankNo", bankNo);
                attributes.put("systemNo", systemNo);
                attributes.put("projectNo", projectNo);
                logger.info("webSocketId: "+webSocketId+" loginTag: "+loginTag+" 尝试Websocket连接");
            }else{
            	return false;
            }
        }
        super.beforeHandshake(serverHttpRequest, serverHttpResponse, wsHandler, attributes);
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }

    private HttpSession getSession(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
            return serverRequest.getServletRequest().getSession(false);
        }
        return null;
    }

}