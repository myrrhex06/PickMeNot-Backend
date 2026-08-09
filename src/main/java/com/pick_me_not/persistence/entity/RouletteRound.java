package com.pick_me_not.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "roulette_rounds")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RouletteRound {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "executed_by_participant_id", nullable = false)
	private Participant executedBy;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "selected_penalty_id", nullable = false)
	private Penalty selectedPenalty;

	@Column(name = "penalty_snapshot", nullable = false, length = 200)
	private String penaltySnapshot;

	@Column(name = "roulette_snapshot", nullable = false, columnDefinition = "json")
	@JdbcTypeCode(SqlTypes.JSON)
	private String rouletteSnapshot;

	@Column(name = "started_at", nullable = false, updatable = false)
	private LocalDateTime startedAt;

	@Column(name = "duration_ms", nullable = false)
	private int durationMs;

	private RouletteRound(
			Room room,
			Participant executedBy,
			Penalty selectedPenalty,
			String penaltySnapshot,
			String rouletteSnapshot,
			LocalDateTime startedAt,
			int durationMs
	) {
		this.room = room;
		this.executedBy = executedBy;
		this.selectedPenalty = selectedPenalty;
		this.penaltySnapshot = penaltySnapshot;
		this.rouletteSnapshot = rouletteSnapshot;
		this.startedAt = startedAt;
		this.durationMs = durationMs;
	}

	public static RouletteRound start(
			Room room,
			Participant executedBy,
			Penalty selectedPenalty,
			String rouletteSnapshot,
			LocalDateTime startedAt,
			int durationMs
	) {
		if (durationMs <= 0) {
			throw new IllegalArgumentException("룰렛 진행 시간은 0보다 커야 합니다.");
		}
		return new RouletteRound(
				room,
				executedBy,
				selectedPenalty,
				selectedPenalty.getContent(),
				rouletteSnapshot,
				startedAt,
				durationMs);
	}
}
