package com.pick_me_not.common.websocket;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WebSocketIdentity {

	private final Long participantId;
	private final String roomCode;
}
