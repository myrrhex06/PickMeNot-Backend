package com.pick_me_not.domain.session;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ParticipantSessionRepository extends JpaRepository<ParticipantSession, Long> {

	Optional<ParticipantSession> findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(
			String tokenHash, LocalDateTime now);

	List<ParticipantSession> findAllByParticipantIdAndRevokedAtIsNull(Long participantId);
}
