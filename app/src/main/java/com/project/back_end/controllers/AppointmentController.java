package com.project.back_end.controllers;

import com.project.back_end.model.Appointment;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final Service service;

    @Autowired
    public AppointmentController(AppointmentService appointmentService, Service service) {
        this.appointmentService = appointmentService;
        this.service = service;
    }

    // 3. Get Appointments
    @GetMapping("/doctor/{token}/{date}/{patientName}")
    public ResponseEntity<?> getAppointments(@PathVariable String token,
                                             @PathVariable String date,
                                             @PathVariable String patientName) {
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        LocalDate appointmentDate = LocalDate.parse(date);
        Long doctorId = Long.parseLong(service.extractEmail(token)); // assuming doctor ID is encoded as subject
        List<Appointment> appointments = appointmentService.getAppointments(doctorId, appointmentDate, patientName);
        return ResponseEntity.ok(appointments);
    }

    // 4. Book Appointment
    @PostMapping("/book/{token}")
    public ResponseEntity<Map<String, String>> bookAppointment(@RequestBody Appointment appointment,
                                                               @PathVariable String token) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient")) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        int validation = service.validateAppointment(appointment.getDoctor().getId(),
                                                     appointment.getAppointmentTime().toLocalDate(),
                                                     appointment.getAppointmentTime().toLocalTime());

        if (validation == -1) {
            response.put("error", "Invalid doctor ID");
            return ResponseEntity.badRequest().body(response);
        } else if (validation == 0) {
            response.put("error", "Doctor not available at selected time");
            return ResponseEntity.badRequest().body(response);
        }

        int result = appointmentService.bookAppointment(appointment);
        if (result == 1) {
            response.put("message", "Appointment booked successfully");
            return ResponseEntity.status(201).body(response);
        } else {
            response.put("error", "Failed to book appointment");
            return ResponseEntity.status(500).body(response);
        }
    }

    // 5. Update Appointment
    @PutMapping("/update/{token}")
    public ResponseEntity<Map<String, String>> updateAppointment(@RequestBody Appointment appointment,
                                                                 @PathVariable String token) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient")) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        Long patientId = Long.parseLong(service.extractEmail(token)); // assuming patient ID is encoded
        String result = appointmentService.updateAppointment(appointment.getId(), appointment, patientId);

        if ("Appointment updated successfully.".equals(result)) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 6. Cancel Appointment
    @DeleteMapping("/cancel/{token}/{appointmentId}")
    public ResponseEntity<Map<String, String>> cancelAppointment(@PathVariable String token,
                                                                 @PathVariable Long appointmentId) {
        Map<String, String> response = new HashMap<>();

        if (!service.validateToken(token, "patient")) {
            response.put("error", "Invalid or expired token");
            return ResponseEntity.status(401).body(response);
        }

        Long patientId = Long.parseLong(service.extractEmail(token)); // assuming patient ID is encoded
        String result = appointmentService.cancelAppointment(appointmentId, patientId);

        if ("Appointment cancelled.".equals(result)) {
            response.put("message", result);
            return ResponseEntity.ok(response);
        } else {
            response.put("error", result);
            return ResponseEntity.badRequest().body(response);
        }
    }
}
