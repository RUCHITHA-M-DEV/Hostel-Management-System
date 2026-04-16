package com.hostel.controller;

import com.hostel.model.User;
import com.hostel.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    private final UserService userService;
    private final RoomService roomService;
    private final ComplaintService complaintService;
    private final PaymentService paymentService;

    public DashboardController(UserService userService, RoomService roomService,
                                ComplaintService complaintService, PaymentService paymentService) {
        this.userService = userService;
        this.roomService = roomService;
        this.complaintService = complaintService;
        this.paymentService = paymentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_WARDEN"))) {
            return "redirect:/warden/dashboard";
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/student/dashboard")
    public String studentDashboard(Authentication authentication, Model model) {
        User student = userService.getUserByUsername(authentication.getName());
        model.addAttribute("currentAllocation", roomService.getActiveAllocationByStudent(student));
        model.addAttribute("pendingAmount", paymentService.getPendingAmountByStudent(student));
        model.addAttribute("openComplaints", complaintService.getComplaintsByStudent(student)
                .stream().filter(c -> c.getStatus() == com.hostel.model.Complaint.ComplaintStatus.PENDING).count());
        return "student/dashboard";
    }

    @GetMapping("/warden/dashboard")
    public String wardenDashboard(Model model) {
        model.addAttribute("totalStudents", userService.countStudents());
        model.addAttribute("pendingComplaints", complaintService.countPendingComplaints());
        model.addAttribute("activeAllocations", roomService.getAllAllocations().stream()
                .filter(a -> a.getStatus() == com.hostel.model.RoomAllocation.AllocationStatus.ACTIVE).count());
        return "warden/dashboard";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("totalStudents", userService.countStudents());
        model.addAttribute("totalRooms", roomService.countTotalRooms());
        model.addAttribute("availableRooms", roomService.countAvailableRooms());
        model.addAttribute("pendingComplaints", complaintService.countPendingComplaints());
        model.addAttribute("totalCollected", paymentService.getTotalCollected());
        model.addAttribute("pendingPayments", paymentService.countPendingPayments());
        return "admin/dashboard";
    }

    @GetMapping("/admin/users")
    public String adminUsers(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }

    @PostMapping("/admin/users/add-warden")
    public String addWarden(@RequestParam String username,
                             @RequestParam String password,
                             @RequestParam String email,
                             @RequestParam String fullName,
                             @RequestParam String phoneNumber,
                             org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            userService.registerWarden(username, password, email, fullName, phoneNumber);
            redirectAttributes.addFlashAttribute("successMsg", "Warden added successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/toggle/{id}")
    public String toggleUser(@PathVariable Long id,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        userService.toggleUserStatus(id);
        redirectAttributes.addFlashAttribute("successMsg", "User status updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/admin/users/delete/{id}")
    public String deleteUser(@PathVariable Long id,
                              org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("successMsg", "User deleted.");
        return "redirect:/admin/users";
    }
}
