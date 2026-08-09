package com.pick_me_not.room.service;

import com.pick_me_not.persistence.entity.Participant;
import com.pick_me_not.persistence.repository.ParticipantRepository;
import com.pick_me_not.persistence.entity.Room;
import com.pick_me_not.persistence.repository.RoomRepository;
import com.pick_me_not.common.enums.RoomStatus;
import com.pick_me_not.persistence.entity.ParticipantSession;
import com.pick_me_not.persistence.repository.ParticipantSessionRepository;
import com.pick_me_not.common.exception.RoomAccessDeniedException;
import com.pick_me_not.common.exception.RoomNotFoundException;
import com.pick_me_not.common.websocket.ParticipantSessionAuthenticator;
import com.pick_me_not.room.dto.CreateRoomRequest;
import com.pick_me_not.room.dto.CreateRoomResponse;
import com.pick_me_not.room.dto.JoinRoomRequest;
import com.pick_me_not.room.dto.JoinRoomResponse;
import com.pick_me_not.room.dto.RoomResponse;
import com.pick_me_not.room.dto.ParticipantResponse;
import com.pick_me_not.room.dto.RoomParticipantsResponse;
import com.pick_me_not.room.realtime.RoomEvent;
import com.pick_me_not.room.realtime.RoomEventType;
import com.pick_me_not.common.util.ParticipantTokenManager;
import com.pick_me_not.common.util.RoomCodeGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomService {

	private static final int ROOM_CODE_GENERATION_ATTEMPTS = 10;
	private static final long ROOM_LIFETIME_HOURS = 24;

	private final RoomRepository roomRepository;
	private final ParticipantRepository participantRepository;
	private final ParticipantSessionRepository participantSessionRepository;
	private final RoomCodeGenerator roomCodeGenerator;
	private final ParticipantTokenManager participantTokenManager;
	private final ParticipantSessionAuthenticator participantSessionAuthenticator;
	private final ApplicationEventPublisher eventPublisher;

	@Transactional
	public CreateRoomResponse create(CreateRoomRequest request) {
		LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
		LocalDateTime expiresAt = now.plusHours(ROOM_LIFETIME_HOURS);
		Room room = roomRepository.save(Room.create(generateUniqueRoomCode(), now, expiresAt));
		Participant host = participantRepository.save(
				Participant.createHost(room, request.getNickname().trim(), now));

		String rawToken = participantTokenManager.generate();
		participantSessionRepository.save(ParticipantSession.issue(
				host, participantTokenManager.hash(rawToken), now, expiresAt));

		return CreateRoomResponse.builder()
				.roomCode(room.getRoomCode())
				.status(room.getStatus())
				.participantId(host.getId())
				.nickname(host.getNickname())
				.participantToken(rawToken)
				.expiresAt(room.getExpiresAt())
				.build();
	}

	public RoomResponse get(String roomCode) {
		Room room = findRoom(roomCode);
		return RoomResponse.from(room, participantRepository.countByRoomId(room.getId()));
	}

	@Transactional
	public JoinRoomResponse join(String roomCode, JoinRoomRequest request) {
		LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
		Room room = roomRepository.findByRoomCodeForUpdate(normalizeRoomCode(roomCode))
				.orElseThrow(() -> new RoomNotFoundException(roomCode));
		room.validateJoinable(now);

		String nickname = request.getNickname().trim();
		if (participantRepository.existsByRoomIdAndNickname(room.getId(), nickname)) {
			throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
		}

		Participant participant = participantRepository.save(Participant.join(room, nickname, now));
		String rawToken = participantTokenManager.generate();
		participantSessionRepository.save(ParticipantSession.issue(
				participant, participantTokenManager.hash(rawToken), now, room.getExpiresAt()));
		publishParticipantEvent(RoomEventType.PARTICIPANT_JOINED, participant, now);
		return JoinRoomResponse.builder()
				.roomCode(room.getRoomCode())
				.status(room.getStatus())
				.participantId(participant.getId())
				.nickname(participant.getNickname())
				.host(participant.isHost())
				.participantToken(rawToken)
				.expiresAt(room.getExpiresAt())
				.build();
	}

	public RoomParticipantsResponse getParticipants(String roomCode, String token) {
		participantSessionAuthenticator.authenticate(roomCode, token);
		Room room = findRoom(roomCode);
		List<ParticipantResponse> participants = participantRepository
				.findAllByRoomIdOrderByJoinedAtAsc(room.getId()).stream()
				.map(ParticipantResponse::from)
				.toList();
		return RoomParticipantsResponse.builder()
				.roomCode(room.getRoomCode())
				.status(room.getStatus())
				.participants(participants)
				.build();
	}

	@Transactional
	public RoomResponse updateStatus(String roomCode, String token, RoomStatus status) {
		Room room = authorizeHost(roomCode, token);
		room.changeStatus(status);
		long participantCount = participantRepository.countByRoomId(room.getId());
		publishRoomEvent(RoomEventType.ROOM_STATUS_CHANGED, room, participantCount);
		return RoomResponse.from(room, participantCount);
	}

	@Transactional
	public void close(String roomCode, String token) {
		Room room = authorizeHost(roomCode, token);
		room.close();
		publishRoomEvent(RoomEventType.ROOM_CLOSED, room, participantRepository.countByRoomId(room.getId()));
	}

	private Room authorizeHost(String roomCode, String token) {
		if (token == null || token.isBlank()) {
			throw new RoomAccessDeniedException();
		}
		return participantSessionRepository.findActiveHostSession(
				normalizeRoomCode(roomCode), participantTokenManager.hash(token), LocalDateTime.now(ZoneOffset.UTC))
				.map(session -> session.getParticipant().getRoom())
				.orElseThrow(RoomAccessDeniedException::new);
	}

	private Room findRoom(String roomCode) {
		return roomRepository.findByRoomCode(normalizeRoomCode(roomCode))
				.orElseThrow(() -> new RoomNotFoundException(roomCode));
	}

	private String generateUniqueRoomCode() {
		for (int attempt = 0; attempt < ROOM_CODE_GENERATION_ATTEMPTS; attempt++) {
			String roomCode = roomCodeGenerator.generate();
			if (!roomRepository.existsByRoomCode(roomCode)) {
				return roomCode;
			}
		}
		throw new IllegalStateException("고유한 방 코드를 생성하지 못했습니다.");
	}

	private String normalizeRoomCode(String roomCode) {
		return roomCode.trim().toUpperCase();
	}

	private void publishParticipantEvent(RoomEventType type, Participant participant, LocalDateTime occurredAt) {
		eventPublisher.publishEvent(RoomEvent.builder()
				.type(type)
				.roomCode(participant.getRoom().getRoomCode())
				.occurredAt(occurredAt)
				.roomStatus(participant.getRoom().getStatus())
				.participantCount(participantRepository.countByRoomId(participant.getRoom().getId()))
				.participant(ParticipantResponse.from(participant))
				.build());
	}

	private void publishRoomEvent(RoomEventType type, Room room, long participantCount) {
		eventPublisher.publishEvent(RoomEvent.builder()
				.type(type)
				.roomCode(room.getRoomCode())
				.occurredAt(LocalDateTime.now(ZoneOffset.UTC))
				.roomStatus(room.getStatus())
				.participantCount(participantCount)
				.build());
	}
}
