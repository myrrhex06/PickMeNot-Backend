package com.pick_me_not.common.websocket;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

@RequiredArgsConstructor
public class WebSocketPrincipal implements Principal {

	private final String name;

	@Override
	public String getName() {
		return name;
	}
}
