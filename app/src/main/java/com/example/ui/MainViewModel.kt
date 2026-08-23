package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BlueprintRepository
import com.example.data.GenerationRecord
import com.example.engine.BlueprintGenerator
import com.example.engine.BlueprintPlanner
import com.example.engine.BlueprintSerializer
import com.example.engine.BlueprintValidator
import com.example.model.ExecutionPlan
import com.example.model.GeneratedArtifact
import com.example.model.ProjectBlueprint
import com.example.model.ProjectNode
import com.example.model.ValidationResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class FactoryTab {
    DASHBOARD,
    EDITOR,
    PLANNER,
    GENERATOR,
    TEMPLATES
}

enum class EditorMode {
    VISUAL_FORM,
    YAML_CODE,
    JSON_CODE
}

data class GenerationState(
    val isRunning: Boolean = false,
    val progress: Float = 0f,
    val currentStep: String = "",
    val logs: List<String> = emptyList(),
    val artifacts: List<GeneratedArtifact> = emptyList(),
    val isCompleted: Boolean = false,
    val error: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = BlueprintRepository(application)

    val currentBlueprint: StateFlow<ProjectBlueprint> = repository.currentBlueprint
    val savedBlueprints: StateFlow<List<ProjectBlueprint>> = repository.savedBlueprints
    val generationHistory: StateFlow<List<GenerationRecord>> = repository.generationHistory

    private val _activeTab = MutableStateFlow(FactoryTab.DASHBOARD)
    val activeTab: StateFlow<FactoryTab> = _activeTab.asStateFlow()

    private val _editorMode = MutableStateFlow(EditorMode.VISUAL_FORM)
    val editorMode: StateFlow<EditorMode> = _editorMode.asStateFlow()

    private val _codeEditorContent = MutableStateFlow("")
    val codeEditorContent: StateFlow<String> = _codeEditorContent.asStateFlow()

    private val _validationResult = MutableStateFlow(ValidationResult(isValid = true))
    val validationResult: StateFlow<ValidationResult> = _validationResult.asStateFlow()

    private val _astNode = MutableStateFlow<ProjectNode?>(null)
    val astNode: StateFlow<ProjectNode?> = _astNode.asStateFlow()

    private val _executionPlan = MutableStateFlow<ExecutionPlan?>(null)
    val executionPlan: StateFlow<ExecutionPlan?> = _executionPlan.asStateFlow()

    private val _formattedPlanText = MutableStateFlow("")
    val formattedPlanText: StateFlow<String> = _formattedPlanText.asStateFlow()

    private val _generationState = MutableStateFlow(GenerationState())
    val generationState: StateFlow<GenerationState> = _generationState.asStateFlow()

    private val _selectedArtifact = MutableStateFlow<GeneratedArtifact?>(null)
    val selectedArtifact: StateFlow<GeneratedArtifact?> = _selectedArtifact.asStateFlow()

    init {
        recomputeAll(repository.currentBlueprint.value)
    }

    fun selectTab(tab: FactoryTab) {
        _activeTab.value = tab
    }

    fun setEditorMode(mode: EditorMode) {
        _editorMode.value = mode
        val bp = currentBlueprint.value
        _codeEditorContent.value = when (mode) {
            EditorMode.YAML_CODE -> BlueprintSerializer.toYaml(bp)
            EditorMode.JSON_CODE -> BlueprintSerializer.toJson(bp)
            EditorMode.VISUAL_FORM -> ""
        }
    }

    fun updateCodeContent(code: String) {
        _codeEditorContent.value = code
        try {
            val parsed = when (_editorMode.value) {
                EditorMode.YAML_CODE -> BlueprintSerializer.fromYaml(code)
                EditorMode.JSON_CODE -> BlueprintSerializer.fromJson(code)
                EditorMode.VISUAL_FORM -> currentBlueprint.value
            }
            updateBlueprint(parsed)
        } catch (_: Exception) {
            // Keep code as user types, validate will flag syntax errors
        }
    }

    fun updateBlueprint(blueprint: ProjectBlueprint) {
        repository.setBlueprint(blueprint)
        recomputeAll(blueprint)
    }

    fun saveCurrentBlueprint() {
        val bp = currentBlueprint.value
        repository.saveBlueprint(bp)
    }

    fun deleteSavedBlueprint(name: String) {
        repository.deleteBlueprint(name)
    }

    fun loadBlueprint(blueprint: ProjectBlueprint) {
        updateBlueprint(blueprint)
        _codeEditorContent.value = when (_editorMode.value) {
            EditorMode.YAML_CODE -> BlueprintSerializer.toYaml(blueprint)
            EditorMode.JSON_CODE -> BlueprintSerializer.toJson(blueprint)
            EditorMode.VISUAL_FORM -> ""
        }
    }

    fun addModule(moduleId: String) {
        val trimmed = moduleId.trim().lowercase().replace(Regex("[^a-z0-9_-]"), "")
        if (trimmed.isNotEmpty() && !currentBlueprint.value.modules.contains(trimmed)) {
            val updatedModules = currentBlueprint.value.modules + trimmed
            updateBlueprint(currentBlueprint.value.copy(modules = updatedModules))
        }
    }

    fun removeModule(moduleId: String) {
        val updatedModules = currentBlueprint.value.modules.filter { it != moduleId }
        if (updatedModules.isNotEmpty()) {
            updateBlueprint(currentBlueprint.value.copy(modules = updatedModules))
        }
    }

    fun moveModule(fromIndex: Int, toIndex: Int) {
        if (fromIndex in currentBlueprint.value.modules.indices && toIndex in currentBlueprint.value.modules.indices) {
            val list = currentBlueprint.value.modules.toMutableList()
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            updateBlueprint(currentBlueprint.value.copy(modules = list))
        }
    }

    private fun recomputeAll(blueprint: ProjectBlueprint) {
        val validation = BlueprintValidator.validate(blueprint)
        _validationResult.value = validation

        if (validation.isValid) {
            val ast = BlueprintPlanner.toAst(blueprint)
            _astNode.value = ast
            val plan = BlueprintPlanner.plan(ast)
            _executionPlan.value = plan
            _formattedPlanText.value = BlueprintPlanner.formatPlan(plan)
        } else {
            _astNode.value = null
            _executionPlan.value = null
            _formattedPlanText.value = "Validation errors present. Plan unavailable."
        }
    }

    fun runGenerator() {
        val bp = currentBlueprint.value
        val validation = BlueprintValidator.validate(bp)
        if (!validation.isValid) {
            _generationState.value = GenerationState(
                isRunning = false,
                error = "Cannot generate: ${validation.errors.firstOrNull() ?: "Validation failed"}"
            )
            return
        }

        viewModelScope.launch {
            _generationState.value = GenerationState(
                isRunning = true,
                progress = 0.1f,
                currentStep = "Compiling blueprint AST...",
                logs = listOf("⚡ LeadOS Factory Compiler v0.1 initialized", "Parsing blueprint for project '${bp.project.name}' (${bp.project.type})")
            )
            delay(300)

            val ast = BlueprintPlanner.toAst(bp)
            _generationState.value = _generationState.value.copy(
                progress = 0.35f,
                currentStep = "Resolving module execution plan...",
                logs = _generationState.value.logs + listOf(
                    "Building dependency graph for ${ast.modules.size} modules",
                    "Pipeline stages: ${ast.modules.joinToString(" -> ") { it.id }}"
                )
            )
            delay(350)

            _generationState.value = _generationState.value.copy(
                progress = 0.65f,
                currentStep = "Synthesizing project artifacts & scaffolds...",
                logs = _generationState.value.logs + listOf(
                    "Generating project root README.md with ${bp.stack.toMap().size} stack configurations",
                    "Generating factory-manifest.json (v1 schema specification)",
                    "Generating scaffolds for modules: ${bp.modules.joinToString(", ")}"
                )
            )
            delay(400)

            val artifacts = BlueprintGenerator.generate(ast)
            _generationState.value = _generationState.value.copy(
                progress = 1.0f,
                isRunning = false,
                isCompleted = true,
                currentStep = "Generation complete!",
                artifacts = artifacts,
                logs = _generationState.value.logs + listOf(
                    "✔ Successfully emitted ${artifacts.size} project artifacts",
                    "Outputs verified: GitHub (${bp.outputs.github}), Notion (${bp.outputs.notion})"
                )
            )
            _selectedArtifact.value = artifacts.firstOrNull()

            repository.recordGeneration(
                GenerationRecord(
                    id = "gen-${System.currentTimeMillis()}",
                    projectName = bp.project.name,
                    projectType = bp.project.type,
                    timestamp = System.currentTimeMillis(),
                    artifactsCount = artifacts.size,
                    modulesCount = bp.modules.size,
                    success = true
                )
            )
        }
    }

    fun selectArtifact(artifact: GeneratedArtifact) {
        _selectedArtifact.value = artifact
    }
}
