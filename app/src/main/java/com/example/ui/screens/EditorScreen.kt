package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProjectBlueprint
import com.example.model.ProjectInfo
import com.example.model.ProjectOutputs
import com.example.model.ProjectType
import com.example.model.TechStack
import com.example.model.ValidationResult
import com.example.ui.EditorMode
import com.example.ui.MainViewModel
import com.example.ui.components.SyntaxHighlightedCodeView
import com.example.ui.theme.*

@Composable
fun EditorScreen(
    viewModel: MainViewModel,
    blueprint: ProjectBlueprint,
    validationResult: ValidationResult
) {
    val editorMode by viewModel.editorMode.collectAsState()
    val codeContent by viewModel.codeEditorContent.collectAsState()

    var newModuleText by remember { mutableStateOf("") }
    var showSavedSnackbar by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Mode Selector Tab Bar
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            SegmentedButton(
                selected = editorMode == EditorMode.VISUAL_FORM,
                onClick = { viewModel.setEditorMode(EditorMode.VISUAL_FORM) },
                shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = CyanBright,
                    activeContentColor = Slate950,
                    inactiveContainerColor = Slate900,
                    inactiveContentColor = Slate400
                )
            ) {
                Text("Visual Builder", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            SegmentedButton(
                selected = editorMode == EditorMode.YAML_CODE,
                onClick = { viewModel.setEditorMode(EditorMode.YAML_CODE) },
                shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = CyanBright,
                    activeContentColor = Slate950,
                    inactiveContainerColor = Slate900,
                    inactiveContentColor = Slate400
                )
            ) {
                Text("YAML", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }

            SegmentedButton(
                selected = editorMode == EditorMode.JSON_CODE,
                onClick = { viewModel.setEditorMode(EditorMode.JSON_CODE) },
                shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor = CyanBright,
                    activeContentColor = Slate950,
                    inactiveContainerColor = Slate900,
                    inactiveContentColor = Slate400
                )
            ) {
                Text("JSON", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        // Validation Status Banner
        if (!validationResult.isValid) {
            Surface(
                color = RoseError.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, RoseError.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = RoseError, modifier = Modifier.size(16.dp))
                        Text("Schema Validation Errors", style = MaterialTheme.typography.labelLarge, color = RoseError)
                    }
                    validationResult.errors.forEach { err ->
                        Text("• $err", style = MaterialTheme.typography.bodyMedium, color = Slate200, fontSize = 12.sp)
                    }
                }
            }
        }

        // Main Editor Area
        when (editorMode) {
            EditorMode.VISUAL_FORM -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    // Project Info Section
                    item {
                        Surface(
                            color = Slate900,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Project Configuration",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                OutlinedTextField(
                                    value = blueprint.project.name,
                                    onValueChange = { newName ->
                                        viewModel.updateBlueprint(
                                            blueprint.copy(
                                                project = blueprint.project.copy(name = newName)
                                            )
                                        )
                                    },
                                    label = { Text("Project Name (^[A-Za-z0-9_-]+$)") },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = CyanBright,
                                        unfocusedBorderColor = Slate700,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Text(
                                    text = "Project Type",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Slate400
                                )

                                LazyRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    items(ProjectType.entries) { pType ->
                                        val isSelected = blueprint.project.type.equals(pType.schemaValue, ignoreCase = true)
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = {
                                                viewModel.updateBlueprint(
                                                    blueprint.copy(
                                                        project = blueprint.project.copy(type = pType.schemaValue)
                                                    )
                                                )
                                            },
                                            label = { Text(pType.displayName, fontSize = 12.sp) },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = ElectricViolet,
                                                selectedLabelColor = Color.White,
                                                containerColor = Slate850,
                                                labelColor = Slate400
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Tech Stack Configuration
                    item {
                        Surface(
                            color = Slate900,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Tech Stack Declarations",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                val stack = blueprint.stack

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = stack.frontend,
                                        onValueChange = { viewModel.updateBlueprint(blueprint.copy(stack = stack.copy(frontend = it))) },
                                        label = { Text("Frontend") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanBright, unfocusedBorderColor = Slate700)
                                    )
                                    OutlinedTextField(
                                        value = stack.backend,
                                        onValueChange = { viewModel.updateBlueprint(blueprint.copy(stack = stack.copy(backend = it))) },
                                        label = { Text("Backend") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanBright, unfocusedBorderColor = Slate700)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = stack.database,
                                        onValueChange = { viewModel.updateBlueprint(blueprint.copy(stack = stack.copy(database = it))) },
                                        label = { Text("Database") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanBright, unfocusedBorderColor = Slate700)
                                    )
                                    OutlinedTextField(
                                        value = stack.orm ?: "",
                                        onValueChange = { viewModel.updateBlueprint(blueprint.copy(stack = stack.copy(orm = it.ifBlank { null }))) },
                                        label = { Text("ORM (Optional)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanBright, unfocusedBorderColor = Slate700)
                                    )
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = stack.auth ?: "",
                                        onValueChange = { viewModel.updateBlueprint(blueprint.copy(stack = stack.copy(auth = it.ifBlank { null }))) },
                                        label = { Text("Auth (Optional)") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanBright, unfocusedBorderColor = Slate700)
                                    )
                                    OutlinedTextField(
                                        value = stack.ai,
                                        onValueChange = { viewModel.updateBlueprint(blueprint.copy(stack = stack.copy(ai = it))) },
                                        label = { Text("AI Provider") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanBright, unfocusedBorderColor = Slate700)
                                    )
                                }

                                OutlinedTextField(
                                    value = stack.deploy,
                                    onValueChange = { viewModel.updateBlueprint(blueprint.copy(stack = stack.copy(deploy = it))) },
                                    label = { Text("Deploy Target") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = CyanBright, unfocusedBorderColor = Slate700)
                                )
                            }
                        }
                    }

                    // Modules Pipeline List
                    item {
                        Surface(
                            color = Slate900,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Modules Pipeline (${blueprint.modules.size})",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Sequential Chain",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = CyanBright
                                    )
                                }

                                // Module chips
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    blueprint.modules.forEachIndexed { index, mod ->
                                        Surface(
                                            color = Slate850,
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(
                                                        text = "#${index + 1}",
                                                        style = MaterialTheme.typography.labelMedium,
                                                        color = Slate600
                                                    )
                                                    Text(
                                                        text = mod,
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = Color.White
                                                    )
                                                    if (index > 0) {
                                                        Text(
                                                            text = "<- ${blueprint.modules[index - 1]}",
                                                            style = MaterialTheme.typography.labelMedium,
                                                            color = Slate400,
                                                            fontSize = 11.sp
                                                        )
                                                    }
                                                }

                                                IconButton(
                                                    onClick = { viewModel.removeModule(mod) },
                                                    modifier = Modifier.size(24.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Remove module",
                                                        tint = RoseError,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                // Add Module Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = newModuleText,
                                        onValueChange = { newModuleText = it },
                                        placeholder = { Text("Add new module id...") },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = CyanBright,
                                            unfocusedBorderColor = Slate700
                                        )
                                    )

                                    FilledIconButton(
                                        onClick = {
                                            if (newModuleText.isNotBlank()) {
                                                viewModel.addModule(newModuleText)
                                                newModuleText = ""
                                            }
                                        },
                                        colors = IconButtonDefaults.filledIconButtonColors(
                                            containerColor = CyanBright,
                                            contentColor = Slate950
                                        )
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                }
                            }
                        }
                    }

                    // Outputs Config
                    item {
                        Surface(
                            color = Slate900,
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Target Sync Outputs",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("GitHub Repository Export", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                                        Text("Sync generated code and workflow manifests", style = MaterialTheme.typography.bodyMedium, color = Slate400)
                                    }
                                    Switch(
                                        checked = blueprint.outputs.github,
                                        onCheckedChange = {
                                            viewModel.updateBlueprint(blueprint.copy(outputs = blueprint.outputs.copy(github = it)))
                                        }
                                    )
                                }

                                Divider(color = Slate800)

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Notion Architecture Sync", style = MaterialTheme.typography.bodyLarge, color = Color.White)
                                        Text("Export blueprint specs to Notion workspace", style = MaterialTheme.typography.bodyMedium, color = Slate400)
                                    }
                                    Switch(
                                        checked = blueprint.outputs.notion,
                                        onCheckedChange = {
                                            viewModel.updateBlueprint(blueprint.copy(outputs = blueprint.outputs.copy(notion = it)))
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Save Button
                    item {
                        Button(
                            onClick = {
                                viewModel.saveCurrentBlueprint()
                                showSavedSnackbar = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanBright,
                                contentColor = Slate950
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Save Blueprint to Storage", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            EditorMode.YAML_CODE, EditorMode.JSON_CODE -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SyntaxHighlightedCodeView(
                        code = codeContent,
                        language = if (editorMode == EditorMode.YAML_CODE) "yaml" else "json",
                        isEditable = true,
                        onCodeChange = { newCode ->
                            viewModel.updateCodeContent(newCode)
                        },
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.setEditorMode(editorMode)
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Reformat")
                        }

                        Button(
                            onClick = {
                                viewModel.saveCurrentBlueprint()
                                showSavedSnackbar = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = Slate950),
                            modifier = Modifier.weight(1.5f)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(Modifier.width(6.dp))
                            Text("Save Spec", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
