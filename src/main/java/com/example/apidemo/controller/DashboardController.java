package com.example.apidemo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @GetMapping("/transactions")
    public Map<String, Object> getTransactions() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> data = new HashMap<>();
        
        data.put("total", 2);
        data.put("items", new Object[] {
            Map.of(
                "order_no", "862E9b43-a52E-1d5f-a24F-b2dAAeCB63AC",
                "timestamp", 1717112257509L,
                "username", "Laura Jones", 
                "price", 8204.3,
                "status", "pending"
            ),
            Map.of(
                "order_no", "2bfa275f-D6A4-6d7A-AEDc-9Fcf3079CA6c",
                "timestamp", 1717112257509L,
                "username", "Susan Hernandez",
                "price", 11826.8,
                "status", "pending"
            )
        });
        
        response.put("code", 20000);
        response.put("data", data);
        return response;
    }
}
