package com.gdldv.notificationservice.service;

import com.gdldv.notificationservice.dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    public void sendNotification(NotificationRequest request) {
        // Mock sending logic (Real implementation would use JavaMailSender or Twilio)
        log.info("Sending {} notification to {}: {}", request.getType(), request.getRecipient(), request.getSubject());
        log.info("Message Body: {}", request.getMessage());
        
        // Simulate processing time
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("Notification sent successfully.");
    }
}
