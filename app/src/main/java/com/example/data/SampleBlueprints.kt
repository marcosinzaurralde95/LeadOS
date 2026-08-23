package com.example.data

import com.example.model.ProjectBlueprint
import com.example.model.ProjectInfo
import com.example.model.ProjectOutputs
import com.example.model.TechStack

object SampleBlueprints {

    val LEAD_OS_DEFAULT = ProjectBlueprint(
        apiVersion = "factory.leados.dev/v1",
        project = ProjectInfo(name = "LeadOS", type = "saas"),
        stack = TechStack(
            frontend = "nextjs",
            backend = "nextjs",
            database = "supabase",
            orm = "drizzle",
            auth = "supabase",
            ai = "openrouter",
            deploy = "vercel"
        ),
        modules = listOf("foundation", "sales"),
        outputs = ProjectOutputs(github = true, notion = true)
    )

    val AGENT_SWARM = ProjectBlueprint(
        apiVersion = "factory.leados.dev/v1",
        project = ProjectInfo(name = "AgentSwarm", type = "agent"),
        stack = TechStack(
            frontend = "react",
            backend = "fastapi",
            database = "pgvector",
            orm = "sqlalchemy",
            auth = "clerk",
            ai = "openrouter",
            deploy = "railway"
        ),
        modules = listOf("agent-core", "vector-memory", "tool-executor", "web-crawler", "feedback-loop"),
        outputs = ProjectOutputs(github = true, notion = true)
    )

    val API_GATEWAY = ProjectBlueprint(
        apiVersion = "factory.leados.dev/v1",
        project = ProjectInfo(name = "NexusGateway", type = "api"),
        stack = TechStack(
            frontend = "dashboard-vite",
            backend = "go-fiber",
            database = "postgres",
            orm = "gorm",
            auth = "auth0",
            ai = "gemini",
            deploy = "cloud-run"
        ),
        modules = listOf("auth-proxy", "rate-limiter", "cache-redis", "routing-mesh", "audit-logger"),
        outputs = ProjectOutputs(github = true, notion = false)
    )

    val AUTOMATION_PIPELINE = ProjectBlueprint(
        apiVersion = "factory.leados.dev/v1",
        project = ProjectInfo(name = "DataPulse", type = "automation"),
        stack = TechStack(
            frontend = "sveltekit",
            backend = "python-temporal",
            database = "clickhouse",
            orm = "prisma",
            auth = "firebase",
            ai = "claude-haiku",
            deploy = "aws-ecs"
        ),
        modules = listOf("event-ingestion", "transformation", "enrichment", "webhook-dispatcher"),
        outputs = ProjectOutputs(github = true, notion = true)
    )

    val MOBILE_SDK = ProjectBlueprint(
        apiVersion = "factory.leados.dev/v1",
        project = ProjectInfo(name = "LeadCoreSDK", type = "library"),
        stack = TechStack(
            frontend = "compose-multiplatform",
            backend = "kotlin-ktor",
            database = "sqlite-room",
            orm = "sqldelight",
            auth = "jwt-keys",
            ai = "on-device-llm",
            deploy = "maven-central"
        ),
        modules = listOf("core-networking", "offline-storage", "sync-engine", "analytics-telemetry"),
        outputs = ProjectOutputs(github = true, notion = false)
    )

    val ALL_TEMPLATES = listOf(
        LEAD_OS_DEFAULT,
        AGENT_SWARM,
        API_GATEWAY,
        AUTOMATION_PIPELINE,
        MOBILE_SDK
    )
}
