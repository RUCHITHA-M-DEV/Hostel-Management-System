package com.hostel.repository;

import com.hostel.model.Payment;
import com.hostel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByStudent(User student);
    List<Payment> findByStudentOrderByCreatedAtDesc(User student);
    List<Payment> findByStatus(Payment.PaymentStatus status);
    List<Payment> findAllByOrderByCreatedAtDesc();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'PAID'")
    BigDecimal sumPaidPayments();

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.student = :student AND p.status = 'PENDING'")
    BigDecimal sumPendingPaymentsByStudent(User student);

    long countByStatus(Payment.PaymentStatus status);
}
