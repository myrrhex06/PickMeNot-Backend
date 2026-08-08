package com.pick_me_not.common.exception.handler;

import com.pick_me_not.common.dto.CommonResponse;
import com.pick_me_not.common.exception.RoomAccessDeniedException;
import com.pick_me_not.common.exception.RoomNotFoundException;
import com.pick_me_not.common.util.ResponseUtil;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RoomNotFoundException.class)
	public ResponseEntity<CommonResponse<Void>> handleNotFound(RoomNotFoundException exception) {
		return ResponseUtil.fail(HttpStatus.NOT_FOUND, exception.getMessage());
	}

	@ExceptionHandler(RoomAccessDeniedException.class)
	public ResponseEntity<CommonResponse<Void>> handleAccessDenied(RoomAccessDeniedException exception) {
		return ResponseUtil.fail(HttpStatus.FORBIDDEN, exception.getMessage());
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<CommonResponse<Void>> handleConflict(IllegalStateException exception) {
		return ResponseUtil.fail(HttpStatus.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<CommonResponse<Void>> handleValidation(MethodArgumentNotValidException exception) {
		String detail = exception.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(DefaultMessageSourceResolvable::getDefaultMessage)
				.orElse("요청 값이 올바르지 않습니다.");
		return ResponseUtil.fail(HttpStatus.BAD_REQUEST, detail);
	}
}
