package com.project.back_end.controllers;

import com.project.back_end.DTO.Login;
import com.project.back_end.model.Doctor;
import com.project.back_end.services.DoctorService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("${api.path}doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final Service service;

    @Autowired
    public DoctorController(DoctorService doctorService, Service service) {
        this.doctorService = doctorService;
        this.service = service;
    }

    // 3. Get Doctor Availability
    @GetMapping("/availability/{user}/{doctorId}/{date}/{token}")
    public ResponseEntity<?> getDoctorAvailability(@PathVariable String user,
                                                   @PathVariable Long doctorId,
                                                   @PathVariable String date,
                                                   @PathVariable String token) {
        if (!service.validateToken(token, user)) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        LocalDate localDate = LocalDate.parse(date);
        List<?> availability = doctorService.getDoctorAvailability(doctorId, localDate);
        return ResponseEntity.ok(availability);
    }

    // 4. Get All Doctors
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> getDoctor() {
        List<Doctor> doctors = doctorService.getDoctors();
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }

    // 5. Save Doctor
    @PostMapping("/register/{token}")
    public ResponseEntity<Map<String, String>> saveDoctor(@RequestBody Doctor doctor,
                                                          @PathVariable String token) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("error", "Unauthorized access");
            return ResponseEntity.status(401).body(response);
        }

        int result = doctorService.saveDoctor(doctor);
        if (result == -1) {
            response.put("error", "Doctor already exists");
            return ResponseEntity.status(409).body(response);
        } else if (result == 1) {
            response.put("message", "Doctor registered successfully");
            return ResponseEntity.status(201).body(response);
        } else {
            response.put("error", "Internal server error");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 6. Doctor Login
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> doctorLogin(@RequestBody Login login) {
        String token = doctorService.validateDoctor(login.getEmail(), login.getPassword());
        Map<String, String> response = new HashMap<>();

        if ("Invalid credentials".equals(token)) {
            response.put("error", token);
            return ResponseEntity.status(401).body(response);
        }

        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    // 7. Update Doctor
    @PutMapping("/update/{token}")
    public ResponseEntity<Map<String, String>> updateDoctor(@RequestBody Doctor doctor,
                                                            @PathVariable String token) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("error", "Unauthorized access");
            return ResponseEntity.status(401).body(response);
        }

        int result = doctorService.updateDoctor(doctor);
        if (result == -1) {
            response.put("error", "Doctor not found");
            return ResponseEntity.status(404).body(response);
        } else {
            response.put("message", "Doctor updated successfully");
            return ResponseEntity.ok(response);
        }
    }

    // 8. Delete Doctor
    @DeleteMapping("/delete/{doctorId}/{token}")
    public ResponseEntity<Map<String, String>> deleteDoctor(@PathVariable Long doctorId,
                                                            @PathVariable String token) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "admin")) {
            response.put("error", "Unauthorized access");
            return ResponseEntity.status(401).body(response);
        }

        int result = doctorService.deleteDoctor(doctorId);
        if (result == -1) {
            response.put("error", "Doctor not found");
            return ResponseEntity.status(404).body(response);
        } else {
            response.put("message", "Doctor deleted successfully");
            return ResponseEntity.ok(response);
        }
    }

    // 9. Filter Doctors
    @GetMapping("/filter/{name}/{time}/{speciality}")
    public ResponseEntity<Map<String, Object>> filter(@PathVariable String name,
                                                      @PathVariable String time,
                                                      @PathVariable String speciality) {
        List<Doctor> doctors = service.filterDoctor(name, speciality, time);
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctors);
        return ResponseEntity.ok(response);
    }
}
