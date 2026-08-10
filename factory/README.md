# LeadOS Factory v0.1

LeadOS Factory is a declarative project compiler for AI-assisted software engineering.

## Current scope

The first vertical slice is intentionally small:

```text
YAML Blueprint -> JSON Schema -> Semantic Validation -> AST -> Execution Plan -> Artifacts
```

Implemented commands:

```bash
factory validate examples/leados.yaml
factory plan examples/leados.yaml
factory generate examples/leados.yaml --out .factory-output
```

## Design constraints

- YAML is the v1 input format; a custom DSL is explicitly deferred.
- The internal representation is typed TypeScript, not raw YAML.
- Semantic validation rejects unknown dependencies, self-dependencies and cycles.
- Generation produces artifacts; source code is only one possible artifact type.
- External publishing (GitHub/Notion) is intentionally separated from the core compiler.

## Local development

```bash
pnpm install
pnpm --dir factory build
pnpm --dir factory test
pnpm --dir factory validate
pnpm --dir factory plan
pnpm --dir factory generate
```

The package is designed to run with free/open tooling. No paid service is required by the core compiler.
