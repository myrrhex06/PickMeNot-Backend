package com.pick_me_not.common.websocket;

import com.pick_me_not.common.exception.RoomAccessDeniedException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketChannelInterceptor implements ChannelInterceptor {

	public static final String PARTICIPANT_TOKEN_HEADER = "X-Participant-Token";
	public static final String IDENTITY_ATTRIBUTE = "webSocketIdentity";
	private static final String ROOM_TOPIC_PREFIX = "/topic/rooms/";

	private final ParticipantSessionAuthenticator participantSessionAuthenticator;

	@Override
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();
		if (command == null) {
			return message;
		}

		try {
			if (command == StompCommand.CONNECT) {
				authenticate(accessor);
			} else if (command == StompCommand.SUBSCRIBE) {
				authorizeSubscription(accessor);
			} else if (command == StompCommand.SEND) {
				throw new RoomAccessDeniedException();
			}
			return message;
		} catch (RoomAccessDeniedException exception) {
			throw new MessageDeliveryException("WebSocket 요청 권한이 없습니다.");
		}
	}

	private void authenticate(StompHeaderAccessor accessor) {
		WebSocketIdentity identity = participantSessionAuthenticator.authenticate(
				accessor.getFirstNativeHeader(PARTICIPANT_TOKEN_HEADER));
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		if (sessionAttributes == null) {
			throw new RoomAccessDeniedException();
		}
		sessionAttributes.put(IDENTITY_ATTRIBUTE, identity);
		accessor.setUser(new WebSocketPrincipal(identity.getParticipantId().toString()));
	}

	private void authorizeSubscription(StompHeaderAccessor accessor) {
		String destination = accessor.getDestination();
		WebSocketIdentity identity = identity(accessor);
		String expectedDestination = ROOM_TOPIC_PREFIX + identity.getRoomCode();
		if (!expectedDestination.equals(destination)) {
			throw new RoomAccessDeniedException();
		}
	}

	private WebSocketIdentity identity(StompHeaderAccessor accessor) {
		Map<String, Object> sessionAttributes = accessor.getSessionAttributes();
		if (sessionAttributes == null) {
			throw new RoomAccessDeniedException();
		}
		Object identity = sessionAttributes.get(IDENTITY_ATTRIBUTE);
		if (!(identity instanceof WebSocketIdentity webSocketIdentity)) {
			throw new RoomAccessDeniedException();
		}
		return webSocketIdentity;
	}
}
