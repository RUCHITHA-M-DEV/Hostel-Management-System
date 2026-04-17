package com.hostel.controller;

import com.hostel.model.Payment;
import com.hostel.model.RoomAllocation;
import com.hostel.model.User;
import com.hostel.service.PaymentService;
import com.hostel.service.RoomService;
import com.hostel.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final UserService userService;
    private final RoomService roomService;

    public PaymentController(PaymentService paymentService, UserService userService, RoomService roomService) {
        this.paymentService = paymentService;
        this.userService = userService;
        this.roomService = roomService;
    }

    @GetMapping("/student/payment")
    @PreAuthorize("hasRole('STUDENT')")
    public String studentPayments(Model model, Authentication authentication) {
        User student = userService.getUserByUsername(authentication.getName());
        model.addAttribute("payments", paymentService.getPaymentsByStudent(student));
        model.addAttribute("pendingAmount", paymentService.getPendingAmountByStudent(student));
        return "student/payment";
    }

    @PostMapping("/student/payment/pay/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public String payNow(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.processPayment(id);
            redirectAttributes.addFlashAttribute("successMsg", "Payment successful!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", e.getMessage());
        }
        return "redirect:/student/payment";
    }

    @GetMapping("/admin/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminPayments(Model model) {
        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAttribute("totalCollected", paymentService.getTotalCollected());
        model.addAttribute("students", userService.getAllStudents());
        model.addAttribute("paymentTypes", Payment.PaymentType.values());
        return "admin/payments";
    }

    @PostMapping("/admin/payments/create")
    @PreAuthorize("hasRole('ADMIN')")
    public String createPayment(@RequestParam Long studentId,
                                 @RequestParam BigDecimal amount,
                                 @RequestParam Payment.PaymentType paymentType,
                                 @RequestParam String dueDate,
                                 @RequestParam(required = false) String remarks,
                                 RedirectAttributes redirectAttributes) {
        User student = userService.getUserById(studentId);
        RoomAllocation allocation = roomService.getActiveAllocationByStudent(student);
        paymentService.createPayment(
                student,
                allocation != null ? allocation.getRoom() : null,
                amount, paymentType,
                LocalDate.parse(dueDate),
                remarks
        );
        redirectAttributes.addFlashAttribute("successMsg", "Payment record created.");
        return "redirect:/admin/payments";
    }
}
