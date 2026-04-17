package com.hostel.controller;

import com.hostel.model.Room;
import com.hostel.model.User;
import com.hostel.service.RoomService;
import com.hostel.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class RoomController {

    private final RoomService roomService;
    private final UserService userService;

    public RoomController(RoomService roomService, UserService userService) {
        this.roomService = roomService;
        this.userService = userService;
    }

    @GetMapping("/student/rooms")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentRooms(Model model, Authentication authentication) {
        User student = userService.getUserByUsername(authentication.getName());
        model.addAttribute("rooms", roomService.getAvailableRooms());
        model.addAttribute("currentAllocation", roomService.getActiveAllocationByStudent(student));
        return "student/rooms";
    }

    @PostMapping("/student/rooms/apply/{roomId}")
    @PreAuthorize("hasRole('STUDENT')")
    public String applyForRoom(@PathVariable Long roomId, Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        try {
            User student = userService.getUserByUsername(authentication.getName());
            roomService.allocateRoom(student, roomId);
            redirectAttributes.addFlashAttribute("successMsg", "Room allocated successfully!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/student/rooms";
    }

    @GetMapping("/warden/allocations")
    @PreAuthorize("hasAnyRole('WARDEN','ADMIN')")
    public String wardenAllocations(Model model) {
        model.addAttribute("allocations", roomService.getAllAllocations());
        return "warden/allocations";
    }

    @PostMapping("/warden/allocations/vacate/{id}")
    @PreAuthorize("hasAnyRole('WARDEN','ADMIN')")
    public String vacateRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roomService.vacateRoom(id);
            redirectAttributes.addFlashAttribute("successMsg", "Room vacated successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/warden/allocations";
    }

    @GetMapping("/admin/rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminRooms(Model model) {
        model.addAttribute("rooms", roomService.getAllRooms());
        return "admin/rooms";
    }

    @PostMapping("/admin/rooms/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addRoom(@RequestParam String roomNumber,
                           @RequestParam Room.RoomType roomType,
                           @RequestParam int capacity,
                           @RequestParam BigDecimal pricePerMonth,
                           @RequestParam(required = false) String description,
                           RedirectAttributes redirectAttributes) {
        Room room = new Room();
        room.setRoomNumber(roomNumber);
        room.setRoomType(roomType);
        room.setCapacity(capacity);
        room.setPricePerMonth(pricePerMonth);
        room.setDescription(description);
        room.setStatus(Room.RoomStatus.AVAILABLE);
        roomService.saveRoom(room);
        redirectAttributes.addFlashAttribute("successMsg", "Room added successfully.");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/admin/rooms/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        roomService.deleteRoom(id);
        redirectAttributes.addFlashAttribute("successMsg", "Room deleted.");
        return "redirect:/admin/rooms";
    }
}
