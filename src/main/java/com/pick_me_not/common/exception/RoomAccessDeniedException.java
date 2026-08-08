package com.pick_me_not.common.exception;

public class RoomAccessDeniedException extends RuntimeException {

	public RoomAccessDeniedException() {
		super("방장 권한이 필요합니다.");
	}
}
