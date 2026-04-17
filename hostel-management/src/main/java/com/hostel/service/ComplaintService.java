package com.hostel.service;

import com.hostel.model.Complaint;
import com.hostel.model.User;
import com.hostel.observer.ComplaintEventPublisher;
import com.hostel.repository.ComplaintRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintEventPublisher eventPublisher;

    public ComplaintService(ComplaintRepository complaintRepository, ComplaintEventPublisher eventPublisher) {
        this.complaintRepository = complaintRepository;
        this.eventPublisher = eventPublisher;
    }

    public Complaint submitComplaint(User student, String title, String description, Complaint.ComplaintCategory category) {
        Complaint complaint = new Complaint();
        complaint.setStudent(student);
        complaint.setTitle(title);
        complaint.setDescription(description);
        complaint.setCategory(category);
        complaint.setStatus(Complaint.ComplaintStatus.PENDING);

        Complaint saved = complaintRepository.save(complaint);
        eventPublisher.notifyComplaintCreated(saved);
        return saved;
    }

    public Complaint updateStatus(Long complaintId, Complaint.ComplaintStatus status, String remarks, User warden) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setStatus(status);
        complaint.setWardenRemarks(remarks);
        if (status == Complaint.ComplaintStatus.RESOLVED || status == Complaint.ComplaintStatus.REJECTED) {
            complaint.setResolvedBy(warden);
            complaint.setResolvedAt(LocalDateTime.now());
        }
        Complaint updated = complaintRepository.save(complaint);
        eventPublisher.notifyComplaintStatusChanged(updated);
        return updated;
    }

    public List<Complaint> getComplaintsByStudent(User student) {
        return complaintRepository.findByStudentOrderByCreatedAtDesc(student);
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Complaint> getComplaintsByStatus(Complaint.ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found."));
    }

    public long countPendingComplaints() {
        return complaintRepository.countByStatus(Complaint.ComplaintStatus.PENDING);
    }
}
