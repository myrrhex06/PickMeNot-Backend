package com.pick_me_not.room.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(description = "방 생성 요청")
public class CreateRoomRequest {

	@Schema(description = "호스트 닉네임", example = "홍길동", maxLength = 30, requiredMode = Schema.RequiredMode.REQUIRED)
	@NotBlank(message = "닉네임은 필수입니다.")
	@Size(max = 30, message = "닉네임은 30자 이하여야 합니다.")
	private String nickname;
}
