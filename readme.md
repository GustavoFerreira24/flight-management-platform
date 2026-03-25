# Project AIControl

## 1. Description of the Project

AIControl is an integrative project developed in the 4th semester of the Licenciatura em Engenharia Informática  at ISEP, integrating the course units EAPLI, LPROG, RCOMP, and SCOMP.

The system is a back-office management platform developed for **AISafe**, a startup targeting the aircraft flight control market. The platform covers three main areas:

- **Basic configuration** — registration and management of aircraft models,
  engine models, manufacturers, air transport companies, airports, air
  control areas, and users.
- **Flight management** — creation and management of flight routes and
  flight plans, including import and validation of flight plans described
  in a custom **Domain Specific Language (DSL)** processed with ANTLR.
- **Flight simulation** — simulation of individual flight plans (validating
  fuel load and altitude requirements) and full air control area simulations,
  detecting potential safety violations across multiple concurrent flights.

The solution is implemented primarily in **Java**, with simulation components in **C** . Networking features expose functionality via **TCP**  and **UDP** . The project follows **Domain-Driven Design (DDD)**, **SCRUM**, and a **Test-Driven Development (TDD)** approach, with JPA/ORM for data persistence.

## 2. Planning and Technical Documentation

[Planning and Technical Documentation](docs/readme.md)

## 3. How to Build

    mvn clean install

## 4. How to Execute Tests

    mvn test

## 5. How to Run

    mvn exec:java

## 6. How to Install/Deploy into Another Machine (or Virtual Machine)

### Prerequisites
- Java 21+
- Maven 3.8+
- A relational database (e.g. PostgreSQL or MySQL) running and accessible
- (For simulation components) GCC compiler with POSIX support (Linux only)

### Steps

1. Clone the repository:

       git clone <https://github.com/Departamento-de-Engenharia-Informatica/sem4pi2526-sem4pi2526_2di3.git>

2. Configure the database connection in `application.properties` (or the
   equivalent configuration file), setting the correct URL, username and
   password.

3. Build the project:

       mvn clean install

4. Run the database bootstrap to initialize default data:

       mvn exec:java -Dexec.mainClass="<bootstrap-main-class>"

5. Deploy and run the application:

       mvn exec:java

## 7. How to Generate PlantUML Diagrams

To generate plantuml diagrams for documentation execute the script (for the moment, only for linux/unix/macos):

    ./generate-plantuml-diagrams.sh


