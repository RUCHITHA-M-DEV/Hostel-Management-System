package com.hostel.service;

import com.hostel.model.Room;
import com.hostel.model.RoomAllocation;
import com.hostel.model.User;
import com.hostel.repository.RoomAllocationRepository;
import com.hostel.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomAllocationRepository allocationRepository;

    public RoomService(RoomRepository roomRepository, RoomAllocationRepository allocationRepository) {
        this.roomRepository = roomRepository;
        this.allocationRepository = allocationRepository;
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Room not found."));
    }

    public void deleteRoom(Long id) {
        roomRepository.deleteById(id);
    }

    public RoomAllocation allocateRoom(User student, Long roomId) {
        if (allocationRepository.existsByStudentAndStatus(student, RoomAllocation.AllocationStatus.ACTIVE)) {
            throw new IllegalStateException("Student already has an active room allocation.");
        }
        Room room = getRoomById(roomId);
        if (!room.isAvailable()) {
            throw new IllegalStateException("Room is not available.");
        }

        RoomAllocation allocation = new RoomAllocation();
        allocation.setStudent(student);
        allocation.setRoom(room);
        allocation.setAllocationDate(LocalDate.now());
        allocation.setStatus(RoomAllocation.AllocationStatus.ACTIVE);

        room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
        if (room.getCurrentOccupancy() >= room.getCapacity()) {
            room.setStatus(Room.RoomStatus.OCCUPIED);
        }
        roomRepository.save(room);

        return allocationRepository.save(allocation);
    }

    public void vacateRoom(Long allocationId) {
        RoomAllocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new IllegalArgumentException("Allocation not found."));
        allocation.setStatus(RoomAllocation.AllocationStatus.VACATED);
        allocation.setVacatingDate(LocalDate.now());

        Room room = allocation.getRoom();
        room.setCurrentOccupancy(Math.max(0, room.getCurrentOccupancy() - 1));
        if (room.getStatus() == Room.RoomStatus.OCCUPIED) {
            room.setStatus(Room.RoomStatus.AVAILABLE);
        }
        roomRepository.save(room);
        allocationRepository.save(allocation);
    }

    public List<RoomAllocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    public List<RoomAllocation> getAllocationsByStudent(User student) {
        return allocationRepository.findByStudent(student);
    }

    public RoomAllocation getActiveAllocationByStudent(User student) {
        return allocationRepository.findByStudentAndStatus(student, RoomAllocation.AllocationStatus.ACTIVE)
                .orElse(null);
    }

    public long countAvailableRooms() {
        return roomRepository.findAvailableRooms().size();
    }

    public long countTotalRooms() {
        return roomRepository.count();
    }
}
