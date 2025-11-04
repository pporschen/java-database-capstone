// index.js

import { openModal } from './util.js';
import { BASE_API_URL } from './config.js';

const ADMIN_API = `${BASE_API_URL}/admin/login`;
const DOCTOR_API = `${BASE_API_URL}/doctor/login`;

window.onload = () => {
  const adminLoginBtn = document.getElementById("adminLogin");
  const doctorLoginBtn = document.getElementById("doctorLogin");

  if (adminLoginBtn) {
    adminLoginBtn.addEventListener("click", () => openModal("adminLogin"));
  }

  if (doctorLoginBtn) {
    doctorLoginBtn.addEventListener("click", () => openModal("doctorLogin"));
  }
};

// Admin Login Handler
window.adminLoginHandler = async function () {
  const username = document.getElementById("adminUsername").value;
  const password = document.getElementById("adminPassword").value;

  const admin = { username, password };

  try {
    const response = await fetch(ADMIN_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(admin),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      localStorage.setItem("userRole", "admin");
      selectRole("admin");
    } else {
      alert("Invalid admin credentials. Please try again.");
    }
  } catch (error) {
    console.error("Admin login error:", error);
    alert("Something went wrong. Please try again later.");
  }
};

// Doctor Login Handler
window.doctorLoginHandler = async function () {
  const email = document.getElementById("doctorEmail").value;
  const password = document.getElementById("doctorPassword").value;

  const doctor = { email, password };

  try {
    const response = await fetch(DOCTOR_API, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(doctor),
    });

    if (response.ok) {
      const data = await response.json();
      localStorage.setItem("token", data.token);
      localStorage.setItem("userRole", "doctor");
      selectRole("doctor");
    } else {
      alert("Invalid doctor credentials. Please try again.");
    }
  } catch (error) {
    console.error("Doctor login error:", error);
    alert("Something went wrong. Please try again later.");
  }
};
