package com.pick_me_not.common.websocket;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketConnectionRegistry {

	private final Map<String, WebSocketIdentity> connections = new ConcurrentHashMap<>();
	private final Map<Long, Integer> connectionCounts = new ConcurrentHashMap<>();

	public synchronized ConnectionChange register(String sessionId, WebSocketIdentity identity) {
		WebSocketIdentity existing = connections.putIfAbsent(sessionId, identity);
		if (existing != null) {
			return new ConnectionChange(existing, false);
		}

		int count = connectionCounts.merge(identity.getParticipantId(), 1, Integer::sum);
		return new ConnectionChange(identity, count == 1);
	}

	public synchronized Optional<ConnectionChange> unregister(String sessionId) {
		WebSocketIdentity identity = connections.remove(sessionId);
		if (identity == null) {
			return Optional.empty();
		}

		int remaining = connectionCounts.computeIfPresent(identity.getParticipantId(),
				(participantId, count) -> count > 1 ? count - 1 : null) == null ? 0
				: connectionCounts.get(identity.getParticipantId());
		return Optional.of(new ConnectionChange(identity, remaining == 0));
	}

	@Getter
	@RequiredArgsConstructor
	public static class ConnectionChange {
		private final WebSocketIdentity identity;
		private final boolean stateChanged;
	}
}
