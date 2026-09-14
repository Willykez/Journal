package com.forexjournal.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.forexjournal.app.ui.components.LogTradeDialog
import com.forexjournal.app.ui.screens.CalendarScreen
import com.forexjournal.app.ui.screens.OverviewScreen
import com.forexjournal.app.ui.screens.TradesScreen
import com.forexjournal.app.ui.theme.AppBg
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.ForexTradeAnalystTheme
import com.forexjournal.app.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForexTradeAnalystTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = AppBg) {
                    AppRoot()
                }
            }
        }
    }
}

private enum class Tab(val label: String) { OVERVIEW("Overview"), TRADES("Trades"), CALENDAR("Calendar") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot(viewModel: DashboardViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var currentTab by remember { mutableStateOf(Tab.OVERVIEW) }
    var showSettings by remember { mutableStateOf(false) }
    var showLogTrade by remember { mutableStateOf(false) }

    val pullState = rememberPullToRefreshState()

    Scaffold(
        containerColor = AppBg,
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text("Trade Journal Analyst", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AppText)
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
                                val liveColor = if (uiState.error != null) AppRed else AppGreen
                                if (!uiState.isLoading) {
                                    PulsingDot(color = liveColor)
                                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 5.dp))
                                }
                                val statusText = when {
                                    uiState.isLoading -> "Loading…"
                                    uiState.error != null -> "Sync failed"
                                    else -> "${uiState.trades.size} trades \u00B7 live"
                                }
                                Text(statusText, fontSize = 10.sp, color = if (uiState.error != null) AppRed else AppMuted)
                            }
                        }
                    },
                    actions = {
                        TextButton(onClick = { showSettings = true }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = AppTeal, modifier = Modifier.size(15.dp))
                            Text(" Connect", color = AppTeal, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AppPanel, titleContentColor = AppText)
                )
                // Thin gradient shadow line for depth under the app bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(3.dp)
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.25f), Color.Transparent)
                            )
                        )
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = AppPanel, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = currentTab == Tab.OVERVIEW,
                    onClick = { currentTab = Tab.OVERVIEW },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                    label = { Text(Tab.OVERVIEW.label, fontSize = 10.sp) },
                    colors = navColors()
                )
                NavigationBarItem(
                    selected = currentTab == Tab.TRADES,
                    onClick = { currentTab = Tab.TRADES },
                    icon = { Icon(Icons.Filled.ReceiptLong, contentDescription = null) },
                    label = { Text(Tab.TRADES.label, fontSize = 10.sp) },
                    colors = navColors()
                )
                NavigationBarItem(
                    selected = currentTab == Tab.CALENDAR,
                    onClick = { currentTab = Tab.CALENDAR },
                    icon = { Icon(Icons.Filled.CalendarMonth, contentDescription = null) },
                    label = { Text(Tab.CALENDAR.label, fontSize = 10.sp) },
                    colors = navColors()
                )
            }
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showLogTrade = true },
                containerColor = AppTeal,
                contentColor = AppBg,
                icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                text = { Text("Log Execution", fontWeight = FontWeight.Bold, fontSize = 13.sp) }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            PullToRefreshBox(
                isRefreshing = uiState.isRefreshing,
                onRefresh = { viewModel.loadData(isManualRefresh = true) },
                state = pullState,
                modifier = Modifier.fillMaxSize()
            ) {
                when {
                    uiState.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = AppTeal)
                        }
                    }
                    uiState.error != null && uiState.trades.isEmpty() -> {
                        ErrorState(message = uiState.error!!, onRetry = { viewModel.loadData() })
                    }
                    uiState.metrics == null -> {
                        EmptyState(onLogTrade = { showLogTrade = true })
                    }
                    else -> {
                        androidx.compose.animation.AnimatedContent(
                            targetState = currentTab,
                            transitionSpec = {
                                (androidx.compose.animation.fadeIn(tween(220)))
                                    .togetherWith(androidx.compose.animation.fadeOut(tween(140)))
                            },
                            label = "tabTransition"
                        ) { tab ->
                            when (tab) {
                                Tab.OVERVIEW -> OverviewScreen(metrics = uiState.metrics!!, analysis = uiState.analysis)
                                Tab.TRADES -> TradesScreen(trades = uiState.trades)
                                Tab.CALENDAR -> CalendarScreen(trades = uiState.trades)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showSettings) {
        SettingsDialog(
            currentUrl = uiState.url,
            onDismiss = { showSettings = false },
            onSave = { newUrl ->
                viewModel.setUrl(newUrl)
                showSettings = false
            }
        )
    }

    if (showLogTrade) {
        LogTradeDialog(
            onDismiss = { showLogTrade = false },
            onSubmit = { trade, onResult -> viewModel.submitTrade(trade, onResult) }
        )
    }
}

@Composable
private fun navColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = AppTeal,
    selectedTextColor = AppTeal,
    indicatorColor = AppTeal.copy(alpha = 0.16f),
    unselectedIconColor = AppMuted,
    unselectedTextColor = AppMuted
)

@Composable
private fun PulsingDot(color: Color) {
    val transition = rememberInfiniteTransition(label = "pulse")
    val alpha by transition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(900), repeatMode = RepeatMode.Reverse),
        label = "pulseAlpha"
    )
    Box(
        modifier = Modifier
            .size(7.dp)
            .clip(androidx.compose.foundation.shape.CircleShape)
            .background(color.copy(alpha = alpha))
    )
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text("Could not sync journal", color = AppText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(
            message,
            color = AppMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = AppTeal, contentColor = AppBg)) {
            Text("Retry")
        }
    }
}

@Composable
private fun EmptyState(onLogTrade: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text("No trades logged yet", color = AppText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(
            "Add rows to your Trades tab, or log one right from here.",
            color = AppMuted,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )
        Button(onClick = onLogTrade, colors = ButtonDefaults.buttonColors(containerColor = AppTeal, contentColor = AppBg)) {
            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Text(" Log Execution")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsDialog(currentUrl: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(currentUrl) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(color = AppPanel, shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Apps Script Web App URL", color = AppText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it },
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = AppText),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppTeal,
                        unfocusedBorderColor = AppMuted.copy(alpha = 0.4f)
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = AppMuted) }
                    Button(
                        onClick = { onSave(text.trim()) },
                        colors = ButtonDefaults.buttonColors(containerColor = AppTeal, contentColor = AppBg),
                        modifier = Modifier.padding(start = 8.dp)
                    ) { Text("Save & Sync") }
                }
            }
        }
    }
}
