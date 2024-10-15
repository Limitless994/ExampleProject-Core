Hi, I'm Davide Sacco, a full-stack developer and AWS Certified Solutions Architect. My passion for computer science and constant curiosity about emerging technologies have led me to develop significant experience as a full-stack developer. Currently, as a technical analyst, I focus on designing sophisticated microservices architectures. I aim to continue my journey toward becoming a Solutions Architect, leveraging my creativity and expertise to drive technological evolution in companies, always staying updated with the latest industry trends.

![image](https://github.com/user-attachments/assets/1172ec9a-ad69-421c-97a8-eb051f030db3)

System Architecture Overview
This architecture consists of multiple layers and services, seamlessly integrated for efficient application management and deployment:

Docker (Top Layer):
Docker is utilized as the primary containerization tool, allowing the various components of the system to run in isolated environments, simplifying deployment and scaling.

Keycloak (Users Management):
Keycloak handles authentication and authorization, providing secure user management for the system. It integrates with the backend to manage access control.

Spring Boot - Core:
The backend of the system is powered by Spring Boot. This is the main layer, which includes business logic, API handling, and integration with the databases. MapStruct is used to simplify object mapping, while other technologies such as Spring Security manage secure interactions.

Angular Front-end Layer (Work in Progress):
The front-end is being developed using Angular. It serves as the user interface, communicating with the Spring Boot backend to deliver a seamless experience.

Keycloak Database (Keycloak DB):
This PostgreSQL database stores the user information managed by Keycloak. It holds user credentials, roles, and permissions required for secure authentication.

Core Database:
Another PostgreSQL instance stores the application’s core data, ensuring that business operations and other critical information are handled efficiently.

Each of these components is designed to function independently while interacting seamlessly through APIs and shared protocols, offering both flexibility and scalability for future growth.
