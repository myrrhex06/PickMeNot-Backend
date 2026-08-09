package com.pick_me_not.roulette.dto;

import com.pick_me_not.persistence.entity.Penalty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "룰렛 벌칙 정보")
public class RoulettePenaltyResponse {

	@Schema(description = "벌칙 ID", example = "1")
	private final Long penaltyId;

	@Schema(description = "벌칙 내용", example = "노래 한 소절 부르기")
	private final String content;

	public static RoulettePenaltyResponse from(Penalty penalty) {
		return RoulettePenaltyResponse.builder()
				.penaltyId(penalty.getId())
				.content(penalty.getContent())
				.build();
	}
}
