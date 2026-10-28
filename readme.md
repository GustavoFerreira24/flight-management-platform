# Flight Management & Simulation Platform

Academic group project developed at **ISEP – Polytechnic of Porto** as part of the fourth-semester integrated project.

The system is a flight management and simulation platform designed for a fictional air-traffic technology company. It combines **software architecture, domain modelling, persistence, networking, domain-specific languages and concurrent systems programming** in a single multi-module application.

## Project Scope

The platform covers three main areas:

- **Flight Management** — management of aircraft, airports, air transport companies, flight routes and flight plans.
- **Flight Plan Validation** — creation, import and validation of flight plans, including a custom Domain Specific Language processed with ANTLR.
- **Flight Simulation** — simulation of multiple concurrent flights and detection of potential safety violations between aircraft.

The project also includes remote-access services and communication between different system components using TCP and UDP.

## Technical Highlights

### Software Architecture

- Domain-Driven Design (DDD)
- Layered and modular architecture
- Domain modelling and aggregate design
- JPA / ORM persistence
- Maven multi-module project
- UML and PlantUML documentation

### Flight Plan DSL

A custom Domain Specific Language was designed to represent and validate flight plans.

The DSL is parsed using **ANTLR**, allowing the application to transform structured flight-plan files into domain objects while validating their syntax and business rules.

### Flight Simulation

The simulation component was implemented in **C using POSIX APIs**.

It includes:

- one process per simulated flight;
- inter-process communication using pipes;
- process synchronization;
- POSIX shared memory;
- named semaphores;
- threads;
- mutexes and condition variables;
- signal handling;
- timestep synchronization;
- real-time aircraft safety monitoring.

The simulator detects current and predicted safety violations based on aircraft positions, altitude and movement.

### Networking

Remote-access functionality uses networking concepts including:

- TCP client/server communication
- UDP communication
- Remote service access
- Centralized event logging
- HTTP-based monitoring dashboards

### Persistence

The application uses **JPA/ORM** to manage persistent domain entities and separate persistence concerns from the business logic.

## Technologies

- Java
- C
- JPA / ORM
- ANTLR
- Maven
- TCP / UDP
- POSIX Processes & Threads
- Shared Memory
- Semaphores
- Git
- GitHub Actions
- PlantUML
- JUnit

## Architecture

The repository is organized as a multi-module Maven project separating the application's main responsibilities.

```text
aisafe.core/                        # Domain and application logic
aisafe.infrastructure.application/ # Infrastructure services
persistence.impl.jpa/              # JPA persistence implementation
aisafe.app.backoffice.console/     # Console back-office application
aisafe.app.backoffice.gui/         # Graphical back-office application
aisafe.app.rcomp/                   # Networking and remote access
scomp-simulation/                   # C/POSIX flight simulation
docs/                               # Requirements, analysis and design documentation
datasets/                           # Application datasets
```

## Testing

The project includes automated testing across multiple components.

Java modules use **JUnit** and Maven-based test execution, while the C simulation includes dedicated build and execution scenarios for normal operation and safety-violation cases.

The complete Java test suite can be executed with:

```bash
mvn test
```

The flight simulation can be built and executed independently using the provided Makefile.

## Documentation

Detailed technical documentation is available in the [`docs/`](docs/) directory and includes:

- Requirements engineering
- Domain model
- Aggregate design
- Sequence diagrams
- User-story analysis
- System design
- Networking components
- Flight DSL specification
- Simulation design

The flight simulation also contains dedicated documentation inside [`scomp-simulation/`](scomp-simulation/).

## Academic Context

This project was developed collaboratively by a student team at ISEP, integrating knowledge from several fourth-semester subjects including software engineering, programming languages, computer networks and computer systems.

This repository is a personal public copy of the original university project and preserves the original project structure, documentation and contributors for portfolio purposes.