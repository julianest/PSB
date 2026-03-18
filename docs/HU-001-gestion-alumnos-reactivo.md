# HU-001 - Gestion de Alumnos Reactivo

## 1) Historia de Usuario

Como equipo de negocio/operaciones,
quiero registrar alumnos y consultar solo los alumnos activos,
para mantener informacion consistente y disponible para procesos academicos.

## 2) Objetivo simplificado

Construir una API reactiva minima que permita:

- Crear alumno con validaciones de consistencia.
- Evitar registros con `id` duplicado.
- Consultar listado de alumnos con estado activo.
- Mantener persistencia en memoria para pruebas y demostracion tecnica.

## 3) Alcance funcional (MVP)

Incluye:

- Endpoint reactivo para crear alumno.
- Endpoint reactivo para listar alumnos activos.
- Validaciones de campos de alumno.
- Manejo de error funcional por `id` repetido.
- Persistencia en H2 con R2DBC.
- Pruebas por capa: controller, service, repository.

No incluye:

- Autenticacion/autorizacion.
- Paginacion, ordenamiento y filtros avanzados.
- Integraciones externas.
- Persistencia productiva fuera de memoria.

## 4) Criterios base para iniciar desarrollo

1. Stack definido: Java 17 + Spring Boot 3.4.1+ + WebFlux + Gradle.
2. Arquitectura definida: Clean/Hexagonal pragmatica por capas.
3. Modelo minimo definido: `Alumno(id, nombre, apellido, estado, edad)`.
4. Estados permitidos: `ACTIVO`, `INACTIVO`.
5. Contrato inicial de errores funcionales y validacion acordado.
6. Convenciones de calidad habilitadas: JaCoCo, SpotBugs, SonarQube.

## 5) Criterios de Aceptacion (Gherkin simplificado)

### CA-01 Crear alumno exitosamente
Dado un alumno valido con `id` no existente
cuando consumo el endpoint de creacion
entonces el sistema registra el alumno y responde exitosamente.

### CA-02 Rechazar id duplicado
Dado un alumno con `id` ya registrado
cuando consumo el endpoint de creacion
entonces el sistema responde error funcional indicando que no se pudo grabar por `id` repetido.

### CA-03 Validar consistencia de datos
Dado un alumno con datos invalidos (campos vacios, edad invalida o estado invalido)
cuando consumo el endpoint de creacion
entonces el sistema responde error de validacion claro y accionable.

### CA-04 Listar solo alumnos activos
Dado alumnos activos e inactivos en persistencia
cuando consumo el endpoint de consulta
entonces el sistema retorna unicamente alumnos con estado `ACTIVO`.

### CA-05 Comportamiento reactivo sin bloqueo
Dado la implementacion del servicio
cuando se revisa el flujo reactivo
entonces no se usan operaciones bloqueantes (`block`, `toFuture().get`).

## 6) Actividades depuradas (plan de ejecucion)

### Fase 1 - Diseno y contratos

- Definir DTOs de entrada/salida para alumno.
- Definir reglas de validacion por campo.
- Definir respuestas de error funcional y de validacion.
- Documentar contratos HTTP minimos (request/response/codigos).

Entregable: contrato API base acordado.

### Fase 2 - Dominio y aplicacion

- Crear entidad y reglas de negocio de alumno.
- Implementar caso de uso de creacion con validacion de id duplicado.
- Implementar caso de uso de consulta de alumnos activos.
- Definir puertos de salida para persistencia.

Entregable: logica de negocio desacoplada de infraestructura.

### Fase 3 - Infraestructura reactiva

- Implementar repositorio reactivo con R2DBC.
- Configurar H2 en memoria.
- Implementar mapeos dominio <-> persistencia.
- Implementar adapters de entrada (controller WebFlux).

Entregable: endpoints operativos conectados a persistencia en memoria.

### Fase 4 - Manejo de errores y calidad

- Crear handler global para errores de dominio/validacion.
- Homologar mensajes de error y codigos HTTP.
- Validar que la implementacion cumpla SOLID, KISS, DRY, YAGNI.

Entregable: comportamiento consistente y mantenible.

### Fase 5 - Pruebas con proposito

- Controller tests: contrato HTTP y errores.
- Service/use case tests: reglas y casos borde (id duplicado, estado activo).
- Repository tests: persistencia y consulta reactiva.
- Verificar build con `gradlew.bat test`.

Entregable: evidencia de comportamiento, no solo cobertura.

## 7) Criterios de terminado (DoD de la HU)

- Endpoints funcionales segun criterios de aceptacion.
- Pruebas por capa ejecutando correctamente.
- Sin operaciones bloqueantes en flujo reactivo.
- Codigo alineado a arquitectura y principios de diseno.
- Documentacion minima actualizada (README e instrucciones del proyecto).

## 8) Riesgos y mitigacion temprana

- Riesgo: validaciones ambiguas de campos.
  Mitigacion: acordar reglas de validacion antes de codificar.

- Riesgo: inconsistencias en manejo de errores.
  Mitigacion: definir formato de error y handler central desde el inicio.

- Riesgo: pruebas centradas solo en cobertura.
  Mitigacion: diseñar escenarios de negocio primero y despues los asserts tecnicos.

## 9) Dependencias tecnicas

- Java 17 disponible localmente.
- Gradle Wrapper funcional.
- Dependencias WebFlux + R2DBC + H2 instaladas via build.gradle.
- Entorno local con capacidad de ejecutar pruebas.
