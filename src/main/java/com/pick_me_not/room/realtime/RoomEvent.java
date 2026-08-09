package com.pick_me_not.room.realtime;

import com.pick_me_not.common.enums.RoomStatus;
import com.pick_me_not.room.dto.ParticipantResponse;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class RoomEvent {

	private final RoomEventType type;
	private final String roomCode;
	private final LocalDateTime occurredAt;
	private final RoomStatus roomStatus;
	private final long participantCount;
	private final ParticipantResponse participant;
}
