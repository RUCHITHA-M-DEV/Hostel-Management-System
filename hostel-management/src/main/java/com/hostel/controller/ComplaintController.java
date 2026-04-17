package com.hostel.controller;

import com.hostel.model.Complaint;
import com.hostel.model.User;
import com.hostel.service.ComplaintService;
import com.hostel.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ComplaintController {

    private final ComplaintService complaintService;
    private final UserService userService;

    public ComplaintController(ComplaintService complaintService, UserService userService) {
        this.complaintService = complaintService;
        this.userService = userService;
    }

    @GetMapping("/student/complaints")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentComplaints(Model model, Authentication authentication) {
        User student = userService.getUserByUsername(authentication.getName());
        model.addAttribute("complaints", complaintService.getComplaintsByStudent(student));
        model.addAttribute("categories", Complaint.ComplaintCategory.values());
        return "student/complaints";
    }

    @PostMapping("/student/complaints/submit")
    @PreAuthorize("hasRole('STUDENT')")
    public String submitComplaint(@RequestParam String title,
                                   @RequestParam String description,
                                   @RequestParam Complaint.ComplaintCategory category,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        User student = userService.getUserByUsername(authentication.getName());
        complaintService.submitComplaint(student, title, description, category);
        redirectAttributes.addFlashAttribute("successMsg", "Complaint submitted successfully.");
        return "redirect:/student/complaints";
    }

    @GetMapping("/warden/complaints")
    @PreAuthorize("hasAnyRole('WARDEN','ADMIN')")
    public String wardenComplaints(Model model) {
        model.addAttribute("complaints", complaintService.getAllComplaints());
        model.addAttribute("statuses", Complaint.ComplaintStatus.values());
        return "warden/complaints";
    }

    @PostMapping("/warden/complaints/update/{id}")
    @PreAuthorize("hasAnyRole('WARDEN','ADMIN')")
    public String updateComplaint(@PathVariable Long id,
                                   @RequestParam Complaint.ComplaintStatus status,
                                   @RequestParam(required = false) String remarks,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        User warden = userService.getUserByUsername(authentication.getName());
        complaintService.updateStatus(id, status, remarks, warden);
        redirectAttributes.addFlashAttribute("successMsg", "Complaint updated.");
        return "redirect:/warden/complaints";
    }
}
