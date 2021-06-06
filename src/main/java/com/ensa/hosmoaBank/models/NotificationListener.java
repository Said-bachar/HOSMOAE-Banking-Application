package com.ensa.hosmoaBank.models;

import javax.persistence.PostPersist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ensa.hosmoaBank.services.NotificationService;

//Notification listener to send notification after persisting entity in the DB.
@Component
class NotificationListener {

 @Autowired
 private NotificationService notificationService;

 @PostPersist
 void send (Notification notification) {
     notificationService.publish(notification);
 }
}
