package com.hostel.repository;

import com.hostel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    Optional<Room> findByRoomNumber(String roomNumber);
    List<Room> findByStatus(Room.RoomStatus status);
    List<Room> findByRoomType(Room.RoomType roomType);

    @Query("SELECT r FROM Room r WHERE r.currentOccupancy < r.capacity AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRooms();
}
