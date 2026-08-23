package com.example.ui

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppTopBar
import com.example.ui.screens.*
import com.example.ui.theme.*

data class NavItem(
    val tab: FactoryTab,
    val label: String,
    val icon: ImageVector
)

val NAV_ITEMS = listOf(
    NavItem(FactoryTab.DASHBOARD, "Dashboard", Icons.Default.Dashboard),
    NavItem(FactoryTab.EDITOR, "Editor", Icons.Default.Edit),
    NavItem(FactoryTab.PLANNER, "Plan DAG", Icons.Default.AccountTree),
    NavItem(FactoryTab.GENERATOR, "Generator", Icons.Default.RocketLaunch),
    NavItem(FactoryTab.TEMPLATES, "Templates", Icons.Default.FolderSpecial)
)

@Composable
fun LeadOSApp(viewModel: MainViewModel = viewModel()) {
    val activeTab by viewModel.activeTab.collectAsState()
    val blueprint by viewModel.currentBlueprint.collectAsState()
    val validationResult by viewModel.validationResult.collectAsState()

    Scaffold(
        containerColor = Slate950,
        topBar = {
            AppTopBar(
                blueprint = blueprint,
                validationResult = validationResult,
                onRunGenerator = {
                    viewModel.runGenerator()
                    viewModel.selectTab(FactoryTab.GENERATOR)
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Slate900,
                tonalElevation = 8.dp
            ) {
                NAV_ITEMS.forEach { item ->
                    val isSelected = activeTab == item.tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectTab(item.tab) },
                        icon = {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (isSelected) CyanBright else Slate400
                            )
                        },
                        label = {
                            Text(
                                text = item.label,
                                color = if (isSelected) CyanBright else Slate400,
                                fontSize = 11.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = CyanBright,
                            selectedTextColor = CyanBright,
                            unselectedIconColor = Slate400,
                            unselectedTextColor = Slate400,
                            indicatorColor = Slate850
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Slate950)
        ) {
            Crossfade(targetState = activeTab, label = "tab_transition") { tab ->
                when (tab) {
                    FactoryTab.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        blueprint = blueprint,
                        validationResult = validationResult,
                        onNavigate = { viewModel.selectTab(it) }
                    )
                    FactoryTab.EDITOR -> EditorScreen(
                        viewModel = viewModel,
                        blueprint = blueprint,
                        validationResult = validationResult
                    )
                    FactoryTab.PLANNER -> PlannerScreen(
                        viewModel = viewModel
                    )
                    FactoryTab.GENERATOR -> GeneratorScreen(
                        viewModel = viewModel,
                        blueprint = blueprint
                    )
                    FactoryTab.TEMPLATES -> TemplatesScreen(
                        viewModel = viewModel,
                        onNavigate = { viewModel.selectTab(it) }
                    )
                }
            }
        }
    }
}
