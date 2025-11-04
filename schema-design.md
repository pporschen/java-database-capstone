## MySQL Database Design

### Table: patients
- id: INT, Primary Key, AUTO_INCREMENT
- full_name: VARCHAR(100), NOT NULL
- email: VARCHAR(100), NOT NULL, UNIQUE
- phone: VARCHAR(20), NOT NULL
- password_hash: VARCHAR(255), NOT NULL
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP

#### Notes:
- Email and phone formats should be validated in application logic.
- Deleting a patient should cascade to delete their appointments.


### Table: doctors
- id: INT, Primary Key, AUTO_INCREMENT
- full_name: VARCHAR(100), NOT NULL
- email: VARCHAR(100), NOT NULL, UNIQUE
- phone: VARCHAR(20), NOT NULL
- specialization: VARCHAR(100), NOT NULL
- contact_info: TEXT
- password_hash: VARCHAR(255), NOT NULL
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP

#### Notes:
- Prevent overlapping appointments via application logic or unique constraints.
- Doctors can be linked to clinic_locations if needed.


### Table: appointments
- id: INT, Primary Key, AUTO_INCREMENT
- doctor_id: INT, Foreign Key → doctors(id), NOT NULL
- patient_id: INT, Foreign Key → patients(id), NOT NULL
- appointment_time: DATETIME, NOT NULL
- status: INT, NOT NULL DEFAULT 0  # 0 = Scheduled, 1 = Completed, 2 = Cancelled
- notes: TEXT

#### Notes:
- Use composite UNIQUE(doctor_id, appointment_time) to prevent double-booking.
- ON DELETE CASCADE for both foreign keys to clean up orphaned records.


### Table: admin
- id: INT, Primary Key, AUTO_INCREMENT
- username: VARCHAR(50), NOT NULL, UNIQUE
- email: VARCHAR(100), NOT NULL, UNIQUE
- password_hash: VARCHAR(255), NOT NULL
- created_at: DATETIME, DEFAULT CURRENT_TIMESTAMP

#### Notes:
- Admins manage doctors and system data.
- Authentication handled similarly to patients and doctors.


### Table: clinic_locations
- id: INT, Primary Key, AUTO_INCREMENT
- name: VARCHAR(100), NOT NULL
- address: TEXT, NOT NULL
- phone: VARCHAR(20)
- email: VARCHAR(100)

#### Notes:
- Optional table to support multi-location clinics.
- Doctors can be assigned via a location_id foreign key.


### Table: payments
- id: INT, Primary Key, AUTO_INCREMENT
- appointment_id: INT, Foreign Key → appointments(id), NOT NULL
- amount: DECIMAL(10,2), NOT NULL
- payment_method: VARCHAR(50)
- payment_status: VARCHAR(50), DEFAULT 'Pending'
- paid_at: DATETIME

#### Notes:
- Each payment is tied to a specific appointment.
- Use ON DELETE CASCADE to remove payments if the appointment is deleted.


## MongoDB Collection Design


### Collection: messages


```json
{
  "_id": "ObjectId('654def789abc')",
  "appointmentId": 102,
  "doctorId": 7,
  "patientId": 24,
  "timestamp": "2025-11-04T10:45:00Z",
  "sender": {
    "role": "doctor",
    "name": "Dr. Anna Becker"
  },
  "receiver": {
    "role": "patient",
    "name": "Patrick Müller"
  },
  "content": "Please remember to bring your lab results to the appointment.",
  "attachments": [
    {
      "fileName": "bloodwork_results.pdf",
      "fileType": "application/pdf",
      "url": "https://clinic-storage.example.com/files/bloodwork_results.pdf"
    }
  ],
  "tags": ["reminder", "lab", "follow-up"],
  "metadata": {
    "read": false,
    "priority": "normal"
  }
}
```


