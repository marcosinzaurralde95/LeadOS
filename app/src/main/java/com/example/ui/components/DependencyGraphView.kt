package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExecutionPlan
import com.example.model.ProjectNode
import com.example.ui.theme.*

@Composable
fun DependencyGraphView(
    projectNode: ProjectNode?,
    executionPlan: ExecutionPlan?,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Slate900,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Slate800),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = "DAG Graph",
                        tint = CyanBright,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Module Execution Pipeline (DAG)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = "${projectNode?.modules?.size ?: 0} Stages",
                    style = MaterialTheme.typography.labelMedium,
                    color = Slate400
                )
            }

            if (projectNode == null || projectNode.modules.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No module execution graph available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Slate400
                    )
                }
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                ) {
                    itemsIndexed(projectNode.modules) { index, module ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            ModuleNodeCard(
                                stageNumber = index + 1,
                                moduleId = module.id,
                                dependsOn = module.dependsOn,
                                isFirst = index == 0,
                                isLast = index == projectNode.modules.size - 1
                            )

                            if (index < projectNode.modules.size - 1) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                        contentDescription = "Depends on",
                                        tint = CyanBright.copy(alpha = 0.8f),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = "deps",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = Slate600,
                                        fontSize = 9.sp
                                    )
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
private fun ModuleNodeCard(
    stageNumber: Int,
    moduleId: String,
    dependsOn: List<String>,
    isFirst: Boolean,
    isLast: Boolean
) {
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isFirst) CyanBright else if (isLast) ElectricViolet else Slate700
        ),
        modifier = Modifier.width(160.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(if (isFirst) CyanBright else Slate700),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$stageNumber",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isFirst) Slate950 else Color.White,
                        fontSize = 11.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Slate900)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "TASK",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricViolet,
                        fontSize = 9.sp
                    )
                }
            }

            Text(
                text = moduleId,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )

            if (dependsOn.isNotEmpty()) {
                Text(
                    text = "depends: ${dependsOn.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Slate400,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            } else {
                Text(
                    text = "root entrypoint",
                    style = MaterialTheme.typography.bodyMedium,
                    color = EmeraldLight,
                    fontSize = 10.sp
                )
            }
        }
    }
}
