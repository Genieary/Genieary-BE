package com.hongik.genieary.config;

import com.hongik.genieary.auth.jwt.JwtUtil;
import com.hongik.genieary.auth.service.CustomUserDetails;
import com.hongik.genieary.auth.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtWebSocketInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticateUser(accessor);
        }

        return message;
    }

    private void authenticateUser(StompHeaderAccessor accessor) {
        List<String> authorizationHeaders = accessor.getNativeHeader("Authorization");

        if (authorizationHeaders != null && !authorizationHeaders.isEmpty()) {
            String token = authorizationHeaders.get(0);

            if (token.startsWith("Bearer ")) {
                token = token.substring(7);

                try {
                    if (jwtUtil.validateToken(token)) {
                        String email = jwtUtil.getEmailFromToken(token);
                        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(email);

                        // Simple Principal 구현
                        SimplePrincipal principal = new SimplePrincipal(userDetails.getId().toString());
                        accessor.setUser(principal);

                        log.info("WebSocket authentication successful for user: {}", userDetails.getId());
                    } else {
                        throw new IllegalArgumentException("Invalid token");
                    }
                } catch (Exception e) {
                    log.error("WebSocket authentication failed: {}", e.getMessage());
                    throw new IllegalArgumentException("Authentication failed");
                }
            }
        } else {
            throw new IllegalArgumentException("No authorization header");
        }
    }

    // 간단한 Principal 구현
    private static class SimplePrincipal implements Principal {
        private final String userId;

        public SimplePrincipal(String userId) {
            this.userId = userId;
        }

        @Override
        public String getName() {
            return userId;
        }
    }
}
