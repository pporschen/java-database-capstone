// doctorDashboard.js

import { getAllAppointments } from "../services/patientServices.js";
import { createPatientRow } from "../components/patientRow.js";

// DOM references
const tableBody = document.getElementById("patientTableBody");
const searchBar = document.getElementById("searchBar");
const todayButton = document.getElementById("todayButton");
const datePicker = document.getElementById("datePicker");

let selectedDate = new Date().toISOString().split("T")[0]; // 'YYYY-MM-DD'
let token = localStorage.getItem("token");
let patientName = null;

// Search bar filtering
if (searchBar) {
  searchBar.addEventListener("input", () => {
    const input = searchBar.value.trim();
    patientName = input.length > 0 ? input : null;
    loadAppointments();
  });
}

// Today button logic
if (todayButton) {
  todayButton.addEventListener("click", () => {
    selectedDate = new Date().toISOString().split("T")[0];
    if (datePicker) datePicker.value = selectedDate;
    loadAppointments();
  });
}

// Date picker logic
if (datePicker) {
  datePicker.addEventListener("change", () => {
    selectedDate = datePicker.value;
    loadAppointments();
  });
}

// Load appointments based on filters
async function loadAppointments() {
  try {
    const appointments = await getAllAppointments(selectedDate, patientName, token);
    tableBody.innerHTML = "";

    if (!appointments || appointments.length === 0) {
      tableBody.innerHTML = `
        <tr><td colspan="5" class="noPatientRecord">No Appointments found for today.</td></tr>
      `;
      return;
    }

    appointments.forEach(appt => {
      const patient = {
        id: appt.patientId,
        name: appt.patientName,
        phone: appt.patientPhone,
        email: appt.patientEmail,
      };
      const row = createPatientRow(patient, appt);
      tableBody.appendChild(row);
    });
  } catch (error) {
    console.error("Error loading appointments:", error);
    tableBody.innerHTML = `
      <tr><td colspan="5" class="noPatientRecord">Error loading appointments. Try again later.</td></tr>
    `;
  }
}

// Initialize dashboard
document.addEventListener("DOMContentLoaded", () => {
  renderContent();
  loadAppointments();
});
