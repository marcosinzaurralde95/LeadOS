package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ArtifactType
import com.example.model.GeneratedArtifact
import com.example.model.ProjectBlueprint
import com.example.ui.MainViewModel
import com.example.ui.components.SyntaxHighlightedCodeView
import com.example.ui.theme.*

@Composable
fun GeneratorScreen(
    viewModel: MainViewModel,
    blueprint: ProjectBlueprint
) {
    val genState by viewModel.generationState.collectAsState()
    val selectedArtifact by viewModel.selectedArtifact.collectAsState()
    val clipboardManager = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
    ) {
        // Generator Action Card
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
                        Column {
                            Text(
                                text = "Code Generation Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Target: .factory-output/ (${blueprint.project.name})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Slate400
                            )
                        }

                        Button(
                            onClick = { viewModel.runGenerator() },
                            enabled = !genState.isRunning,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanBright,
                                contentColor = Slate950
                            )
                        ) {
                            if (genState.isRunning) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Slate950,
                                    strokeWidth = 2.dp
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Building...")
                            } else {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Generate Code", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    if (genState.isRunning) {
                        LinearProgressIndicator(
                            progress = { genState.progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = CyanBright,
                            trackColor = Slate800
                        )
                        Text(
                            text = genState.currentStep,
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanBright
                        )
                    }

                    if (genState.error != null) {
                        Text(
                            text = genState.error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RoseError
                        )
                    }
                }
            }
        }

        // Terminal Logs Card
        if (genState.logs.isNotEmpty()) {
            item {
                Surface(
                    color = CodeBackground,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(Icons.Default.Terminal, contentDescription = null, tint = CyanBright, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Factory Compiler Logs",
                                style = MaterialTheme.typography.labelMedium,
                                color = Slate400,
                                fontSize = 11.sp
                            )
                        }

                        Divider(color = Slate800)

                        genState.logs.forEach { log ->
                            Text(
                                text = "> $log",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (log.startsWith("✔")) EmeraldLight else if (log.startsWith("⚡")) CyanBright else Slate200,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Artifacts Selector Chips
        if (genState.artifacts.isNotEmpty()) {
            item {
                Text(
                    text = "Generated Project Artifacts (${genState.artifacts.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            item {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(genState.artifacts) { artifact ->
                        val isSelected = selectedArtifact?.path == artifact.path
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.selectArtifact(artifact) },
                            label = {
                                Text(
                                    text = artifact.filename,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (artifact.type) {
                                        ArtifactType.MARKDOWN -> Icons.Default.Description
                                        ArtifactType.JSON -> Icons.Default.DataObject
                                        ArtifactType.YAML -> Icons.Default.Code
                                        ArtifactType.SOURCE -> Icons.Default.Javascript
                                        ArtifactType.CONFIG -> Icons.Default.Settings
                                    },
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = CyanBright,
                                selectedLabelColor = Slate950,
                                selectedLeadingIconColor = Slate950,
                                containerColor = Slate900,
                                labelColor = Slate200,
                                iconColor = Slate400
                            )
                        )
                    }
                }
            }

            // Selected Artifact Preview
            selectedArtifact?.let { artifact ->
                item {
                    Surface(
                        color = Slate900,
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = artifact.path,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "${artifact.type} • ${artifact.content.lines().size} lines",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Slate400,
                                        fontSize = 11.sp
                                    )
                                }

                                IconButton(
                                    onClick = { clipboardManager.setText(AnnotatedString(artifact.content)) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanBright, modifier = Modifier.size(16.dp))
                                }
                            }

                            SyntaxHighlightedCodeView(
                                code = artifact.content,
                                language = when (artifact.type) {
                                    ArtifactType.JSON -> "json"
                                    ArtifactType.YAML -> "yaml"
                                    ArtifactType.MARKDOWN -> "markdown"
                                    ArtifactType.SOURCE -> "typescript"
                                    ArtifactType.CONFIG -> "yaml"
                                },
                                isEditable = false
                            )
                        }
                    }
                }
            }
        }
    }
}
