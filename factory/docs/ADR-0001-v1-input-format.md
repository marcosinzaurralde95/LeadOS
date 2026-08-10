# ADR-0001 — Formato de entrada declarativo de v1

## Estado

Aceptado

## Decisión

LeadOS Factory v1 utiliza YAML como formato declarativo de proyectos, validado mediante JSON Schema.

## Alternativa rechazada

Un DSL propio de Factory queda pospuesto hasta que el contrato YAML demuestre una limitación real de usabilidad.

## Justificación

Un lenguaje propio introduciría lexer, parser, versionado de gramática, reglas de compatibilidad y una superficie de pruebas mayor antes de que Factory pueda generar artefactos útiles. YAML ofrece legibilidad humana, herramientas maduras e interoperabilidad directa con CI/CD y agentes de desarrollo basados en LLM.

La arquitectura interna permanece independiente del parser: YAML se convierte en un AST tipado antes de la planificación o generación. Un DSL futuro podrá apuntar al mismo AST sin modificar el contrato del Runtime.

## Consecuencias

Positivas:
- entrega más rápida de v0.1;
- menor coste de mantenimiento;
- edición sencilla por humanos y máquinas;
- ausencia de vendor lock-in;
- posibilidad de incorporar un DSL posteriormente.

Negativas:
- YAML es menos expresivo que un lenguaje específico;
- las restricciones semánticas deben implementarse por separado del JSON Schema.
