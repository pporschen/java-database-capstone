package com.project.back_end.services;

import com.project.back_end.model.Prescription;
import com.project.back_end.repo.PrescriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;

    @Autowired
    public PrescriptionService(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    // 3. Save Prescription
    public ResponseEntity<Map<String, String>> savePrescription(Prescription prescription) {
        try {
            List<Prescription> existing = prescriptionRepository.findByAppointmentId(prescription.getAppointmentId());
            if (!existing.isEmpty()) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Prescription already exists for this appointment.");
                return ResponseEntity.badRequest().body(response);
            }

            prescriptionRepository.save(prescription);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Prescription saved successfully.");
            return ResponseEntity.status(201).body(response);

        } catch (Exception e) {
            System.err.println("Error saving prescription: " + e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("error", "Internal server error while saving prescription.");
            return ResponseEntity.status(500).body(error);
        }
    }

    // 4. Get Prescription
    public ResponseEntity<Map<String, Object>> getPrescription(Long appointmentId) {
        try {
            List<Prescription> prescriptions = prescriptionRepository.findByAppointmentId(appointmentId);
            if (prescriptions.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("message", "No prescription found for this appointment.");
                return ResponseEntity.ok(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("prescription", prescriptions.get(0)); // assuming one per appointment
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error retrieving prescription: " + e.getMessage());
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Internal server error while retrieving prescription.");
            return ResponseEntity.status(500).body(error);
        }
    }
}
