---
description: Checklist de verificacion de estilo, indentacion y buenas practicas para Java/Spring WebFlux en este repositorio.
applyTo: "**/*.java"
---

# Code Style Verification Checklist

Usar este checklist antes de aprobar cambios o cerrar tareas.

## 1) Formato e indentacion

- Indentacion consistente de 4 espacios (sin mezclar tabs y espacios).
- Llaves y bloques con formato consistente en todo el archivo.
- Una clase publica por archivo.
- Lineas razonables y legibles; extraer metodos si el bloque se vuelve denso.

## 2) Convenciones de nombres

- Clases en `PascalCase`.
- Metodos y variables en `camelCase`.
- Constantes en `UPPER_SNAKE_CASE`.
- Nombres con intencion de dominio, evitar abreviaturas ambiguas.

## 3) Estructura y limpieza de codigo

- Metodos pequenos y enfocados en una sola responsabilidad.
- Sin duplicacion de logica (DRY).
- Sin complejidad accidental ni sobreingenieria (KISS + YAGNI).
- Dependencias entre capas respetan arquitectura limpia.

## 4) Reglas reactivas (WebFlux)

- No usar `block()` ni llamadas bloqueantes equivalentes.
- Mantener pipelines `Mono/Flux` legibles y con manejo de errores explicito.
- Evitar side effects no controlados en operadores reactivos.

## 5) Validaciones y errores

- Validaciones de entrada claras y consistentes.
- Errores funcionales con mensajes accionables y sin exponer internals.
- Manejo centralizado de excepciones HTTP cuando aplique.

## 6) Pruebas con proposito

- Cada cambio funcional debe traer o ajustar pruebas.
- Probar comportamiento esperado, errores y casos borde.
- Evitar pruebas triviales sin valor de negocio.

## 7) Performance y consistencia

- Evitar transformaciones redundantes y consultas innecesarias.
- Priorizar consistencia en operaciones de escritura criticas.
- Revisar costo de validaciones y operadores en pipeline reactivo.

## 8) Checklist rapido de PR

- [ ] Compila sin warnings criticos.
- [ ] Pruebas relevantes pasan.
- [ ] No hay bloqueo en flujo reactivo.
- [ ] Se respetan principios SOLID, KISS, DRY, YAGNI.
- [ ] Codigo legible, mantenible y alineado con arquitectura.
