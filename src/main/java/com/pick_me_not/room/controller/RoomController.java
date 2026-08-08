package com.pick_me_not.room.controller;

import com.pick_me_not.common.config.OpenApiConfig;
import com.pick_me_not.common.dto.CommonResponse;
import com.pick_me_not.common.util.ResponseUtil;
import com.pick_me_not.room.service.RoomService;
import com.pick_me_not.room.dto.CreateRoomRequest;
import com.pick_me_not.room.dto.CreateRoomResponse;
import com.pick_me_not.room.dto.RoomResponse;
import com.pick_me_not.room.dto.UpdateRoomRequest;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
@Tag(name = "Room", description = "방 생성, 조회, 상태 변경 및 종료 API")
public class RoomController {

	private static final String PARTICIPANT_TOKEN_HEADER = "X-Participant-Token";

	private final RoomService roomService;

	@PostMapping
	@Operation(summary = "방 생성", description = "호스트 닉네임으로 방을 생성하고 호스트 인증용 참가자 토큰을 발급합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "201", description = "방 생성 성공"),
		@ApiResponse(responseCode = "400", description = "요청 검증 실패",
					content = @Content(schema = @Schema(implementation = CommonResponse.class))),
		@ApiResponse(responseCode = "409", description = "방 코드 생성 실패",
					content = @Content(schema = @Schema(implementation = CommonResponse.class)))
	})
	public ResponseEntity<CommonResponse<CreateRoomResponse>> create(@Valid @RequestBody CreateRoomRequest request) {
		CreateRoomResponse response = roomService.create(request);
		return ResponseUtil.success(HttpStatus.CREATED, "방 생성에 성공했습니다.", response);
	}

	@GetMapping("/{roomCode}")
	@Operation(summary = "방 조회", description = "방 코드로 방 상태와 현재 참가자 수를 조회합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "방 조회 성공"),
		@ApiResponse(responseCode = "404", description = "방을 찾을 수 없음",
					content = @Content(schema = @Schema(implementation = CommonResponse.class)))
	})
	public ResponseEntity<CommonResponse<RoomResponse>> get(
			@Parameter(description = "방 코드", example = "A1B2C3", in = ParameterIn.PATH, required = true)
			@PathVariable String roomCode) {
		RoomResponse response = roomService.get(roomCode);
		return ResponseUtil.success(HttpStatus.OK, "방 조회에 성공했습니다.", response);
	}

	@PatchMapping("/{roomCode}")
	@Operation(summary = "방 상태 변경", description = "호스트가 방의 상태를 변경합니다.")
	@SecurityRequirement(name = OpenApiConfig.PARTICIPANT_TOKEN_SCHEME)
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "방 상태 변경 성공"),
		@ApiResponse(responseCode = "400", description = "요청 검증 실패",
					content = @Content(schema = @Schema(implementation = CommonResponse.class))),
		@ApiResponse(responseCode = "403", description = "호스트 권한 없음",
					content = @Content(schema = @Schema(implementation = CommonResponse.class))),
		@ApiResponse(responseCode = "409", description = "종료된 방의 상태 변경 시도",
					content = @Content(schema = @Schema(implementation = CommonResponse.class)))
	})
	public ResponseEntity<CommonResponse<RoomResponse>> updateStatus(
			@Parameter(description = "방 코드", example = "A1B2C3", in = ParameterIn.PATH, required = true)
			@PathVariable String roomCode,
			@Parameter(hidden = true) @RequestHeader(PARTICIPANT_TOKEN_HEADER) String participantToken,
			@Valid @RequestBody UpdateRoomRequest request) {
		RoomResponse response = roomService.updateStatus(roomCode, participantToken, request.getStatus());
		return ResponseUtil.success(HttpStatus.OK, "방 상태 변경에 성공했습니다.", response);
	}

	@DeleteMapping("/{roomCode}")
	@Operation(summary = "방 종료", description = "호스트가 방을 종료 상태로 변경합니다.")
	@SecurityRequirement(name = OpenApiConfig.PARTICIPANT_TOKEN_SCHEME)
	@ApiResponses({
		@ApiResponse(responseCode = "204", description = "방 종료 성공"),
		@ApiResponse(responseCode = "403", description = "호스트 권한 없음",
					content = @Content(schema = @Schema(implementation = CommonResponse.class)))
	})
	public ResponseEntity<CommonResponse<Void>> close(
			@Parameter(description = "방 코드", example = "A1B2C3", in = ParameterIn.PATH, required = true)
			@PathVariable String roomCode,
			@Parameter(hidden = true) @RequestHeader(PARTICIPANT_TOKEN_HEADER) String participantToken) {
		roomService.close(roomCode, participantToken);
		return ResponseUtil.success(HttpStatus.NO_CONTENT, "방 종료에 성공했습니다.");
	}
}
