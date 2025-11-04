// doctorCard.js

import { showBookingOverlay } from './loggedPatient.js';
import { deleteDoctor } from './doctorServices.js';
import { fetchPatientDetails } from './patientServices.js';

export function createDoctorCard(doctor) {
  const role = localStorage.getItem("userRole");
  const token = localStorage.getItem("token");

  // Main card container
  const card = document.createElement("div");
  card.className = "doctor-card";

  // Doctor info section
  const info = document.createElement("div");
  info.className = "doctor-info";

  const name = document.createElement("h3");
  name.textContent = doctor.name;

  const specialty = document.createElement("p");
  specialty.textContent = `Specialty: ${doctor.specialty}`;

  const email = document.createElement("p");
  email.textContent = `Email: ${doctor.email}`;

  const timeList = document.createElement("ul");
  timeList.textContent = "Available Times:";
  doctor.availableTimes?.forEach(time => {
    const li = document.createElement("li");
    li.textContent = time;
    timeList.appendChild(li);
  });

  info.append(name, specialty, email, timeList);

  // Action buttons section
  const actions = document.createElement("div");
  actions.className = "doctor-actions";

  // === ADMIN ROLE ===
  if (role === "admin") {
    const deleteBtn = document.createElement("button");
    deleteBtn.textContent = "Delete";
    deleteBtn.className = "adminBtn";
    deleteBtn.onclick = async () => {
      const adminToken = localStorage.getItem("token");
      try {
        const result = await deleteDoctor(doctor.id, adminToken);
        alert(result.message || "Doctor deleted successfully.");
        card.remove();
      } catch (err) {
        alert("Error deleting doctor.");
      }
    };
    actions.appendChild(deleteBtn);
  }

  // === PATIENT (NOT LOGGED-IN) ===
  else if (role === "patient") {
    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";
    bookBtn.className = "adminBtn";
    bookBtn.onclick = () => {
      alert("Please log in to book an appointment.");
    };
    actions.appendChild(bookBtn);
  }

  // === LOGGED-IN PATIENT ===
  else if (role === "loggedPatient") {
    const bookBtn = document.createElement("button");
    bookBtn.textContent = "Book Now";
    bookBtn.className = "adminBtn";
    bookBtn.onclick = async () => {
      if (!token) {
        alert("Session expired. Please log in again.");
        window.location.href = "/";
        return;
      }
      try {
        const patient = await fetchPatientDetails(token);
        showBookingOverlay(doctor, patient);
      } catch (err) {
        alert("Unable to fetch patient details.");
      }
    };
    actions.appendChild(bookBtn);
  }

  // Final assembly
  card.append(info, actions);
  return card;
}
