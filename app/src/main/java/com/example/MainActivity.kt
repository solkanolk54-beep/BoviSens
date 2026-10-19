package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Cow
import com.example.ui.screens.AddCowDialog
import com.example.ui.screens.AlertsScreen
import com.example.ui.screens.CowDetailDialog
import com.example.ui.screens.HerdDashboardScreen
import com.example.ui.screens.SettingsDialog
import com.example.ui.theme.AlertCriticalRed
import com.example.ui.theme.AlertLamenessOrange
import com.example.ui.theme.BoviGreenPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BoviTab
import com.example.ui.viewmodel.HerdViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HerdViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BoviSensMainApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BoviSensMainApp(viewModel: HerdViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showAddCowDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    // Back button handling: return to dashboard if on sub-tab
    if (uiState.activeTab != BoviTab.DASHBOARD) {
        BackHandler {
            viewModel.selectTab(BoviTab.DASHBOARD)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            BoviBottomNavigation(
                currentTab = uiState.activeTab,
                alertsCount = uiState.alertCows.size,
                onSelectTab = { tab ->
                    if (tab == BoviTab.SETTINGS) {
                        showSettingsDialog = true
                    } else {
                        viewModel.selectTab(tab)
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (uiState.activeTab) {
                BoviTab.DASHBOARD, BoviTab.HERD_LIST -> {
                    HerdDashboardScreen(
                        cows = uiState.filteredCows,
                        summary = uiState.summary,
                        selectedFilter = uiState.selectedFilter,
                        searchQuery = uiState.searchQuery,
                        statusMessage = uiState.statusMessage,
                        isRefreshing = uiState.isRefreshing,
                        onFilterChange = { viewModel.setFilter(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onSelectCow = { viewModel.selectCow(it) },
                        onRefresh = { viewModel.fetchHerdData() },
                        onAddCowClick = { showAddCowDialog = true },
                        onOpenSettings = { showSettingsDialog = true },
                        onOpenAlertsTab = { viewModel.selectTab(BoviTab.URGENT_ALERTS) },
                        onDismissMessage = { viewModel.dismissMessage() }
                    )
                }

                BoviTab.URGENT_ALERTS -> {
                    AlertsScreen(
                        alertCows = uiState.alertCows,
                        onSelectCow = { viewModel.selectCow(it) },
                        onResolveAlert = { tag, note -> viewModel.resolveAlert(tag, note) }
                    )
                }

                BoviTab.SETTINGS -> {
                    // Falls back to dashboard with settings dialog
                    viewModel.selectTab(BoviTab.DASHBOARD)
                    showSettingsDialog = true
                }
            }
        }
    }

    // Detail dialog for selected cow
    uiState.selectedCow?.let { cow ->
        CowDetailDialog(
            cow = cow,
            onDismiss = { viewModel.selectCow(null) },
            onResolveAlert = { tag, note -> viewModel.resolveAlert(tag, note) },
            onDeleteCow = { tag -> viewModel.deleteCow(tag) }
        )
    }

    // Add cow dialog
    if (showAddCowDialog) {
        AddCowDialog(
            onDismiss = { showAddCowDialog = false },
            onAddCow = { tag, name, breed, barn, milk, health, notes ->
                viewModel.addNewCow(tag, name, breed, barn, milk, health, notes)
            }
        )
    }

    // Settings dialog
    if (showSettingsDialog) {
        SettingsDialog(
            currentUrl = uiState.apiBaseUrl,
            onDismiss = { showSettingsDialog = false },
            onSaveUrl = { newUrl -> viewModel.setApiBaseUrl(newUrl) },
            onSimulateTelemetry = {
                viewModel.simulateTelemetryStream()
                viewModel.dismissMessage()
            }
        )
    }
}

@Composable
fun BoviBottomNavigation(
    currentTab: BoviTab,
    alertsCount: Int,
    onSelectTab: (BoviTab) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("bovi_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = currentTab == BoviTab.DASHBOARD,
            onClick = { onSelectTab(BoviTab.DASHBOARD) },
            icon = {
                Icon(
                    imageVector = if (currentTab == BoviTab.DASHBOARD) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                    contentDescription = "لوحة المتابعة"
                )
            },
            label = { Text("متابعة القطيع") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BoviGreenPrimary,
                indicatorColor = BoviGreenPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_tab_dashboard")
        )

        NavigationBarItem(
            selected = currentTab == BoviTab.URGENT_ALERTS,
            onClick = { onSelectTab(BoviTab.URGENT_ALERTS) },
            icon = {
                BadgedBox(
                    badge = {
                        if (alertsCount > 0) {
                            Badge(
                                containerColor = AlertLamenessOrange,
                                contentColor = MaterialTheme.colorScheme.surface
                            ) {
                                Text("$alertsCount")
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentTab == BoviTab.URGENT_ALERTS) Icons.Default.NotificationsActive else Icons.Outlined.Notifications,
                        contentDescription = "التنبيهات العاجلة"
                    )
                }
            },
            label = { Text("تنبيهات عاجلة (24/24)") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = AlertLamenessOrange,
                indicatorColor = AlertLamenessOrange.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_tab_alerts")
        )

        NavigationBarItem(
            selected = false,
            onClick = { onSelectTab(BoviTab.SETTINGS) },
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "الإعدادات والمستشعرات"
                )
            },
            label = { Text("المستشعرات") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = BoviGreenPrimary,
                indicatorColor = BoviGreenPrimary.copy(alpha = 0.15f)
            ),
            modifier = Modifier.testTag("nav_tab_settings")
        )
    }
}
