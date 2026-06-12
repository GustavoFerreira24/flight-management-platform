# <span style="color:#E67E22">Project AIControl</span>

## <span style="color:#2E75B6">1. Description of the Project</span>

AIControl is an integrative project developed in the 4th semester of the Licenciatura em Engenharia Informática  at ISEP, integrating the course units EAPLI, LPROG, RCOMP, and SCOMP.

The system is a back-office management platform developed for **AISafe**, a startup targeting the aircraft flight control market. The platform covers three main areas:

- **<span style="color:#70AD47">Basic configuration</span>** — registration and management of aircraft models,
  engine models, manufacturers, air transport companies, airports, air
  control areas, and users.

- **<span style="color:#70AD47">Flight management</span>** — creation and management of flight routes and
  flight plans, including import and validation of flight plans described
  in a custom **Domain Specific Language (DSL)** processed with ANTLR.

- **<span style="color:#70AD47">Flight simulation</span>** — simulation of individual flight plans (validating
  fuel load and altitude requirements) and full air control area simulations,
  detecting potential safety violations across multiple concurrent flights.

The solution is implemented primarily in **Java**, with simulation components in **C**. Networking features expose functionality via **TCP** and **UDP**. The project follows **Domain-Driven Design (DDD)**, **SCRUM**, and a **Test-Driven Development (TDD)** approach, with JPA/ORM for data persistence.

---

## <span style="color:#2E75B6">2. Planning and Technical Documentation</span>

[Planning and Technical Documentation](docs/readme.md)

---

## <span style="color:#2E75B6">3. How to Build</span>

    mvn clean install

---

## <span style="color:#2E75B6">4. How to Execute Tests</span>

    mvn test

---

## <span style="color:#2E75B6">5. How to Run</span>

    mvn exec:java

### <span style="color:#70AD47">RCOMP Remote Access</span>

Start the Remote Access Logging Server:

    mvn -pl aisafe.app.rcomp -am exec:java -Dexec.mainClass=isep.aisafe.app.rcomp.remoteaccess.RemoteAccessLoggingServer -Dexec.args="9001 8080"

Start the Remote Access TCP Server:

    mvn -pl aisafe.app.rcomp -am exec:java -Dexec.mainClass=isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpServer -Dexec.args="9000 localhost 9001"

Start a TCP client:

    mvn -pl aisafe.app.rcomp -am exec:java -Dexec.mainClass=isep.aisafe.app.rcomp.remoteaccess.RemoteAccessTcpClient -Dexec.args="localhost 9000 US44"

Use `US44`, `US78` or `US86` as the last client argument depending on the remote service to test.

Logging dashboards:

    http://localhost:8080/events
    http://localhost:8080/active-users

---

## <span style="color:#2E75B6">6. How to Install/Deploy into Another Machine (or Virtual Machine)</span>

### <span style="color:#70AD47">Prerequisites</span>

- Java 21+
- Maven 3.8+
- A relational database (e.g. PostgreSQL or MySQL) running and accessible
- (For simulation components) GCC compiler with POSIX support (Linux only)

### <span style="color:#70AD47">Steps</span>

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

---

## <span style="color:#2E75B6">7. How to Generate PlantUML Diagrams</span>

To generate plantuml diagrams for documentation execute the script (for the moment, only for linux/unix/macos):

    ./generate-plantuml-diagrams.sh
