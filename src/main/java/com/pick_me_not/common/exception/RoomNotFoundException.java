package com.pick_me_not.common.exception;

public class RoomNotFoundException extends RuntimeException {

	public RoomNotFoundException(String roomCode) {
		super("방을 찾을 수 없습니다: " + roomCode);
	}
}
