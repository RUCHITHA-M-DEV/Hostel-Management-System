package com.hostel.repository;

import com.hostel.model.Complaint;
import com.hostel.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudent(User student);
    List<Complaint> findByStatus(Complaint.ComplaintStatus status);
    List<Complaint> findByStudentOrderByCreatedAtDesc(User student);
    List<Complaint> findAllByOrderByCreatedAtDesc();
    long countByStatus(Complaint.ComplaintStatus status);
}
