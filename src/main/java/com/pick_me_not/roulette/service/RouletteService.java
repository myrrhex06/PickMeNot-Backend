package com.pick_me_not.roulette.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pick_me_not.common.enums.RoomStatus;
import com.pick_me_not.common.exception.RoomAccessDeniedException;
import com.pick_me_not.common.exception.RoomNotFoundException;
import com.pick_me_not.common.util.ParticipantTokenManager;
import com.pick_me_not.persistence.entity.Participant;
import com.pick_me_not.persistence.entity.Penalty;
import com.pick_me_not.persistence.entity.Room;
import com.pick_me_not.persistence.entity.RouletteRound;
import com.pick_me_not.persistence.repository.ParticipantSessionRepository;
import com.pick_me_not.persistence.repository.PenaltyRepository;
import com.pick_me_not.persistence.repository.RoomRepository;
import com.pick_me_not.persistence.repository.RouletteRoundRepository;
import com.pick_me_not.room.realtime.RoomEvent;
import com.pick_me_not.room.realtime.RoomEventType;
import com.pick_me_not.roulette.dto.RoulettePenaltyResponse;
import com.pick_me_not.roulette.dto.RouletteRoundResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RouletteService {

	private static final int ROULETTE_DURATION_MS = 5_000;

	private final RoomRepository roomRepository;
	private final ParticipantSessionRepository participantSessionRepository;
	private final PenaltyRepository penaltyRepository;
	private final RouletteRoundRepository rouletteRoundRepository;
	private final ParticipantTokenManager participantTokenManager;
	private final ObjectMapper objectMapper;
	private final ApplicationEventPublisher eventPublisher;
	private final SecureRandom random = new SecureRandom();

	@Transactional
	public RouletteRoundResponse start(String roomCode, String token) {
		String normalizedRoomCode = roomCode.trim().toUpperCase();
		LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
		Room room = roomRepository.findByRoomCodeForUpdate(normalizedRoomCode)
				.orElseThrow(() -> new RoomNotFoundException(roomCode));
		Participant executor = authorizeHost(normalizedRoomCode, token, now);

		if (room.getStatus() != RoomStatus.PLAYING) {
			throw new IllegalStateException("게임 진행 중인 방에서만 룰렛을 돌릴 수 있습니다.");
		}
		validateNoRoundInProgress(room, now);

		List<Penalty> penalties = penaltyRepository.findAllByRoomIdAndActiveTrueOrderByIdAsc(room.getId());
		if (penalties.isEmpty()) {
			throw new IllegalStateException("활성화된 벌칙이 없습니다.");
		}

		List<RoulettePenaltyResponse> penaltyResponses = penalties.stream()
				.map(RoulettePenaltyResponse::from)
				.toList();
		int selectedIndex = random.nextInt(penalties.size());
		Penalty selectedPenalty = penalties.get(selectedIndex);
		RouletteRound round = rouletteRoundRepository.save(RouletteRound.start(
				room,
				executor,
				selectedPenalty,
				serializeSnapshot(penaltyResponses),
				now,
				ROULETTE_DURATION_MS));

		RouletteRoundResponse response = RouletteRoundResponse.builder()
				.roundId(round.getId())
				.penalties(penaltyResponses)
				.selectedIndex(selectedIndex)
				.selectedPenalty(penaltyResponses.get(selectedIndex))
				.startedAt(now)
				.durationMs(ROULETTE_DURATION_MS)
				.build();

		eventPublisher.publishEvent(RoomEvent.builder()
				.type(RoomEventType.ROULETTE_STARTED)
				.roomCode(room.getRoomCode())
				.occurredAt(now)
				.roomStatus(room.getStatus())
				.rouletteRound(response)
				.build());
		return response;
	}

	private Participant authorizeHost(String roomCode, String token, LocalDateTime now) {
		if (token == null || token.isBlank()) {
			throw new RoomAccessDeniedException();
		}
		return participantSessionRepository.findActiveHostSession(
				roomCode, participantTokenManager.hash(token), now)
				.map(session -> session.getParticipant())
				.orElseThrow(RoomAccessDeniedException::new);
	}

	private void validateNoRoundInProgress(Room room, LocalDateTime now) {
		rouletteRoundRepository.findFirstByRoomIdOrderByStartedAtDesc(room.getId())
				.filter(round -> round.getStartedAt().plusNanos(round.getDurationMs() * 1_000_000L).isAfter(now))
				.ifPresent(round -> {
					throw new IllegalStateException("이미 룰렛이 진행 중입니다.");
				});
	}

	private String serializeSnapshot(List<RoulettePenaltyResponse> penalties) {
		try {
			return objectMapper.writeValueAsString(penalties);
		} catch (JsonProcessingException exception) {
			throw new IllegalStateException("룰렛 스냅샷을 생성하지 못했습니다.", exception);
		}
	}
}
