package com.pick_me_not.domain.roulette;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RouletteRoundRepository extends JpaRepository<RouletteRound, Long> {

	Optional<RouletteRound> findFirstByRoomIdOrderByStartedAtDesc(Long roomId);

	Optional<RouletteRound> findByIdAndRoomId(Long roundId, Long roomId);
}
