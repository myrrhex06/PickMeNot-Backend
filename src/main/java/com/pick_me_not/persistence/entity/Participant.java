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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "participants", uniqueConstraints =
		@UniqueConstraint(name = "uk_participants_room_nickname", columnNames = {"room_id", "nickname"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Participant {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "room_id", nullable = false)
	private Room room;

	@Column(nullable = false, length = 30)
	private String nickname;

	@Column(name = "is_host", nullable = false)
	private boolean host;

	@Column(nullable = false)
	private boolean connected;

	@Column(name = "joined_at", nullable = false, updatable = false)
	private LocalDateTime joinedAt;

	@Column(name = "last_seen_at", nullable = false)
	private LocalDateTime lastSeenAt;

	private Participant(Room room, String nickname, boolean host, LocalDateTime joinedAt) {
		this.room = room;
		this.nickname = nickname;
		this.host = host;
		this.connected = false;
		this.joinedAt = joinedAt;
		this.lastSeenAt = joinedAt;
	}

	public static Participant createHost(Room room, String nickname, LocalDateTime joinedAt) {
		return new Participant(room, nickname, true, joinedAt);
	}

	public static Participant join(Room room, String nickname, LocalDateTime joinedAt) {
		return new Participant(room, nickname, false, joinedAt);
	}

	public void connect(LocalDateTime connectedAt) {
		this.connected = true;
		this.lastSeenAt = connectedAt;
	}

	public void disconnect(LocalDateTime disconnectedAt) {
		this.connected = false;
		this.lastSeenAt = disconnectedAt;
	}
}
