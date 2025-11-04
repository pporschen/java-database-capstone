package com.project.back_end.services;

import com.project.back_end.model.Admin;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    @Autowired
    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    // 3. Validate Token
    public boolean validateToken(String token, String role) {
        return tokenService.validateToken(token, role);
    }

    // 4. Validate Admin Login
    public ResponseEntity<String> validateAdmin(String username, String password) {
        try {
            Admin admin = adminRepository.findByUsername(username);
            if (admin == null || !admin.getPassword().equals(password)) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            String token = tokenService.generateToken(admin.getUsername(), "admin");
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    // 5. Filter Doctor
    @Transactional
    public List<Doctor> filterDoctor(String name, String specialty, String timePeriod) {
        if (name != null && specialty != null && timePeriod != null) {
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, timePeriod);
        } else if (name != null && specialty != null) {
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        } else if (name != null && timePeriod != null) {
            return doctorService.filterDoctorByNameAndTime(name, timePeriod);
        } else if (specialty != null && timePeriod != null) {
            return doctorService.filterDoctorByTimeAndSpecility(specialty, timePeriod);
        } else if (name != null) {
            return doctorService.findDoctorByName(name);
        } else if (specialty != null) {
            return doctorService.filterDoctorBySpecility(specialty);
        } else if (timePeriod != null) {
            return doctorService.filterDoctorsByTime(timePeriod);
        } else {
            return doctorService.getDoctors();
        }
    }

    // 6. Validate Appointment
    @Transactional
    public int validateAppointment(Long doctorId, LocalDate date, LocalTime time) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) return -1;

        List<LocalTime> availableTimes = doctorService.getDoctorAvailability(doctorId, date);
        return availableTimes.contains(time) ? 1 : 0;
    }

    // 7. Validate Patient Registration
    public boolean validatePatient(String email, String phone) {
        return patientRepository.findByEmailOrPhone(email, phone) == null;
    }

    // 8. Validate Patient Login
    public ResponseEntity<String> validatePatientLogin(String email, String password) {
        try {
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null || !patient.getPassword().equals(password)) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            String token = tokenService.generateToken(patient.getId(), "patient");
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }

    // 9. Filter Patient Appointments
    @Transactional
    public ResponseEntity<?> filterPatient(String token, String condition, String doctorName) {
        try {
            String email = tokenService.extractEmail(token);
            Patient patient = patientRepository.findByEmail(email);
            if (patient == null) return ResponseEntity.status(401).body("Invalid token");

            Long patientId = patient.getId();

            if (condition != null && doctorName != null) {
                return ResponseEntity.ok(patientService.filterByDoctorAndCondition(doctorName, patientId, condition));
            } else if (condition != null) {
                return ResponseEntity.ok(patientService.filterByCondition(patientId, condition));
            } else if (doctorName != null) {
                return ResponseEntity.ok(patientService.filterByDoctor(doctorName, patientId));
            } else {
                return ResponseEntity.ok(patientService.getPatientAppointment(patientId));
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error");
        }
    }
}
