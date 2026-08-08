package com.pick_me_not.domain.penalty;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PenaltyRepository extends JpaRepository<Penalty, Long> {

	List<Penalty> findAllByRoomIdAndActiveTrueOrderByIdAsc(Long roomId);

	Optional<Penalty> findByIdAndRoomId(Long penaltyId, Long roomId);
}
