package com.pick_me_not.roulette.controller;

import com.pick_me_not.common.config.OpenApiConfig;
import com.pick_me_not.common.dto.CommonResponse;
import com.pick_me_not.common.util.ResponseUtil;
import com.pick_me_not.roulette.dto.RouletteRoundResponse;
import com.pick_me_not.roulette.service.RouletteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms/{roomCode}/roulette-rounds")
@RequiredArgsConstructor
@Tag(name = "Roulette", description = "벌칙 룰렛 API")
public class RouletteController {

	private static final String PARTICIPANT_TOKEN_HEADER = "X-Participant-Token";

	private final RouletteService rouletteService;

	@PostMapping
	@Operation(
			summary = "벌칙 룰렛 시작",
			description = "호스트가 룰렛 결과를 확정하고 방 구독자에게 ROULETTE_STARTED 이벤트를 전파합니다.")
	@SecurityRequirement(name = OpenApiConfig.PARTICIPANT_TOKEN_SCHEME)
	public ResponseEntity<CommonResponse<RouletteRoundResponse>> start(
			@Parameter(description = "방 코드", example = "A1B2C3", in = ParameterIn.PATH, required = true)
			@PathVariable String roomCode,
			@Parameter(hidden = true) @RequestHeader(PARTICIPANT_TOKEN_HEADER) String participantToken) {
		RouletteRoundResponse response = rouletteService.start(roomCode, participantToken);
		return ResponseUtil.success(HttpStatus.CREATED, "벌칙 룰렛을 시작했습니다.", response);
	}
}
