package com.project.back_end.controllers;

import com.project.back_end.model.Admin;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.path}admin")
public class AdminController {

    private final Service service;

    @Autowired
    public AdminController(Service service) {
        this.service = service;
    }

    // 3. Admin Login Endpoint
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> adminLogin(@RequestBody Admin admin) {
        ResponseEntity<String> response = service.validateAdmin(admin.getUsername(), admin.getPassword());

        Map<String, String> result = new HashMap<>();
        if (response.getStatusCode().is2xxSuccessful()) {
            result.put("token", response.getBody());
            return ResponseEntity.ok(result);
        } else {
            result.put("error", response.getBody());
            return ResponseEntity.status(response.getStatusCode()).body(result);
        }
    }
}
