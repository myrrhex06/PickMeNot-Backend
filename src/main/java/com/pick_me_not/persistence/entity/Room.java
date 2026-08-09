package com.pick_me_not.persistence.entity;

import com.pick_me_not.common.enums.RoomStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "rooms")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

	private Room(String roomCode, LocalDateTime createdAt, LocalDateTime expiresAt) {
		this.roomCode = roomCode;
		this.status = RoomStatus.WAITING;
		this.createdAt = createdAt;
		this.expiresAt = expiresAt;
	}

	public static Room create(String roomCode, LocalDateTime createdAt, LocalDateTime expiresAt) {
		if (expiresAt.isBefore(createdAt) || expiresAt.isEqual(createdAt)) {
			throw new IllegalArgumentException("방 만료 시각은 생성 시각보다 뒤여야 합니다.");
		}
		return new Room(roomCode, createdAt, expiresAt);
	}

	public void changeStatus(RoomStatus status) {
		if (this.status == RoomStatus.CLOSED) {
			throw new IllegalStateException("종료된 방의 상태는 변경할 수 없습니다.");
		}
		this.status = status;
	}

	public void close() {
		this.status = RoomStatus.CLOSED;
	}

	public void validateJoinable(LocalDateTime now) {
		if (status != RoomStatus.WAITING) {
			throw new IllegalStateException("대기 중인 방에만 참여할 수 있습니다.");
		}
		if (!expiresAt.isAfter(now)) {
			throw new IllegalStateException("만료된 방에는 참여할 수 없습니다.");
		}
	}
}
