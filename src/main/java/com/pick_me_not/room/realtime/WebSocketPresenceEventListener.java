package com.pick_me_not.room.realtime;

import com.pick_me_not.common.websocket.WebSocketChannelInterceptor;
import com.pick_me_not.common.websocket.WebSocketIdentity;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketPresenceEventListener {

	private final RoomPresenceService roomPresenceService;

	@EventListener
	public void connected(SessionConnectedEvent event) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
		WebSocketIdentity identity = identity(accessor);
		if (accessor.getSessionId() != null && identity != null) {
			roomPresenceService.connect(accessor.getSessionId(), identity);
		}
	}

	@EventListener
	public void disconnected(SessionDisconnectEvent event) {
		roomPresenceService.disconnect(event.getSessionId());
	}

	private WebSocketIdentity identity(StompHeaderAccessor accessor) {
		Map<String, Object> attributes = accessor.getSessionAttributes();
		if (attributes == null) {
			return null;
		}
		Object identity = attributes.get(WebSocketChannelInterceptor.IDENTITY_ATTRIBUTE);
		return identity instanceof WebSocketIdentity webSocketIdentity ? webSocketIdentity : null;
	}
}
