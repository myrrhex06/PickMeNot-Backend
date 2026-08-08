package com.pick_me_not.common.util;

import com.pick_me_not.common.dto.CommonResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseUtil {

	public static <T> ResponseEntity<CommonResponse<T>> success(
			HttpStatusCode status,
			String message,
			T result
	) {
		CommonResponse<T> response = CommonResponse.<T>builder()
				.status(status.value())
				.message(message)
				.result(result)
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(status).body(response);
	}

	public static ResponseEntity<CommonResponse<Void>> success(
			HttpStatusCode status,
			String message
	) {
		return success(status, message, null);
	}

	public static ResponseEntity<CommonResponse<Void>> success(String message) {
		return success(HttpStatus.OK, message, null);
	}

	public static ResponseEntity<CommonResponse<Void>> fail(
			HttpStatus status,
			String message
	) {
		CommonResponse<Void> response = CommonResponse.<Void>builder()
				.status(status.value())
				.message(message)
				.result(null)
				.timestamp(LocalDateTime.now())
				.build();

		return ResponseEntity.status(status).body(response);
	}

	public static ResponseEntity<CommonResponse<Void>> fail(String message) {
		return fail(HttpStatus.BAD_REQUEST, message);
	}
}
