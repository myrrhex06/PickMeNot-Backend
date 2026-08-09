package com.pick_me_not.common.websocket;

import com.pick_me_not.common.exception.RoomAccessDeniedException;
import com.pick_me_not.common.util.ParticipantTokenManager;
import com.pick_me_not.persistence.entity.Participant;
import com.pick_me_not.persistence.entity.ParticipantSession;
import com.pick_me_not.persistence.repository.ParticipantSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipantSessionAuthenticator {

	private final ParticipantSessionRepository participantSessionRepository;
	private final ParticipantTokenManager participantTokenManager;

	public WebSocketIdentity authenticate(String token) {
		if (token == null || token.isBlank()) {
			throw new RoomAccessDeniedException();
		}

		Participant participant = participantSessionRepository.findActiveSession(
				participantTokenManager.hash(token), LocalDateTime.now(ZoneOffset.UTC))
				.map(ParticipantSession::getParticipant)
				.orElseThrow(RoomAccessDeniedException::new);

		return WebSocketIdentity.builder()
				.participantId(participant.getId())
				.roomCode(participant.getRoom().getRoomCode())
				.build();
	}

	public WebSocketIdentity authenticate(String roomCode, String token) {
		WebSocketIdentity identity = authenticate(token);
		if (!identity.getRoomCode().equals(normalizeRoomCode(roomCode))) {
			throw new RoomAccessDeniedException();
		}
		return identity;
	}

	private String normalizeRoomCode(String roomCode) {
		return roomCode.trim().toUpperCase();
	}
}
