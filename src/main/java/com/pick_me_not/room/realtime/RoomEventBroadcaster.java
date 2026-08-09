package com.pick_me_not.room.realtime;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RoomEventBroadcaster {

	private static final String ROOM_TOPIC_PREFIX = "/topic/rooms/";

	private final SimpMessagingTemplate messagingTemplate;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void broadcast(RoomEvent event) {
		messagingTemplate.convertAndSend(ROOM_TOPIC_PREFIX + event.getRoomCode(), event);
	}
}
