package com.pick_me_not.room.dto;

import com.pick_me_not.common.enums.RoomStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class JoinRoomResponse {

	private final String roomCode;
	private final RoomStatus status;
	private final Long participantId;
	private final String nickname;
	private final boolean host;
	private final String participantToken;
	private final LocalDateTime expiresAt;
}
