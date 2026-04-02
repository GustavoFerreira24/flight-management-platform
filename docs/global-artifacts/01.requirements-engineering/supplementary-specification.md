# <span style="color:#E67E22">Supplementary Specification – AlSafe System</span>

---

## <span style="color:#2E75B6">1. Introduction</span>

This document defines the non-functional requirements, technical constraints, and design considerations of the AlSafe system, complementing the User Stories and the Use Case Diagram.

---

## <span style="color:#2E75B6">2. Usability Requirements</span>

- **Remote Access**  
  The system must allow remote access through TCP-based client applications for actors such as Weather Person, ATCC, and Pilots.

- **Real-Time Visualization**  
  Simulated flight monitoring must be available through a web interface, using AJAX to provide automatic updates without requiring page reload.

---

## <span style="color:#2E75B6">3. Reliability Requirements</span>

- **Semantic Validation**  
  All semantic errors detected during flight plan processing must be collected and reported in a single execution.

- **Data Integrity**  
  All interactions with the system via remote applications must occur exclusively through TCP connections. Direct database access is strictly prohibited.

---

## <span style="color:#2E75B6">4. Performance Requirements</span>

- **Scalability**  
  The system must support a large number of simultaneous flights by enabling parallel simulation through the division of airspace into subareas.

- **Concurrency in C**  
  The simulation component must use processes, threads, pipes, and shared memory to ensure efficient data coordination.

---

## <span style="color:#2E75B6">5. Technical Constraints and Supportability</span>

- **Programming Languages (NFR10)**  
  The main solution must be implemented in Java, with simulation and physics components developed in C.

- **Persistence (NFR08)**  
  The system must support both in-memory persistence and relational database systems (RDBMS). A final deployment must include a remote persistent database.

- **Unit Test Coverage (NFR03)**  
  Unit test coverage for domain and controller packages must not be below 90%.

- **DSL Processing**  
  The DSL must be processed using ANTLR, producing an internal representation (AST).

---

## <span style="color:#2E75B6">6. Development Process and Constraints</span>

- **Project Management (NFR01)**  
  The project follows the Scrum methodology, with defined sprints and weekly meetings with the Scrum Master.

- **Source Control (NFR04)**  
  All source code, documentation, and artifacts must be versioned using GitHub. Only the main branch is used for releases.

- **Continuous Integration (NFR05 & NFR06)**  
  The repository must support automated builds, with commits ensuring the system remains in a valid state and passes all tests.

- **Technical Documentation (NFR02)**  
  Documentation must be maintained in the repository (docs folder), using Markdown and UML where applicable.

- **Build and Deployment (NFR07)**  
  The system must include scripts for building and deploying in a Linux environment, along with a README explaining execution steps.

- **Diagram Generation**  
  Diagrams should be created using PlantUML and stored both as source and PNG format.

---
