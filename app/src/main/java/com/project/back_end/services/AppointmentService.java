package com.project.back_end.services;

import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.model.Patient;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;
    private final Service sharedService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              TokenService tokenService,
                              Service sharedService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
        this.sharedService = sharedService;
    }

    // 4. Book Appointment
    @Transactional
    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    // 5. Update Appointment
    @Transactional
    public String updateAppointment(Long appointmentId, Appointment updated, Long patientId) {
        Appointment existing = appointmentRepository.findById(appointmentId).orElse(null);
        if (existing == null) return "Appointment not found.";
        if (!existing.getPatient().getId().equals(patientId)) return "Unauthorized update attempt.";

        Doctor doctor = doctorRepository.findById(updated.getDoctor().getId()).orElse(null);
        if (doctor == null) return "Doctor not found.";

        List<Appointment> conflicts = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctor.getId(),
                updated.getAppointmentTime(),
                updated.getAppointmentTime().plusHours(1)
        );
        if (!conflicts.isEmpty()) return "Doctor not available at selected time.";

        existing.setAppointmentTime(updated.getAppointmentTime());
        existing.setStatus(updated.getStatus());
        appointmentRepository.save(existing);
        return "Appointment updated successfully.";
    }

    // 6. Cancel Appointment
    @Transactional
    public String cancelAppointment(Long appointmentId, Long patientId) {
        Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
        if (appointment == null) return "Appointment not found.";
        if (!appointment.getPatient().getId().equals(patientId)) return "Unauthorized cancellation attempt.";

        appointmentRepository.delete(appointment);
        return "Appointment cancelled.";
    }

    // 7. Get Appointments
    @Transactional
    public List<Appointment> getAppointments(Long doctorId, LocalDate date, String patientName) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = start.plusDays(1);

        if (patientName == null || patientName.trim().isEmpty()) {
            return appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            return appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                    doctorId, patientName, start, end
            );
        }
    }

    // 8. Change Status
    @Transactional
    public void changeStatus(Long appointmentId, int status) {
        appointmentRepository.updateStatus(status, appointmentId);
    }
}
