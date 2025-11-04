package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.model.Appointment;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public PatientService(PatientRepository patientRepository,
                          AppointmentRepository appointmentRepository,
                          TokenService tokenService) {
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    // 3. Create Patient
    public int createPatient(Patient patient) {
        try {
            patientRepository.save(patient);
            return 1;
        } catch (Exception e) {
            System.err.println("Error creating patient: " + e.getMessage());
            return 0;
        }
    }

    // 4. Get Patient Appointments
    @Transactional
    public List<AppointmentDTO> getPatientAppointment(Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository.findByPatientId(patientId);
            return appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error fetching appointments: " + e.getMessage());
            return List.of();
        }
    }

    // 5. Filter by Condition
    @Transactional
    public List<AppointmentDTO> filterByCondition(Long patientId, String condition) {
        try {
            int status = switch (condition.toLowerCase()) {
                case "future" -> 0;
                case "past" -> 1;
                default -> throw new IllegalArgumentException("Invalid condition: " + condition);
            };
            List<Appointment> appointments = appointmentRepository
                    .findByPatient_IdAndStatusOrderByAppointmentTimeAsc(patientId, status);
            return appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error filtering appointments: " + e.getMessage());
            return List.of();
        }
    }

    // 6. Filter by Doctor
    @Transactional
    public List<AppointmentDTO> filterByDoctor(String doctorName, Long patientId) {
        try {
            List<Appointment> appointments = appointmentRepository
                    .filterByDoctorNameAndPatientId(doctorName, patientId);
            return appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error filtering by doctor: " + e.getMessage());
            return List.of();
        }
    }

    // 7. Filter by Doctor and Condition
    @Transactional
    public List<AppointmentDTO> filterByDoctorAndCondition(String doctorName, Long patientId, String condition) {
        try {
            int status = switch (condition.toLowerCase()) {
                case "future" -> 0;
                case "past" -> 1;
                default -> throw new IllegalArgumentException("Invalid condition: " + condition);
            };
            List<Appointment> appointments = appointmentRepository
                    .filterByDoctorNameAndPatientIdAndStatus(doctorName, patientId, status);
            return appointments.stream()
                    .map(AppointmentDTO::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error filtering by doctor and condition: " + e.getMessage());
            return List.of();
        }
    }

    // 8. Get Patient Details
    public Patient getPatientDetails(String token) {
        try {
            String email = tokenService.extractEmail(token);
            return patientRepository.findByEmail(email);
        } catch (Exception e) {
            System.err.println("Error retrieving patient details: " + e.getMessage());
            return null;
        }
    }
}
