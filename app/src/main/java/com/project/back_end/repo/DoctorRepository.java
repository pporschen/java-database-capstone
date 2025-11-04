package com.project.back_end.repo;

import com.project.back_end.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Retrieve a doctor by email
    Doctor findByEmail(String email);

    // Retrieve doctors whose name contains the search string (case-sensitive)
    List<Doctor> findByNameLike(String name);

    // Retrieve doctors by name and specialty (case-insensitive)
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(String name, String specialty);

    // Retrieve doctors by specialty (case-insensitive)
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}
