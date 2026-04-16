package com.hostel.observer;

import com.hostel.model.Complaint;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Behavioral Pattern: Observer - Publisher/Subject
 * Manages complaint event observers and publishes events.
 */
@Component
public class ComplaintEventPublisher {

    private final List<ComplaintObserver> observers = new ArrayList<>();

    public void subscribe(ComplaintObserver observer) {
        observers.add(observer);
    }

    public void unsubscribe(ComplaintObserver observer) {
        observers.remove(observer);
    }

    public void notifyComplaintCreated(Complaint complaint) {
        for (ComplaintObserver observer : observers) {
            observer.onComplaintCreated(complaint);
        }
    }

    public void notifyComplaintStatusChanged(Complaint complaint) {
        for (ComplaintObserver observer : observers) {
            observer.onComplaintStatusChanged(complaint);
        }
    }
}
