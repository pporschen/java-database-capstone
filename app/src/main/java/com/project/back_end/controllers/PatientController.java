package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.model.Patient;
import com.project.back_end.services.PatientService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
public class PatientController {

    private final PatientService patientService;
    private final Service service;

    @Autowired
    public PatientController(PatientService patientService, Service service) {
        this.patientService = patientService;
        this.service = service;
    }

    // 3. Get Patient Details
    @GetMapping("/details/{token}")
    public ResponseEntity<?> getPatient(@PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        Patient patient = patientService.getPatientDetails(token);
        return ResponseEntity.ok(patient);
    }

    // 4. Create Patient
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> createPatient(@RequestBody Patient patient) {
        Map<String, String> response = new HashMap<>();

        boolean isValid = service.validatePatient(patient.getEmail(), patient.getPhone());
        if (!isValid) {
            response.put("error", "Patient already exists");
            return ResponseEntity.status(409).body(response);
        }

        int result = patientService.createPatient(patient);
        if (result == 1) {
            response.put("message", "Patient registered successfully");
            return ResponseEntity.status(201).body(response);
        } else {
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 5. Patient Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Login login) {
        String token = service.validatePatientLogin(login.getEmail(), login.getPassword());
        Map<String, String> response = new HashMap<>();

        if ("Invalid credentials".equals(token)) {
            response.put("error", token);
            return ResponseEntity.status(401).body(response);
        }

        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // 6. Get Patient Appointments
    @GetMapping("/appointments/{patientId}/{token}/{role}")
    public ResponseEntity<?> getPatientAppointment(@PathVariable Long patientId,
                                                   @PathVariable String token,
                                                   @PathVariable String role) {
        if (!service.validateToken(token, role)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        return ResponseEntity.ok(patientService.getPatientAppointment(patientId));
    }

    // 7. Filter Patient Appointments
    @GetMapping("/appointments/filter/{condition}/{name}/{token}")
    public ResponseEntity<?> filterPatientAppointment(@PathVariable String condition,
                                                      @PathVariable String name,
                                                      @PathVariable String token) {
        if (!service.validateToken(token, "patient")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        return service.filterPatient(token, condition, name);
    }
}
