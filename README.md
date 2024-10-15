# Hi, I'm Davide Sacco

I am a full-stack developer and AWS Certified Solutions Architect. My passion for computer science and constant curiosity about emerging technologies have led me to develop significant experience as a full-stack developer. Currently, as a technical analyst, I focus on designing sophisticated microservices architectures. I aim to continue my journey toward becoming a Solutions Architect, leveraging my creativity and expertise to drive technological evolution in companies, always staying updated with the latest industry trends.

---

![System Architecture](https://github.com/user-attachments/assets/1172ec9a-ad69-421c-97a8-eb051f030db3)

### System Architecture Overview

This architecture consists of multiple layers and services, seamlessly integrated for efficient application management and deployment:

---

#### **Docker (Top Layer)**  
Docker is utilized as the primary containerization tool, allowing the various components of the system to run in isolated environments, simplifying deployment and scaling.

---

#### **Keycloak (Users Management)**  
Keycloak handles authentication and authorization, providing secure user management for the system. It integrates with the backend to manage access control.

---

#### **Spring Boot - Core**  
The backend of the system is powered by Spring Boot. This is the main layer, which includes business logic, API handling, and integration with the databases.  
- **MapStruct** is used to simplify object mapping.  
- **Spring Security** manages secure interactions within the backend.

---

#### **Angular Front-end Layer (Work in Progress)**  
The front-end is being developed using Angular. It serves as the user interface, communicating with the Spring Boot backend to deliver a seamless user experience.

---

#### **Keycloak Database (Keycloak DB)**  
This **PostgreSQL** database stores the user information managed by Keycloak. It holds user credentials, roles, and permissions required for secure authentication.

---

#### **Core Database**  
Another **PostgreSQL** instance stores the application’s core data, ensuring that business operations and other critical information are handled efficiently.

---

Each of these components is designed to function independently while interacting seamlessly through APIs and shared protocols, offering both flexibility and scalability for future growth.

---

### How to Run the Project

1. **Run Docker Compose file**  
   - Using IntelliJ, you should have the run configurations ready. Simply run the Docker Compose file to start all containers.

2. **Wait for Keycloak DB Initialization**  
   - The Keycloak container will automatically stop initially because it depends on the Keycloak DB.  
   - Wait for **1 minute** to allow the Keycloak DB container to start properly.  
   - After 1 minute, manually start the Keycloak container again using Docker:
   ```bash
   docker start <keycloak-container-name>
   ```
3. **Stop the Java Core App (Optional)**  
   - If necessary, you can stop the Java core app during this waiting period to prevent errors:
   ```bash
   docker stop <java-core-app-container-name>
4. **Access Keycloak Control Panel**  
   - Once Keycloak is running, open your browser and navigate to:
   ```bash
   http://localhost:8080
   ```
    - **CREDENTIALS**: admin / admin
5. **Import the Realm Settings**  
   - In the Keycloak control panel, go to the administration console and import the realm settings file.  
   - The file is located in the `src/main/resources` directory of your project.

6. **Testing with Postman**  
   - You can now test the system using Postman.  
   - The Postman collections are available in the `resources` folder.  
   - Import these collections into Postman and use them to test the authentication and other API calls.

7. **Configure Java Application**  
   - Locate the local IP address of the PostgreSQL Core DB container.  
   - Once identified, update the `application.yml` file with this IP address for the Core DB connection.

8. **Run "core install" in IntelliJ**  
   - If you're using IntelliJ, run the **"core install"** configuration to generate the jar file for the Java application.  
   - If you're not using IntelliJ, create a new run configuration to build the jar file manually.

9. **Deploy the Java App Container**  
   - After generating the jar file, deploy the Java application container again by running the Docker command:
   ```bash
   docker start <java-core-app-container-name>
    ```
