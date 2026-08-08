package com.pick_me_not.domain.participant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

	List<Participant> findAllByRoomIdOrderByJoinedAtAsc(Long roomId);

	Optional<Participant> findByIdAndRoomId(Long participantId, Long roomId);

	Optional<Participant> findByRoomIdAndNickname(Long roomId, String nickname);

	boolean existsByRoomIdAndNickname(Long roomId, String nickname);
}
