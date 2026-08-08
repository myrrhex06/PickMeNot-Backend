package com.pick_me_not.room.dto;

import com.pick_me_not.persistence.entity.Room;
import com.pick_me_not.common.enums.RoomStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoomResponse {

	private final String roomCode;
	private final RoomStatus status;
	private final LocalDateTime createdAt;
	private final LocalDateTime expiresAt;
	private final long participantCount;

	public static RoomResponse from(Room room, long participantCount) {
		return RoomResponse.builder()
				.roomCode(room.getRoomCode())
				.status(room.getStatus())
				.createdAt(room.getCreatedAt())
				.expiresAt(room.getExpiresAt())
				.participantCount(participantCount)
				.build();
	}
}
