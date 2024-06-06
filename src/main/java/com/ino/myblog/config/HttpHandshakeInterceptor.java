/*
package com.ino.myblog.config;

import com.ino.myblog.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.crypto.SecretKey;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

public class HttpHandshakeInterceptor implements HandshakeInterceptor {
    private static final String SECRET_KEY = "YourSecretKeyForJWT"; // JWT 서명에 사용할 비밀키

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpSession session = servletRequest.getServletRequest().getSession(false);
            if (session != null) {
                User user = (User) session.getAttribute("user");
                if (user != null) {
                    attributes.put("userId", user.getId());
                }
            }
        }
            String authToken = request.getURI().getQuery().split("token=")[1];
            System.out.println("authToken = " + authToken);
            if (authToken != null && authToken.startsWith("Bearer ")) {
                String jwtToken = authToken.substring(7); // "Bearer " 제거
                try {
                    // JWT 토큰 파싱 및 검증
                    SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
                    Jws<Claims> parsedToken = Jwts.parserBuilder()
                            .setSigningKey(key)
                            .build()
                            .parseClaimsJws(jwtToken);

                    // 토큰에서 사용자 정보 추출 (예: 사용자 이름)
                    String username = parsedToken.getBody().getSubject();

                    // Authentication 객체 생성 (이 부분은 프로젝트에 맞게 수정)
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            username, null, Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                    response.setStatusCode(HttpStatus.UNAUTHORIZED);
                    return false; // 핸드셰이크 중단
                }
            }

            return true;
        }



    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Do something after handshake
    }
}
*/
