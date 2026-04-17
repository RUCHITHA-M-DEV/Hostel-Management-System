package com.hostel.repository;

import com.hostel.model.RoomAllocation;
import com.hostel.model.User;
import com.hostel.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomAllocationRepository extends JpaRepository<RoomAllocation, Long> {
    Optional<RoomAllocation> findByStudentAndStatus(User student, RoomAllocation.AllocationStatus status);
    List<RoomAllocation> findByRoom(Room room);
    List<RoomAllocation> findByStudent(User student);
    List<RoomAllocation> findByStatus(RoomAllocation.AllocationStatus status);
    boolean existsByStudentAndStatus(User student, RoomAllocation.AllocationStatus status);
}
