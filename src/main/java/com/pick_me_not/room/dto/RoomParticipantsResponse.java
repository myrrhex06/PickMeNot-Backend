package com.pick_me_not.room.dto;

import com.pick_me_not.common.enums.RoomStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RoomParticipantsResponse {

	private final String roomCode;
	private final RoomStatus status;
	private final List<ParticipantResponse> participants;
}
