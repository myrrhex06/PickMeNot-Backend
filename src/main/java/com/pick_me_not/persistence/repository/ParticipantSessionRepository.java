package com.pick_me_not.persistence.repository;

import com.pick_me_not.persistence.entity.ParticipantSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParticipantSessionRepository extends JpaRepository<ParticipantSession, Long> {

	Optional<ParticipantSession> findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
			String tokenHash, LocalDateTime now);

	@Query("""
			select ps from ParticipantSession ps
			join fetch ps.participant p
			join fetch p.room r
			where ps.tokenHash = :tokenHash
			  and ps.revokedAt is null
			  and ps.expiresAt > :now
			  and r.status <> com.pick_me_not.common.enums.RoomStatus.CLOSED
			  and r.expiresAt > :now
			""")
	Optional<ParticipantSession> findActiveSession(
			@Param("tokenHash") String tokenHash,
			@Param("now") LocalDateTime now);

	List<ParticipantSession> findAllByParticipantIdAndRevokedAtIsNull(Long participantId);

	@Query("""
			select ps from ParticipantSession ps
			join fetch ps.participant p
			join fetch p.room r
			where ps.tokenHash = :tokenHash
			  and ps.revokedAt is null
			  and ps.expiresAt > :now
			  and p.host = true
			  and r.roomCode = :roomCode
			""")
	Optional<ParticipantSession> findActiveHostSession(
			@Param("roomCode") String roomCode,
			@Param("tokenHash") String tokenHash,
			@Param("now") LocalDateTime now);
}
