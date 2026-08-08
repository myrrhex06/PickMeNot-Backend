package com.pick_me_not.domain.room;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "room_code", nullable = false, unique = true, length = 12)
	private String roomCode;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private RoomStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Version
	@Column(nullable = false)
	private long version;
}
