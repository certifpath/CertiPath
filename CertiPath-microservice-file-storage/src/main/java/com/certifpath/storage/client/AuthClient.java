package com.certifpath.storage.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

// "AUTH-SERVICE" est le nom dans Eureka
@FeignClient(name = "AUTH-SERVICE")
public interface AuthClient {

    @GetMapping("/auth/auditors/emails")
    List<String> getAuditorEmails();
}
