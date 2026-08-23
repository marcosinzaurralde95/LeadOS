package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ProjectBlueprint
import com.example.model.ValidationResult
import com.example.ui.FactoryTab
import com.example.ui.MainViewModel
import com.example.ui.components.DependencyGraphView
import com.example.ui.theme.*

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    blueprint: ProjectBlueprint,
    validationResult: ValidationResult,
    onNavigate: (FactoryTab) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp)
    ) {
        // Hero Card
        item {
            Surface(
                color = Slate900,
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "PROJECT FACTORY",
                                style = MaterialTheme.typography.labelMedium,
                                color = CyanBright,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = blueprint.project.name,
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.horizontalGradient(
                                        listOf(DeepCyan, DeepViolet)
                                    )
                                )
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = blueprint.project.type.uppercase(),
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Text(
                        text = "Declarative software blueprint compiled for AI-assisted engineering.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Slate400
                    )

                    Divider(color = Slate800, modifier = Modifier.padding(vertical = 4.dp))

                    // Quick Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onNavigate(FactoryTab.EDITOR) },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Edit Spec", fontSize = 13.sp)
                        }

                        Button(
                            onClick = { onNavigate(FactoryTab.PLANNER) },
                            colors = ButtonDefaults.buttonColors(containerColor = Slate800),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.AccountTree, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Plan DAG", fontSize = 13.sp)
                        }

                        Button(
                            onClick = {
                                viewModel.runGenerator()
                                onNavigate(FactoryTab.GENERATOR)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBright, contentColor = Color.Black),
                            modifier = Modifier.weight(1.2f)
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Generate", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Metrics Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Modules",
                    value = "${blueprint.modules.size}",
                    subtitle = "Deterministic Pipeline",
                    icon = Icons.Default.Layers,
                    accentColor = CyanBright,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Outputs",
                    value = "${if (blueprint.outputs.github) "GH" else ""}${if (blueprint.outputs.github && blueprint.outputs.notion) " + " else ""}${if (blueprint.outputs.notion) "Notion" else ""}".ifEmpty { "None" },
                    subtitle = "Automated Sync",
                    icon = Icons.Default.CloudSync,
                    accentColor = VioletBright,
                    modifier = Modifier.weight(1f)
                )

                MetricCard(
                    title = "Schema",
                    value = if (validationResult.isValid) "Pass" else "Fail",
                    subtitle = "v1 Specification",
                    icon = if (validationResult.isValid) Icons.Default.CheckCircle else Icons.Default.Error,
                    accentColor = if (validationResult.isValid) EmeraldLight else RoseError,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Dependency DAG Preview
        item {
            DependencyGraphView(
                projectNode = viewModel.astNode.value,
                executionPlan = viewModel.executionPlan.value
            )
        }

        // Tech Stack Architecture
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
                            text = "Target Tech Stack",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Configured in YAML",
                            style = MaterialTheme.typography.labelMedium,
                            color = Slate400
                        )
                    }

                    val stackEntries = blueprint.stack.toMap().entries.toList()
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        stackEntries.chunked(2).forEach { rowItems ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                rowItems.forEach { (key, value) ->
                                    Surface(
                                        color = Slate850,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = key,
                                                style = MaterialTheme.typography.labelMedium,
                                                color = Slate400
                                            )
                                            Text(
                                                text = value,
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = CyanBright
                                            )
                                        }
                                    }
                                }
                                if (rowItems.size == 1) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Slate900,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = accentColor,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Slate400,
                fontSize = 11.sp
            )
        }
    }
}
