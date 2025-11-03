# 1.  Architecture summary
This Spring Boot application integrates both MVC and RESTful controllers. 
The Admin and Doctor dashboards are rendered using Thymeleaf templates, whereas the remaining modules are exposed via RESTful APIs.
It connects to two distinct databases: MySQL, which stores patient, doctor, appointment, and admin records, and MongoDB, which handles prescription data.
All incoming requests are funneled through a unified service layer that delegates operations to the corresponding repositories. MySQL leverages JPA entities, while MongoDB utilizes document-based models.

# 2.  Numbered flow of data and control
1. A user interacts with the system via either a Thymeleaf-based dashboard (e.g., AdminDashboard or DoctorDashboard) or through REST API clients (e.g., Appointment or Patient modules).

2. The request is routed to the appropriate controller—Thymeleaf controllers handle server-rendered views, while REST controllers process API calls.

3. The controller delegates the request to the service layer, which contains the core business logic and orchestrates workflows.

4. The service layer interacts with the repository layer to perform data access operations.

5. Repositories connect to either MySQL (for structured data like patients and appointments) or MongoDB (for flexible data like prescriptions).

6. Retrieved data is mapped into Java model classes—JPA entities for MySQL and document models for MongoDB.

7. These models are returned to the user: rendered as HTML in Thymeleaf views or serialized as JSON in REST API responses.
