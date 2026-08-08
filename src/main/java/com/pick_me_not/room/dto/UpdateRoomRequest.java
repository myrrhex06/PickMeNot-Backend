package com.pick_me_not.room.dto;

import com.pick_me_not.common.enums.RoomStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateRoomRequest {

	@NotNull(message = "방 상태는 필수입니다.")
	private RoomStatus status;
}
