# =============================================================================
# Containerfile (Dockerfile) — Student Service
# =============================================================================
# Multi-stage build optimizado para produccion.
# Compatible con Podman y Docker (formato OCI estandar).
# Stage 1 (builder): compila y genera el JAR ejecutable con Gradle wrapper.
# Stage 2 (runtime): imagen minima con JRE 17 Alpine, sin codigo fuente.
#
# Build  : podman build -t student-service:local .
# Run    : podman run -p 8080:8080 student-service:local
#
# Proyecto : Scotiabank Reactive Challenge
# Group    : com.scotiabank.challenge
# Version  : 0.0.1-SNAPSHOT
# Puerto   : 8080 (configurado en application.properties)
# =============================================================================

# -----------------------------------------------------------------------------
# Stage 1: Build
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

# Copiar archivos de configuracion de Gradle primero (optimiza cache de capas)
COPY gradlew gradlew.bat ./
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# Dar permisos y descargar dependencias (cache independiente al codigo fuente)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon --quiet || true

# Copiar codigo fuente y recursos de configuracion de calidad
COPY src/ src/
COPY config/ config/

# Generar JAR ejecutable (omite tests; ya los ejecuta el pipeline de CI)
RUN ./gradlew bootJar --no-daemon -x test

# -----------------------------------------------------------------------------
# Stage 2: Runtime
# -----------------------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine AS runtime

# Metadatos de imagen
LABEL maintainer="Scotiabank Reactive Challenge"
LABEL org.opencontainers.image.title="student-service"
LABEL org.opencontainers.image.description="Microservicio reactivo de gestion de alumnos"
LABEL org.opencontainers.image.version="0.0.1-SNAPSHOT"
LABEL org.opencontainers.image.vendor="com.scotiabank.challenge"

# Usuario no-root por seguridad (OWASP: evitar ejecucion como root)
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copiar unicamente el JAR final desde el stage de build
COPY --from=builder /workspace/build/libs/student-service-*.jar app.jar

# Asignar ownership al usuario no-root
RUN chown appuser:appgroup app.jar

USER appuser

# Puerto expuesto (alineado con server.port en application.properties)
EXPOSE 8080

# Health check via Spring Actuator (management.endpoints.web.exposure.include=health)
HEALTHCHECK --interval=30s --timeout=5s --start-period=20s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health || exit 1

# Punto de entrada con parametros JVM razonables para contenedor
ENTRYPOINT ["java", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
