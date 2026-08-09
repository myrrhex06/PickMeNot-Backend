package com.pick_me_not.room.dto;

import com.pick_me_not.persistence.entity.Participant;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ParticipantResponse {

	private final Long participantId;
	private final String nickname;
	private final boolean host;
	private final boolean connected;
	private final LocalDateTime lastSeenAt;

	public static ParticipantResponse from(Participant participant) {
		return ParticipantResponse.builder()
				.participantId(participant.getId())
				.nickname(participant.getNickname())
				.host(participant.isHost())
				.connected(participant.isConnected())
				.lastSeenAt(participant.getLastSeenAt())
				.build();
	}
}
