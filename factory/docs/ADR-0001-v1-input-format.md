# ADR-0001 — v1 blueprint input format

## Status

Accepted

## Decision

LeadOS Factory v1 uses YAML as its declarative project format, validated by JSON Schema.

## Rejected alternative

A custom Factory DSL is deferred until the YAML contract demonstrates a real usability limitation.

## Rationale

A custom language would introduce a lexer, parser, grammar versioning, compatibility rules and a larger testing surface before Factory can generate useful artifacts. YAML provides human readability, mature tooling and straightforward interoperability with CI/CD and LLM-based development agents.

The internal architecture remains parser-independent: YAML is converted to a typed AST before planning or generation. A future DSL can therefore target the same AST without changing the Runtime contract.

## Consequences

Positive:
- faster v0.1 delivery;
- lower maintenance cost;
- easy machine and human authoring;
- no vendor lock-in;
- future DSL remains possible.

Negative:
- YAML is less expressive than a purpose-built language;
- semantic constraints must be implemented separately from JSON Schema.
