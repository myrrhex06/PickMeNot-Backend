package com.pick_me_not.domain.room;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

	Optional<Room> findByRoomCode(String roomCode);

	boolean existsByRoomCode(String roomCode);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select r from Room r where r.roomCode = :roomCode")
	Optional<Room> findByRoomCodeForUpdate(String roomCode);
}
