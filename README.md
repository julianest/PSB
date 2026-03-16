# Student Service

## Identificacion inicial del proyecto

Proyecto backend reactivo construido con Spring Boot para el reto **Scotiabank Reactive Challenge**.

- Nombre de proyecto Gradle: `student-service`
- Group: `com.scotiabank.challenge`
- Version: `0.0.1-SNAPSHOT`
- Clase principal: `student_service.Application`
- Lenguaje: Java 17 (toolchain)

## Stack tecnologico

- Java 17
- Spring Boot 3.4.1
- Spring WebFlux
- Spring Data R2DBC
- Base de datos H2 (en memoria) con driver R2DBC
- Validacion con `spring-boot-starter-validation`
- OpenAPI/Swagger (`springdoc-openapi-starter-webflux-ui`)
- MapStruct + Lombok

## Build y dependencias (desde build.gradle)

### Plugins

- `java`
- `org.springframework.boot` `3.4.1`
- `io.spring.dependency-management` `1.1.7`
- `jacoco`
- `org.sonarqube` `4.4.1.3373`
- `com.github.spotbugs` `6.0.7`

### Dependencias clave

- API reactiva: `spring-boot-starter-webflux`
- Persistencia reactiva: `spring-boot-starter-data-r2dbc`
- Base de datos: `com.h2database:h2`, `io.r2dbc:r2dbc-h2`
- Serializacion JSON de fechas: `jackson-datatype-jsr310`
- Documentacion API: `org.springdoc:springdoc-openapi-starter-webflux-ui:2.5.0`
- Mapeo DTO/entidad: `org.mapstruct:mapstruct:1.5.5.Final`
- Boilerplate reduction: `org.projectlombok:lombok`
- Testing: `spring-boot-starter-test`, `reactor-test`, `junit-platform-launcher`

## Calidad, cobertura y analisis estatico

- **JaCoCo** habilitado (`0.8.10`) para reportes XML y HTML.
- **SonarQube** configurado con:
  - `sonar.projectKey=bank-test`
  - `sonar.host.url=http://localhost:9000`
  - Reporte de cobertura en `build/reports/jacoco/test/jacocoTestReport.xml`
- **SpotBugs** habilitado con reportes HTML.

## Estructura principal del codigo

- `src/main/java/student_service/Application.java`
- `src/main/java/student_service/config/OpenApiConfig.java`
- `src/main/resources/application.properties` (actualmente vacio)
- `src/test/java/student_service/ApplicationTests.java`

## Comandos utiles

```bash
# Ejecutar pruebas
./gradlew test

# Generar reporte de cobertura
./gradlew jacocoTestReport

# Verificar calidad (incluye cobertura configurada)
./gradlew check

# Ejecutar SpotBugs
./gradlew spotbugsMain spotbugsTest
```

En Windows PowerShell o CMD, usar `gradlew.bat` en lugar de `./gradlew`.

## Hallazgos iniciales de la revision

1. El comentario del build indica una cobertura minima esperada de 80%, pero la regla real de JaCoCo esta en `0.00`.
2. SonarQube apunta a `localhost:9000`, adecuado para entorno local, pero requiere ajuste para CI/CD compartido.
3. El archivo `application.properties` principal esta vacio; aun no hay configuracion de runtime declarada.
4. `HELP.md` sugiere que el paquete original `student-service` se normalizo a `student_service`, lo cual ya esta aplicado en codigo.

## Estado de identificacion

Este README deja una base de **identificacion tecnica inicial** del servicio para onboarding, auditoria y siguientes iteraciones (dominio, endpoints, persistencia y pruebas funcionales).
