# LeadOS Factory v0.1

LeadOS Factory es un compilador declarativo de proyectos para ingeniería de software asistida por IA.

## Alcance actual

El primer corte vertical se mantiene deliberadamente pequeño:

```text
Blueprint YAML -> JSON Schema -> Validación semántica -> AST -> Plan de ejecución -> Artefactos
```

Comandos implementados:

```bash
factory validate examples/leados.yaml
factory plan examples/leados.yaml
factory generate examples/leados.yaml --out .factory-output
```

## Restricciones de diseño

- YAML es el formato de entrada de v1; un DSL propio queda explícitamente pospuesto.
- La representación interna es TypeScript tipado, no YAML sin procesar.
- La validación semántica rechaza dependencias desconocidas, dependencias de sí mismo y ciclos.
- La generación produce artefactos; el código fuente es solo un posible tipo de artefacto.
- La publicación externa (GitHub/Notion) está separada deliberadamente del compilador principal.

## Runtime v0.1

El Runtime ejecuta el plan respetando dependencias y permite paralelizar tareas independientes mediante workers declarativos.

```text
AST -> Plan -> Scheduler -> Workers -> Artefactos
```

El worker inicial es `ModuleWorker`, utilizado para validar el contrato de ejecución antes de conectar agentes de IA.

## Desarrollo local

```bash
pnpm install
pnpm --dir factory build
pnpm --dir factory test
pnpm --dir factory validate
pnpm --dir factory plan
pnpm --dir factory generate
```

El paquete está diseñado para utilizar herramientas gratuitas y abiertas. El compilador central no requiere ningún servicio de pago.
