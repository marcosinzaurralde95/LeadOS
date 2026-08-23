package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.engine.BlueprintSerializer
import com.example.model.ProjectBlueprint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GenerationRecord(
    val id: String,
    val projectName: String,
    val projectType: String,
    val timestamp: Long,
    val artifactsCount: Int,
    val modulesCount: Int,
    val success: Boolean
)

class BlueprintRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("leados_factory_prefs", Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }

    private val _currentBlueprint = MutableStateFlow(SampleBlueprints.LEAD_OS_DEFAULT)
    val currentBlueprint: StateFlow<ProjectBlueprint> = _currentBlueprint.asStateFlow()

    private val _savedBlueprints = MutableStateFlow<List<ProjectBlueprint>>(emptyList())
    val savedBlueprints: StateFlow<List<ProjectBlueprint>> = _savedBlueprints.asStateFlow()

    private val _generationHistory = MutableStateFlow<List<GenerationRecord>>(emptyList())
    val generationHistory: StateFlow<List<GenerationRecord>> = _generationHistory.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val currentJson = prefs.getString("current_blueprint", null)
        if (currentJson != null) {
            try {
                _currentBlueprint.value = BlueprintSerializer.fromJson(currentJson)
            } catch (_: Exception) {
                _currentBlueprint.value = SampleBlueprints.LEAD_OS_DEFAULT
            }
        } else {
            _currentBlueprint.value = SampleBlueprints.LEAD_OS_DEFAULT
        }

        val savedListJson = prefs.getString("saved_blueprints", null)
        if (savedListJson != null) {
            try {
                val list = json.decodeFromString<List<ProjectBlueprint>>(savedListJson)
                _savedBlueprints.value = list
            } catch (_: Exception) {
                _savedBlueprints.value = SampleBlueprints.ALL_TEMPLATES
            }
        } else {
            _savedBlueprints.value = SampleBlueprints.ALL_TEMPLATES
        }

        val historyJson = prefs.getString("generation_history", null)
        if (historyJson != null) {
            try {
                val list = json.decodeFromString<List<GenerationRecord>>(historyJson)
                _generationHistory.value = list
            } catch (_: Exception) {
                _generationHistory.value = emptyList()
            }
        }
    }

    fun setBlueprint(blueprint: ProjectBlueprint) {
        _currentBlueprint.value = blueprint
        prefs.edit().putString("current_blueprint", BlueprintSerializer.toJson(blueprint)).apply()
    }

    fun saveBlueprint(blueprint: ProjectBlueprint) {
        val currentList = _savedBlueprints.value.toMutableList()
        val index = currentList.indexOfFirst { it.project.name == blueprint.project.name }
        if (index >= 0) {
            currentList[index] = blueprint
        } else {
            currentList.add(0, blueprint)
        }
        _savedBlueprints.value = currentList
        prefs.edit().putString("saved_blueprints", json.encodeToString(currentList)).apply()
        setBlueprint(blueprint)
    }

    fun deleteBlueprint(name: String) {
        val currentList = _savedBlueprints.value.toMutableList()
        currentList.removeAll { it.project.name == name }
        _savedBlueprints.value = currentList
        prefs.edit().putString("saved_blueprints", json.encodeToString(currentList)).apply()
    }

    fun recordGeneration(record: GenerationRecord) {
        val history = _generationHistory.value.toMutableList()
        history.add(0, record)
        if (history.size > 20) history.removeLast()
        _generationHistory.value = history
        prefs.edit().putString("generation_history", json.encodeToString(history)).apply()
    }
}
