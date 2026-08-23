package com.example.engine

import com.example.model.ProjectBlueprint
import com.example.model.ValidationResult

object BlueprintValidator {

    private val PROJECT_NAME_REGEX = Regex("^[A-Za-z0-9_-]+$")
    private val VALID_TYPES = setOf("saas", "api", "library", "agent", "automation")
    private const val EXPECTED_API_VERSION = "factory.leados.dev/v1"

    fun validate(blueprint: ProjectBlueprint): ValidationResult {
        val errors = mutableListOf<String>()
        val warnings = mutableListOf<String>()

        // 1. Validate apiVersion
        if (blueprint.apiVersion != EXPECTED_API_VERSION) {
            errors.add("Invalid apiVersion '${blueprint.apiVersion}'. Expected '$EXPECTED_API_VERSION'")
        }

        // 2. Validate project
        if (blueprint.project.name.isBlank()) {
            errors.add("Project name cannot be empty")
        } else if (!PROJECT_NAME_REGEX.matches(blueprint.project.name)) {
            errors.add("Project name '${blueprint.project.name}' is invalid. Must match pattern ^[A-Za-z0-9_-]+$")
        }

        if (!VALID_TYPES.contains(blueprint.project.type.lowercase())) {
            errors.add("Invalid project type '${blueprint.project.type}'. Must be one of: ${VALID_TYPES.joinToString(", ")}")
        }

        // 3. Validate stack
        val stack = blueprint.stack
        if (stack.frontend.isBlank()) errors.add("Stack 'frontend' is required and cannot be blank")
        if (stack.backend.isBlank()) errors.add("Stack 'backend' is required and cannot be blank")
        if (stack.database.isBlank()) errors.add("Stack 'database' is required and cannot be blank")
        if (stack.ai.isBlank()) errors.add("Stack 'ai' is required and cannot be blank")
        if (stack.deploy.isBlank()) errors.add("Stack 'deploy' is required and cannot be blank")

        // 4. Validate modules
        if (blueprint.modules.isEmpty()) {
            errors.add("Blueprint must define at least 1 module")
        } else {
            val seenModules = mutableSetOf<String>()
            blueprint.modules.forEachIndexed { index, module ->
                if (module.isBlank()) {
                    errors.add("Module at index $index is empty")
                } else if (!PROJECT_NAME_REGEX.matches(module)) {
                    errors.add("Module '$module' has invalid characters. Use alphanumeric, dash, or underscore")
                } else if (seenModules.contains(module)) {
                    errors.add("Duplicate module '$module' found. Modules must be unique")
                } else {
                    seenModules.add(module)
                }
            }
        }

        if (blueprint.modules.size > 10) {
            warnings.add("More than 10 modules detected. Pipeline compilation may take additional time.")
        }

        return ValidationResult(
            isValid = errors.isEmpty(),
            errors = errors,
            warnings = warnings
        )
    }
}
