package com.pick_me_not.persistence.repository;

import com.pick_me_not.persistence.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

	List<Participant> findAllByRoomIdOrderByJoinedAtAsc(Long roomId);

	Optional<Participant> findByIdAndRoomId(Long participantId, Long roomId);

	Optional<Participant> findByRoomIdAndNickname(Long roomId, String nickname);

	boolean existsByRoomIdAndNickname(Long roomId, String nickname);

	long countByRoomId(Long roomId);

	@Modifying(clearAutomatically = true)
	@Query("update Participant p set p.connected = false where p.connected = true")
	int disconnectAll();
}
