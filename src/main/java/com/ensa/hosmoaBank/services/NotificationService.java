package com.ensa.hosmoaBank.services;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import com.ensa.hosmoaBank.models.Notification;
import com.ensa.hosmoaBank.repositories.ClientRepository;

@Service
@EnableScheduling
public class NotificationService {
    
	Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private AuthService authService;
    
    @Autowired
    private ClientRepository clientRepository;

    private List<Consumer<Notification>> listeners = new CopyOnWriteArrayList<>();

    // function to subscribe clients to server events (notifications)
    public void subscribe (Consumer<Notification> listener) {
        listeners.add(listener);
        logger.info("Added a listener, for a total of {} listener{}", listeners.size(), listeners.size() > 1 ? "s" : "");
    }

    // function to push notification
    public void publish (Notification notification) {
        logger.info("PROCESS NOTIF : {}", notification);
        // make sure the notification is sent only to the according client.
        if (notification.getClient().getId() == clientRepository.findByUser(authService.getCurrentUser()).get().getId()) {
        	System.out.println(listeners);
        	listeners.forEach(c -> {
        		System.err.println(c);
        		c.accept(notification);
        		});
        	
        }
            

    }

}
