package com.example.engine

import com.example.model.ProjectBlueprint
import com.example.model.ProjectInfo
import com.example.model.ProjectOutputs
import com.example.model.TechStack
import kotlinx.serialization.json.Json

object BlueprintSerializer {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun toJson(blueprint: ProjectBlueprint): String {
        return json.encodeToString(ProjectBlueprint.serializer(), blueprint)
    }

    fun fromJson(jsonStr: String): ProjectBlueprint {
        return json.decodeFromString(ProjectBlueprint.serializer(), jsonStr)
    }

    fun toYaml(blueprint: ProjectBlueprint): String {
        val sb = StringBuilder()
        sb.appendLine("apiVersion: ${blueprint.apiVersion}")
        sb.appendLine()
        sb.appendLine("project:")
        sb.appendLine("  name: ${blueprint.project.name}")
        sb.appendLine("  type: ${blueprint.project.type}")
        sb.appendLine()
        sb.appendLine("stack:")
        sb.appendLine("  frontend: ${blueprint.stack.frontend}")
        sb.appendLine("  backend: ${blueprint.stack.backend}")
        sb.appendLine("  database: ${blueprint.stack.database}")
        blueprint.stack.orm?.let { if (it.isNotBlank()) sb.appendLine("  orm: $it") }
        blueprint.stack.auth?.let { if (it.isNotBlank()) sb.appendLine("  auth: $it") }
        sb.appendLine("  ai: ${blueprint.stack.ai}")
        sb.appendLine("  deploy: ${blueprint.stack.deploy}")
        sb.appendLine()
        sb.appendLine("modules:")
        for (module in blueprint.modules) {
            sb.appendLine("  - $module")
        }
        sb.appendLine()
        sb.appendLine("outputs:")
        sb.appendLine("  github: ${blueprint.outputs.github}")
        sb.appendLine("  notion: ${blueprint.outputs.notion}")
        return sb.toString()
    }

    fun fromYaml(yamlStr: String): ProjectBlueprint {
        // Lightweight line-based YAML parser for declarative blueprints
        var apiVersion = "factory.leados.dev/v1"
        var projectName = "LeadOS"
        var projectType = "saas"
        var frontend = "nextjs"
        var backend = "nextjs"
        var database = "supabase"
        var orm: String? = null
        var auth: String? = null
        var ai = "openrouter"
        var deploy = "vercel"
        val modules = mutableListOf<String>()
        var github = true
        var notion = true

        var currentSection = ""

        val lines = yamlStr.lines()
        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty() || line.startsWith("#")) continue

            if (line.startsWith("apiVersion:")) {
                apiVersion = line.substringAfter("apiVersion:").trim().trim('"', '\'')
                currentSection = ""
                continue
            }

            if (line == "project:") {
                currentSection = "project"
                continue
            }
            if (line == "stack:") {
                currentSection = "stack"
                continue
            }
            if (line == "modules:") {
                currentSection = "modules"
                continue
            }
            if (line == "outputs:") {
                currentSection = "outputs"
                continue
            }

            when (currentSection) {
                "project" -> {
                    if (line.startsWith("name:")) projectName = line.substringAfter("name:").trim().trim('"', '\'')
                    if (line.startsWith("type:")) projectType = line.substringAfter("type:").trim().trim('"', '\'')
                }
                "stack" -> {
                    if (line.startsWith("frontend:")) frontend = line.substringAfter("frontend:").trim().trim('"', '\'')
                    if (line.startsWith("backend:")) backend = line.substringAfter("backend:").trim().trim('"', '\'')
                    if (line.startsWith("database:")) database = line.substringAfter("database:").trim().trim('"', '\'')
                    if (line.startsWith("orm:")) orm = line.substringAfter("orm:").trim().trim('"', '\'')
                    if (line.startsWith("auth:")) auth = line.substringAfter("auth:").trim().trim('"', '\'')
                    if (line.startsWith("ai:")) ai = line.substringAfter("ai:").trim().trim('"', '\'')
                    if (line.startsWith("deploy:")) deploy = line.substringAfter("deploy:").trim().trim('"', '\'')
                }
                "modules" -> {
                    if (line.startsWith("-")) {
                        val mod = line.substringAfter("-").trim().trim('"', '\'')
                        if (mod.isNotEmpty()) modules.add(mod)
                    }
                }
                "outputs" -> {
                    if (line.startsWith("github:")) github = line.substringAfter("github:").trim().toBooleanStrictOrNull() ?: true
                    if (line.startsWith("notion:")) notion = line.substringAfter("notion:").trim().toBooleanStrictOrNull() ?: true
                }
            }
        }

        return ProjectBlueprint(
            apiVersion = apiVersion,
            project = ProjectInfo(name = projectName, type = projectType),
            stack = TechStack(
                frontend = frontend,
                backend = backend,
                database = database,
                orm = orm,
                auth = auth,
                ai = ai,
                deploy = deploy
            ),
            modules = if (modules.isNotEmpty()) modules else listOf("foundation", "sales"),
            outputs = ProjectOutputs(github = github, notion = notion)
        )
    }
}
