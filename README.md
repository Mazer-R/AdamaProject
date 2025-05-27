
Adama is a personal-student project developed to manage product ownership in a warehouse environment.

It allows users to request products, which must go through a hierarchical approval process based on user roles within the organization.
The system also includes user management, internal messaging, and JWT-based authentication.

Key Features:

	•	Product request and assignment workflow.
	•	Hierarchical validation of requests based on roles.
	•	User management: creation, editing, and role assignment.
	•	Internal messaging system between users.
	•	JWT-based authentication and authorization.
	•	Modular architecture inspired by microservices.

Technologies Used:
Backend:

	•	Java 21
	•	Spring Boot
	•	Spring Security
	•	Spring Data JPA
	•	Hibernate Validator
	•	JUnit + Mockito (for testing)
	•	Docker + Docker Compose

Frontend (separate repository):

	•	JavaFX: https://github.com/Mazer-R/Adama_UI

Database:

	•	PostgreSQL (Dockerized)

Installation and Execution:

	1.	Clone the repository:
 
git clone https://github.com/Mazer-R/adama.git

	2.	Navigate to the project root and start the database:
 
docker-compose up

	3.	Build and run the backend:
 
./gradlew clean build

./gradlew bootRun

	4.	Launch the JavaFX client from the Adama_UI repository.

Notes:

	•	The backend is organized into independent modules (auth, products, orders, users, messages), simulating a microservices architecture within a monorepo.
	•	For frontend connectivity, Ngrok was used to tunnel local services (default: http://localhost:8080) without modifying frontend configuration.

Testing:

The project includes unit and integration tests using JUnit and Mockito.
To run them:

	./gradlew test

Security:
The system uses JWT for authenticating and authorizing access to protected endpoints.
Tokens are generated upon login and must be included in the Authorization header for subsequent requests.

Project Structure:

	•	/auth: handles authentication and token generation
	•	/product: product management logic
	•	/order: product request and approval process
	•	/users: user and role management
	•	/messages: internal user messaging


=================================================================================
Adama – Gestor de productos de almacén / Warehouse Product Management System

Adama es una aplicación desarrollada como proyecto personal con el objetivo de gestionar la titularidad de productos dentro de un entorno de almacén.

Permite que los usuarios soliciten productos, los cuales deben pasar por un flujo de validación jerárquica, definido por los roles asignados en la organización.
El sistema también incorpora funcionalidades de administración de usuarios, mensajería interna y autenticación mediante JWT.

Características principales:

	•	Solicitud y asignación de productos a usuarios.
	•	Validación jerárquica de solicitudes según el rol del usuario.
	•	Gestión de usuarios: alta, edición y asignación de roles.
	•	Sistema de mensajería interna entre usuarios.
	•	Autenticación y autorización con JWT.
	•	Arquitectura modular orientada a microservicios.

Tecnologías utilizadas:
Backend:

	•	Java 21
	•	Spring Boot
	•	Spring Security
	•	Spring Data JPA
	•	Hibernate Validator
	•	JUnit + Mockito (pruebas)
	•	Docker + Docker Compose

Frontend (repositorio separado):

	•	JavaFX: https://github.com/Mazer-R/Adama_UI

Base de datos:

	•	PostgreSQL (contenedor Docker)

Instalación y ejecución:
1.	Clona este repositorio:
   
		git clone https://github.com/Mazer-R/adama.git

 
2.	Dirígete al directorio raíz del proyecto y levanta la base de datos con Docker Compose:
	
		docker-compose up
3.	Compila y ejecuta el backend:
   
		./gradlew clean build
		./gradlew bootRun
  	
4.	Ejecuta el cliente JavaFX desde su repositorio correspondiente (Adama_UI).

Notas:

	•	El backend está dividido en módulos independientes (auth, products, orders, users, messages), siguiendo una aproximación modular simulando una arquitectura de microservicios en un único repositorio.
	•	Para pruebas desde el frontend, se ha utilizado Ngrok con el fin de crear un túnel seguro sin necesidad de modificar la dirección de exposición local (por defecto, el backend corre en http://localhost:8080).

Pruebas:
El proyecto incluye pruebas unitarias e integración con JUnit y Mockito.
Para ejecutarlas:

	./gradlew test

Seguridad:

	El sistema utiliza JWT para autenticar y autorizar el acceso a los endpoints.
	Los tokens se generan tras el login y deben incluirse en la cabecera Authorization de cada solicitud.

Estructura del proyecto:

	•	/auth: autenticación y emisión de tokens JWT
	•	/product: lógica de gestión de productos
	•	/order: gestión de solicitudes de productos
	•	/users: administración de usuarios y roles
	•	/messages: mensajería interna entre usuarios
