package com.project.back_end.controllers;

import com.project.back_end.model.Prescription;
import com.project.back_end.services.AppointmentService;
import com.project.back_end.services.PrescriptionService;
import com.project.back_end.services.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.path}prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final Service service;
    private final AppointmentService appointmentService;

    @Autowired
    public PrescriptionController(PrescriptionService prescriptionService,
                                  Service service,
                                  AppointmentService appointmentService) {
        this.prescriptionService = prescriptionService;
        this.service = service;
        this.appointmentService = appointmentService;
    }

    // 3. Save Prescription
    @PostMapping("/save/{token}")
    public ResponseEntity<?> savePrescription(@RequestBody Prescription prescription,
                                              @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        // Update appointment status to reflect prescription issued
        appointmentService.changeStatus(prescription.getAppointmentId(), 2); // assuming status 2 = prescription issued

        return prescriptionService.savePrescription(prescription);
    }

    // 4. Get Prescription
    @GetMapping("/get/{appointmentId}/{token}")
    public ResponseEntity<?> getPrescription(@PathVariable Long appointmentId,
                                             @PathVariable String token) {
        if (!service.validateToken(token, "doctor")) {
            return ResponseEntity.status(401).body("Invalid or expired token");
        }

        return prescriptionService.getPrescription(appointmentId);
    }
}
