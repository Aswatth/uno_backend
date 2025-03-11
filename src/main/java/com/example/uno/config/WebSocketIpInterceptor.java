package com.example.uno.config;

import com.example.uno.models.ConnectionData;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

/**
 * An interceptor class to derive client IP-Address and Port.
 */
public class WebSocketIpInterceptor implements HandshakeInterceptor {

  @Override
  public boolean beforeHandshake(
      org.springframework.http.server.ServerHttpRequest request,
      org.springframework.http.server.ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Map<String, Object> attributes) {

    if (request instanceof ServletServerHttpRequest) {
      ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;

      String clientIp = servletRequest.getServletRequest().getRemoteAddr();
      int clientPort = servletRequest.getServletRequest().getRemotePort();

      ConnectionData connectionData = new ConnectionData(clientIp, clientPort);

      attributes.put("connectionData", connectionData);
    }
    return true;
  }

  @Override
  public void afterHandshake(
      org.springframework.http.server.ServerHttpRequest request,
      org.springframework.http.server.ServerHttpResponse response,
      WebSocketHandler wsHandler,
      Exception exception) {
    // No action needed after handshake
  }
}
