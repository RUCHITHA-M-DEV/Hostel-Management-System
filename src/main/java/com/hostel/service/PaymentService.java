package com.hostel.service;

import com.hostel.model.Payment;
import com.hostel.model.Room;
import com.hostel.model.User;
import com.hostel.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(User student, Room room, BigDecimal amount,
                                  Payment.PaymentType type, LocalDate dueDate, String remarks) {
        Payment payment = new Payment();
        payment.setStudent(student);
        payment.setRoom(room);
        payment.setAmount(amount);
        payment.setPaymentType(type);
        payment.setDueDate(dueDate);
        payment.setRemarks(remarks);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        return paymentRepository.save(payment);
    }

    public Payment processPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setStatus(Payment.PaymentStatus.PAID);
        payment.setPaidDate(LocalDate.now());
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        return paymentRepository.save(payment);
    }

    public List<Payment> getPaymentsByStudent(User student) {
        return paymentRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAllByOrderByCreatedAtDesc();
    }

    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found."));
    }

    public BigDecimal getTotalCollected() {
        BigDecimal total = paymentRepository.sumPaidPayments();
        return total != null ? total : BigDecimal.ZERO;
    }

    public BigDecimal getPendingAmountByStudent(User student) {
        BigDecimal pending = paymentRepository.sumPendingPaymentsByStudent(student);
        return pending != null ? pending : BigDecimal.ZERO;
    }

    public long countPendingPayments() {
        return paymentRepository.countByStatus(Payment.PaymentStatus.PENDING);
    }

    public void cancelPayment(Long paymentId) {
        Payment payment = getPaymentById(paymentId);
        payment.setStatus(Payment.PaymentStatus.CANCELLED);
        paymentRepository.save(payment);
    }
}
