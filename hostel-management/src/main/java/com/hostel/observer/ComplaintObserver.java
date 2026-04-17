package com.hostel.observer;

import com.hostel.model.Complaint;

/**
 * Behavioral Pattern: Observer
 * Interface for complaint event observers.
 */
public interface ComplaintObserver {
    void onComplaintCreated(Complaint complaint);
    void onComplaintStatusChanged(Complaint complaint);
}
