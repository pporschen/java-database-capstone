package com.project.back_end.services;

import com.project.back_end.model.Appointment;
import com.project.back_end.model.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    @Autowired
    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<LocalTime> getDoctorAvailability(Long doctorId, LocalDate date) {
        Doctor doctor = doctorRepository.findById(doctorId).orElse(null);
        if (doctor == null) return Collections.emptyList();

        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(
                doctorId, date.atStartOfDay(), date.plusDays(1).atStartOfDay()
        );

        Set<LocalTime> bookedTimes = appointments.stream()
                .map(a -> a.getAppointmentTime().toLocalTime())
                .collect(Collectors.toSet());

        return doctor.getAvailableTimes().stream()
                .filter(time -> !bookedTimes.contains(time))
                .collect(Collectors.toList());
    }

    @Transactional
    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public int updateDoctor(Doctor doctor) {
        if (!doctorRepository.existsById(doctor.getId())) return -1;
        doctorRepository.save(doctor);
        return 1;
    }

    @Transactional
    public List<Doctor> getDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional
    public int deleteDoctor(Long doctorId) {
        if (!doctorRepository.existsById(doctorId)) return -1;
        appointmentRepository.deleteAllByDoctorId(doctorId);
        doctorRepository.deleteById(doctorId);
        return 1;
    }

    public String validateDoctor(String email, String password) {
        Doctor doctor = doctorRepository.findByEmail(email);
        if (doctor == null || !doctor.getPassword().equals(password)) {
            return "Invalid credentials";
        }
        return tokenService.generateToken(doctor.getId(), "doctor");
    }

    @Transactional
    public List<Doctor> findDoctorByName(String name) {
        return doctorRepository.findByNameLike(name);
    }

    @Transactional
    public List<Doctor> filterDoctorsByNameSpecilityandTime(String name, String specialty, String timePeriod) {
        List<Doctor> doctors = doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        return filterDoctorByTime(doctors, timePeriod);
    }

    public List<Doctor> filterDoctorByTime(List<Doctor> doctors, String timePeriod) {
        return doctors.stream()
                .filter(doc -> doc.getAvailableTimes().stream().anyMatch(time -> {
                    return "AM".equalsIgnoreCase(timePeriod) ? time.isBefore(LocalTime.NOON) : time.isAfter(LocalTime.NOON);
                }))
                .collect(Collectors.toList());
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndTime(String name, String timePeriod) {
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        return filterDoctorByTime(doctors, timePeriod);
    }

    @Transactional
    public List<Doctor> filterDoctorByNameAndSpecility(String name, String specialty) {
        return doctorRepository.findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
    }

    @Transactional
    public List<Doctor> filterDoctorByTimeAndSpecility(String specialty, String timePeriod) {
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        return filterDoctorByTime(doctors, timePeriod);
    }

    @Transactional
    public List<Doctor> filterDoctorBySpecility(String specialty) {
        return doctorRepository.findBySpecialtyIgnoreCase(specialty);
    }

    @Transactional
    public List<Doctor> filterDoctorsByTime(String timePeriod) {
        List<Doctor> doctors = doctorRepository.findAll();
        return filterDoctorByTime(doctors, timePeriod);
    }
}
