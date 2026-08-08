package com.pick_me_not.common.util;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class RoomCodeGenerator {

	private static final char[] CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();
	private static final int CODE_LENGTH = 6;
	private final SecureRandom secureRandom = new SecureRandom();

	public String generate() {
		StringBuilder code = new StringBuilder(CODE_LENGTH);
		for (int i = 0; i < CODE_LENGTH; i++) {
			code.append(CHARACTERS[secureRandom.nextInt(CHARACTERS.length)]);
		}
		return code.toString();
	}
}
