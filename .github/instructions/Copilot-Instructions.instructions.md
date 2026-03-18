---
description: Instrucciones maestras del proyecto Student Service para generacion, revision y refactorizacion de codigo.
applyTo: "**"
---

# Copilot Instructions - Student Service

## 1) Contexto del reto

Este repositorio implementa un microservicio reactivo con Spring Boot para el challenge de Scotiabank.

- Endpoints implementados: POST /v1/api/students y GET /v1/api/students/active.
- Validacion funcional de id duplicado implementada en el caso de uso de creacion.
- Persistencia reactiva con H2 en memoria sobre R2DBC implementada.
- Handler global de errores implementado para conflicto, validacion e illegal argument.
- Filtros de logging reactivo de entrada/salida implementados.
- Suite de pruebas por capas activa (controller, use case, repository/adapter y filtros).

Requisitos funcionales minimos del challenge:

- Exponer endpoint reactivo para crear alumno con validaciones de consistencia.
- Si el `id` ya existe, responder error funcional claro (sin filtrar detalles internos).
- Exponer endpoint reactivo para listar alumnos con estado activo.
- Persistencia en memoria usando H2 (vía stack reactivo R2DBC).
- Mantener pruebas unitarias por capa (controller, service, repository).

## 2) Stack y restricciones tecnicas

- Java 17 (obligatorio).
- Spring Boot 3.4.1 o superior.
- Spring WebFlux (no mezclar con Spring MVC bloqueante).
- Gradle (Groovy DSL preferido en este repositorio).
- OpenAPI con springdoc-openapi-starter-webflux-ui (version actual en build.gradle).

## 3) Principios de diseño obligatorios

Aplicar siempre estos principios en codigo nuevo o refactorizaciones:

- SOLID:
	- SRP: una sola responsabilidad por clase/componente.
	- OCP: extender comportamiento con nuevas implementaciones, no con condicionales gigantes.
	- LSP: contratos coherentes entre interfaces e implementaciones.
	- ISP: interfaces pequenas, especificas por caso de uso.
	- DIP: casos de uso dependen de abstracciones del dominio, no de detalles de infraestructura.
- KISS: elegir la solucion mas simple que cumpla el requerimiento.
- DRY: eliminar duplicaciones en validaciones, mapeos y construccion de respuestas.
- YAGNI: no agregar features no solicitadas por el reto o por requerimientos actuales.

## 4) Arquitectura objetivo (Clean/Hexagonal pragmatica)

Usar capas claras y dependencia hacia adentro:

- `domain`: entidades, value objects, reglas de negocio, puertos.
- `application`: casos de uso y orquestacion de negocio.
- `infraestructure` (mantener nombre actual del repo): adapters de entrada/salida, persistencia, configuracion.

Implementacion observada en el repositorio:

- Entrada: controller, dto, filtros de request y handler global de excepciones.
- Salida: adapter de persistencia, repositorio R2DBC, mapeador dominio-entidad y filtro de response.

Reglas:

- Controllers no contienen logica de negocio; solo validan entrada superficial y delegan.
- Casos de uso concentran reglas de negocio y coordinan puertos.
- Repositorios/adapters convierten entre modelo de persistencia y dominio.
- Errores de dominio deben mapearse a respuestas HTTP consistentes en un handler central.

## 5) Estilo de implementacion reactiva

- Evitar `block()`, `toFuture().get()` y cualquier bloqueo en flujo reactivo.
- Encadenar operaciones con `Mono`/`Flux` y operadores expresivos.
- Separar validaciones sincrónicas de validaciones reactivas para mantener legibilidad.
- Propagar errores con tipos de excepcion de dominio y mensajes accionables.
- Mantener filtros reactivos compatibles con respuestas con body, streaming y sin body.

## 6) Calidad: pruebas con proposito

Las pruebas no se escriben solo para cobertura; deben validar comportamiento.

Minimo esperado:

- Controller tests: contrato HTTP, codigos de estado, payloads, errores de validacion.
- Service/use case tests: reglas de negocio, casos borde, id duplicado, filtro por estado activo.
- Repository tests: consultas reactivas, persistencia y recuperacion de datos esperada.
- Tests de adapters y filtros cuando se introduzcan responsabilidades de infraestructura adicionales.

Buenas practicas:

- Usar nombres descriptivos orientados a comportamiento (`debe...cuando...`).
- Incluir casos felices, casos de error y casos limite.
- Evitar mocks innecesarios cuando un test de integracion ligera aporta mas valor.

## 7) Performance y CAP (prioridad de consistencia)

Para este contexto de challenge, priorizar consistencia de datos (C) sobre disponibilidad eventual en operaciones criticas de escritura.

Lineamientos:

- Validar id duplicado con flujo consistente antes de persistir.
- Evitar operaciones costosas innecesarias en pipeline reactivo.
- Evitar conversiones redundantes de DTO <-> dominio <-> entidad.
- Limitar trabajo en controllers; mantener logica de negocio en servicios/casos de uso.
- Evitar lecturas duplicadas o materializacion completa de payloads salvo necesidad explicita de auditoria/log.

## 8) Convenciones de revision de codigo

En cada cambio revisar:

- Claridad de nombres (clases, metodos, variables, endpoints).
- Cohesion por capa y ausencia de acoplamiento accidental.
- Manejo de errores consistente.
- Cobertura de pruebas con intencion de negocio.
- Riesgos de performance y bloqueo en flujos reactivos.
- Consistencia del contrato API documentado en StudentApiDoc/OpenAPI con comportamiento real del controller.
- Coherencia entre validaciones de DTO y reglas de dominio/caso de uso.

## 9) Definition of Done para cambios

Antes de cerrar un cambio, validar:

- Compila y pasa pruebas (`gradlew.bat test` en Windows).
- No rompe arquitectura ni principios definidos.
- Incluye/ajusta pruebas relevantes al comportamiento modificado.
- Documenta decisiones no obvias en codigo o README si aplica.
- Si se modifica calidad/cobertura, verificar que `gradlew.bat check` siga cumpliendo el umbral minimo.
- Si se modifica logging/errores, verificar formato y semantica de respuesta HTTP.
- Si se modifica CI/CD, validar coherencia con `.github/workflows/ci.yml`, `sonar-project.properties` y `build.gradle`.
- Si se modifica publicacion de artefactos, verificar diferencia entre GitHub Actions Artifacts y GitHub Packages (Maven).

## 9.1) Reglas CI/CD del repositorio

- El analisis de SonarCloud en CI se ejecuta con scanner CLI via `SonarSource/sonarcloud-github-action`, no con `./gradlew sonarqube`.
- El job `artifact` debe ejecutarse solo en rama `main` y despues de pasar `sonar` y `quality-gate`.
- Las acciones de GitHub deben mantenerse en versiones compatibles con Node.js 24.
- Publicacion de paquete Maven en GitHub Packages se realiza via `./gradlew publish` usando `GITHUB_TOKEN`.
- No automatizar push a `main`; el flujo objetivo es commit/push a `develop` y merge controlado a `main`.

## 10) Alcance de estas instrucciones

Estas reglas aplican para:

- Generacion de codigo nuevo.
- Refactorizaciones.
- Revisiones de pull requests.
- Respuestas tecnicas sobre diseno, calidad, testing y performance.

## 11) Riesgos conocidos y mitigacion sugerida

- Riesgo de concurrencia en creacion de alumnos: la validacion de id duplicado es previa a persistencia (existsById + save). Mantener PK en base y, si se endurece consistencia funcional, mapear errores de clave duplicada a 409 en el handler global.
- Riesgo de memoria en logging: los filtros de request/response materializan body completo para registrar logs canonicos. Si se amplia alcance de payloads, aplicar truncado y limites de tamano.