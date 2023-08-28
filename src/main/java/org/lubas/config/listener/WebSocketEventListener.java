package org.lubas.config.listener;

import org.lubas.domain.entity.AppUser;
import org.lubas.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final UserService userService;

    @Autowired
    public WebSocketEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("User");

        // Add debug logging
        System.out.println("Disconnect Event - Username: " + username);

        if (username != null) {
            AppUser appUser = userService.loadUserByUsername(username);

            // Add more debug logging
            if (appUser != null) {
                System.out.println("Disconnect Event - User Last Seen At: " + appUser.getLastSeenAt());
            } else {
                System.out.println("Disconnect Event - User is null");
            }

            assert appUser != null;
            userService.updateLastSeenAt(appUser, "Offline");
        }
    }
}
