# LeadOS Factory (Android)

Declarative project factory and blueprint compiler for AI-assisted software engineering, rewritten as a modern native Android application using Kotlin, Jetpack Compose, and Material 3.

## Overview

LeadOS Factory enables software engineers and AI agents to declaratively define, validate, plan, and generate production software architectures from `factory.leados.dev/v1` blueprints.

## Core Features

- **Declarative Blueprint Studio**: Visual form builder and live YAML/JSON syntax-highlighted editor for defining project metadata, tech stack, modular pipeline steps, and sync targets.
- **Blueprint Schema Validation**: Strict verification of naming conventions (`^[A-Za-z0-9_-]+$`), project types (`saas`, `api`, `library`, `agent`, `automation`), required stack parameters, and unique module dependencies.
- **Topological DAG Planner**: Deterministic directed acyclic graph compiler mapping module execution order and dependency chains (matching `factory plan` CLI output).
- **Code Generator Engine**: Compiles project ASTs into clean scaffolding artifacts, `README.md`, `factory-manifest.json`, module directory structures, environment configurations, and exportable packages.
- **Starter Blueprint Catalog**: Built-in templates for LeadOS SaaS, Autonomous Agent Swarm, API Gateway Mesh, Automation Pipelines, and Mobile SDKs.

## Architecture

- **Language & UI**: Kotlin 2.1.0, Jetpack Compose, Material 3
- **State Management**: StateFlow, MVVM Clean Architecture
- **Data Serialization**: `kotlinx.serialization` (JSON & YAML)
- **Local Storage**: Blueprint & Generation history persistence
