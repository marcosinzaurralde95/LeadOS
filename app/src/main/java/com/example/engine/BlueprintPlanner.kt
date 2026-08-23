package com.example.engine

import com.example.model.ExecutionPlan
import com.example.model.ExecutionTask
import com.example.model.ModuleNode
import com.example.model.ProjectBlueprint
import com.example.model.ProjectNode

object BlueprintPlanner {

    /**
     * Converts a ProjectBlueprint into its AST representation with deterministic module dependency graph.
     */
    fun toAst(blueprint: ProjectBlueprint): ProjectNode {
        val modules = blueprint.modules.mapIndexed { index, id ->
            ModuleNode(
                kind = "Module",
                id = id,
                dependsOn = if (index == 0) emptyList() else listOf(blueprint.modules[index - 1])
            )
        }

        return ProjectNode(
            kind = "Project",
            name = blueprint.project.name,
            type = blueprint.project.type,
            stack = blueprint.stack,
            modules = modules,
            outputs = blueprint.outputs
        )
    }

    /**
     * Generates a deterministic DAG ExecutionPlan for the project.
     */
    fun plan(project: ProjectNode): ExecutionPlan {
        val tasks = project.modules.map { module ->
            ExecutionTask(
                id = "module:${module.id}",
                kind = "module",
                module = module.id,
                dependsOn = module.dependsOn.map { "module:$it" }
            )
        }

        return ExecutionPlan(
            project = project.name,
            tasks = tasks
        )
    }

    /**
     * Formats the execution plan in the exact CLI format.
     */
    fun formatPlan(planResult: ExecutionPlan): String {
        val lines = mutableListOf("Project: ${planResult.project}", "Execution plan:")
        for (task in planResult.tasks) {
            val deps = if (task.dependsOn.isNotEmpty()) " <- ${task.dependsOn.joinToString(", ")}" else ""
            lines.add("- ${task.id}$deps")
        }
        return lines.joinToString("\n")
    }
}
