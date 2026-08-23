package com.example.model

import kotlinx.serialization.Serializable

@Serializable
enum class ProjectType(val displayName: String, val description: String) {
    SAAS("SaaS", "Fullstack Software as a Service application"),
    API("API Service", "High-performance backend API & microservices"),
    LIBRARY("Library / SDK", "Reusable code library, package, or mobile module"),
    AGENT("AI Agent", "Autonomous LLM reasoning agent & workflow orchestrator"),
    AUTOMATION("Automation", "Event-driven background pipeline & worker automation");

    val schemaValue: String
        get() = name.lowercase()

    companion object {
        fun fromSchema(value: String): ProjectType {
            return entries.find { it.schemaValue.equals(value, ignoreCase = true) } ?: SAAS
        }
    }
}

@Serializable
data class ProjectInfo(
    val name: String,
    val type: String
)

@Serializable
data class TechStack(
    val frontend: String = "nextjs",
    val backend: String = "nextjs",
    val database: String = "supabase",
    val orm: String? = "drizzle",
    val auth: String? = "supabase",
    val ai: String = "openrouter",
    val deploy: String = "vercel"
) {
    fun toMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        map["frontend"] = frontend
        map["backend"] = backend
        map["database"] = database
        orm?.let { if (it.isNotBlank()) map["orm"] = it }
        auth?.let { if (it.isNotBlank()) map["auth"] = it }
        map["ai"] = ai
        map["deploy"] = deploy
        return map
    }
}

@Serializable
data class ProjectOutputs(
    val github: Boolean = true,
    val notion: Boolean = true
)

@Serializable
data class ProjectBlueprint(
    val apiVersion: String = "factory.leados.dev/v1",
    val project: ProjectInfo,
    val stack: TechStack,
    val modules: List<String>,
    val outputs: ProjectOutputs = ProjectOutputs()
)

@Serializable
data class ModuleNode(
    val kind: String = "Module",
    val id: String,
    val dependsOn: List<String> = emptyList()
)

@Serializable
data class ProjectNode(
    val kind: String = "Project",
    val name: String,
    val type: String,
    val stack: TechStack,
    val modules: List<ModuleNode>,
    val outputs: ProjectOutputs
)

@Serializable
data class ExecutionTask(
    val id: String,
    val kind: String = "module",
    val module: String,
    val dependsOn: List<String> = emptyList()
)

@Serializable
data class ExecutionPlan(
    val project: String,
    val tasks: List<ExecutionTask>
)

enum class ArtifactType {
    MARKDOWN,
    JSON,
    YAML,
    SOURCE,
    CONFIG
}

data class GeneratedArtifact(
    val path: String,
    val filename: String,
    val content: String,
    val type: ArtifactType,
    val module: String? = null
)

data class ValidationResult(
    val isValid: Boolean,
    val errors: List<String> = emptyList(),
    val warnings: List<String> = emptyList()
)
