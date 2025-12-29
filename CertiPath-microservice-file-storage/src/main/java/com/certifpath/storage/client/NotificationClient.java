package com.certifpath.storage.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// "NOTIFICATION-SERVICE" doit correspondre exactement au nom dans Eureka (souvent en MAJUSCULES)
@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/notifications/send")
    void sendNotification(@RequestParam("userId") String userId,
                          @RequestParam("message") String message,
                          @RequestParam("type") String type);
}