package com.pick_me_not.room.realtime;

import com.pick_me_not.common.websocket.WebSocketConnectionRegistry;
import com.pick_me_not.common.websocket.WebSocketIdentity;
import com.pick_me_not.persistence.entity.Participant;
import com.pick_me_not.persistence.repository.ParticipantRepository;
import com.pick_me_not.room.dto.ParticipantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class RoomPresenceService {

	private final WebSocketConnectionRegistry connectionRegistry;
	private final ParticipantRepository participantRepository;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public void connect(String sessionId, WebSocketIdentity identity) {
		WebSocketConnectionRegistry.ConnectionChange change = connectionRegistry.register(sessionId, identity);
		if (!change.isStateChanged()) {
			return;
		}

		try {
			Participant participant = participantRepository.findById(identity.getParticipantId())
					.orElseThrow(() -> new IllegalStateException("참가자를 찾을 수 없습니다."));
			LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
			participant.connect(now);
			publish(RoomEventType.PARTICIPANT_CONNECTED, participant, now);
		} catch (RuntimeException exception) {
			connectionRegistry.unregister(sessionId);
			throw exception;
		}
	}

	@Transactional
	public void disconnect(String sessionId) {
		connectionRegistry.unregister(sessionId)
				.filter(WebSocketConnectionRegistry.ConnectionChange::isStateChanged)
				.flatMap(change -> participantRepository.findById(change.getIdentity().getParticipantId()))
				.ifPresent(participant -> {
					LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
					participant.disconnect(now);
					publish(RoomEventType.PARTICIPANT_DISCONNECTED, participant, now);
				});
	}

	@Transactional
	@EventListener(ApplicationReadyEvent.class)
	public void resetStaleConnections() {
		participantRepository.disconnectAll();
	}

	private void publish(RoomEventType type, Participant participant, LocalDateTime occurredAt) {
		eventPublisher.publishEvent(RoomEvent.builder()
				.type(type)
				.roomCode(participant.getRoom().getRoomCode())
				.occurredAt(occurredAt)
				.roomStatus(participant.getRoom().getStatus())
				.participantCount(participantRepository.countByRoomId(participant.getRoom().getId()))
				.participant(ParticipantResponse.from(participant))
				.build());
	}
}
