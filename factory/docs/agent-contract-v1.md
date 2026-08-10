# Contrato de Agentes v1

## Objetivo

Un agente de LeadOS Factory no es un prompt gigante. Es una especificación declarativa que describe una capacidad ejecutable por un worker.

```text
AgentSpec
  + Arquitectura
  + Estándares
  + Knowledge Packs
  + Tarea
        ↓
Contexto de ejecución
        ↓
Modelo/worker
        ↓
Artefactos
```

## Principios

1. El Runtime no depende de un proveedor de modelos.
2. El agente declara capacidades y permisos antes de ejecutar.
3. El contexto se compila a partir de fuentes versionadas.
4. Los secretos nunca forman parte de `AgentSpec`.
5. La salida esperada son artefactos verificables, no texto libre sin contrato.

## Campos mínimos

- `id`: identificador estable.
- `role`: rol operativo.
- `goal`: resultado esperado.
- `capabilities`: capacidades que puede ejecutar.
- `permissions`: superficies que puede modificar.
- `inputs`: entradas aceptadas.
- `outputs`: tipos de salida.
- `constraints`: reglas que no puede violar.

## Contexto

El compilador de contexto combina:

- especificación del agente;
- arquitectura del proyecto;
- estándares;
- conocimiento relevante;
- tarea concreta;
- contexto adicional, cuando existe.

Esto permite cambiar el modelo subyacente sin cambiar el contrato del agente.
