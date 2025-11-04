// adminDashboard.js

import { openModal } from "../util.js";
import { getDoctors, filterDoctors, saveDoctor } from "../services/doctorServices.js";
import { createDoctorCard } from "../doctorCard.js";

// DOM references
const contentDiv = document.getElementById("content");
const searchBar = document.getElementById("searchBar");
const timeFilter = document.getElementById("timeFilter");
const specialtyFilter = document.getElementById("specialtyFilter");
const addDoctorBtn = document.getElementById("addDocBtn");

// Attach modal trigger
if (addDoctorBtn) {
  addDoctorBtn.addEventListener("click", () => openModal("addDoctor"));
}

// Load doctors on page ready
document.addEventListener("DOMContentLoaded", loadDoctorCards);

// Load all doctors and render them
async function loadDoctorCards() {
  try {
    const doctors = await getDoctors();
    renderDoctorCards(doctors);
  } catch (error) {
    console.error("Error loading doctors:", error);
  }
}

// Filter listeners
if (searchBar) searchBar.addEventListener("input", filterDoctorsOnChange);
if (timeFilter) timeFilter.addEventListener("change", filterDoctorsOnChange);
if (specialtyFilter) specialtyFilter.addEventListener("change", filterDoctorsOnChange);

// Filter doctors based on input
async function filterDoctorsOnChange() {
  const name = searchBar?.value?.trim() || null;
  const time = timeFilter?.value || null;
  const specialty = specialtyFilter?.value || null;

  try {
    const filtered = await filterDoctors(name, time, specialty);
    if (filtered.length > 0) {
      renderDoctorCards(filtered);
    } else {
      contentDiv.innerHTML = `<p class="noPatientRecord">No doctors found with the given filters.</p>`;
    }
  } catch (error) {
    alert("Failed to filter doctors. Please try again.");
    console.error("Filter error:", error);
  }
}

// Render doctor cards
function renderDoctorCards(doctors) {
  contentDiv.innerHTML = "";
  doctors.forEach(doc => {
    const card = createDoctorCard(doc);
    contentDiv.appendChild(card);
  });
}

// Add new doctor from modal form
window.adminAddDoctor = async function () {
  const name = document.getElementById("doctorName").value;
  const email = document.getElementById("doctorEmail").value;
  const phone = document.getElementById("doctorPhone").value;
  const password = document.getElementById("doctorPassword").value;
  const specialty = document.getElementById("doctorSpecialty").value;
  const timesRaw = document.getElementById("doctorTimes").value;

  const availableTimes = timesRaw.split(",").map(t => t.trim()).filter(Boolean);
  const token = localStorage.getItem("token");

  if (!token) {
    alert("Session expired. Please log in again.");
    return;
  }

  const doctor = { name, email, phone, password, specialty, availableTimes };

  try {
    const result = await saveDoctor(doctor, token);
    if (result.success) {
      alert("Doctor added successfully.");
      document.getElementById("modal").classList.add("hidden");
      loadDoctorCards();
    } else {
      alert(result.message || "Failed to add doctor.");
    }
  } catch (error) {
    console.error("Add doctor error:", error);
    alert("Something went wrong while adding the doctor.");
  }
};
