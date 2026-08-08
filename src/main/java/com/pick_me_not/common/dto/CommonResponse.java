package com.pick_me_not.common.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommonResponse<T> {

	private final int status;
	private final String message;
	private final T result;
	private final LocalDateTime timestamp;
}
