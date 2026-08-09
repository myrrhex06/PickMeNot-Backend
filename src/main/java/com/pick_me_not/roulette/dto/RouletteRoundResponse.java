package com.pick_me_not.roulette.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "룰렛 라운드 결과")
public class RouletteRoundResponse {

	@Schema(description = "라운드 ID", example = "1")
	private final Long roundId;

	@Schema(description = "룰렛 벌칙 목록")
	private final List<RoulettePenaltyResponse> penalties;

	@Schema(description = "선택된 벌칙 인덱스", example = "2")
	private final int selectedIndex;

	@Schema(description = "선택된 벌칙")
	private final RoulettePenaltyResponse selectedPenalty;

	@Schema(description = "룰렛 시작 시각", example = "2026-08-10T15:30:00")
	private final LocalDateTime startedAt;

	@Schema(description = "룰렛 진행 시간(ms)", example = "5000")
	private final int durationMs;
}
