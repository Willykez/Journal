package com.forexjournal.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.data.Trade
import com.forexjournal.app.ui.components.TradeDetailDialog
import com.forexjournal.app.ui.components.TradeRow
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TradesScreen(
    trades: List<Trade>,
    onDelete: (Trade, (Boolean, String?) -> Unit) -> Unit,
    onUndoDelete: (Trade, (Boolean, String?) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var sessionFilter by remember { mutableStateOf("ALL") }
    var selectedTrade by remember { mutableStateOf<Trade?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val sessions = remember(trades) { trades.map { it.session }.distinct() }
    val filtered = if (sessionFilter == "ALL") trades else trades.filter { it.session == sessionFilter }
    val reversed = remember(filtered) { filtered.asReversed() }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterButton("ALL", sessionFilter == "ALL") { sessionFilter = "ALL" }
                sessions.forEach { s ->
                    FilterButton(s, sessionFilter == s) { sessionFilter = s }
                }
            }

            Text(
                "${filtered.size} / ${trades.size} trades \u00B7 swipe left to delete",
                fontSize = 11.sp,
                color = AppMuted,
                modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 6.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reversed, key = { it.rowIndex ?: it.hashCode() }) { t ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart) {
                                onDelete(t) { success, error ->
                                    if (success) {
                                        scope.launch {
                                            val result = snackbarHostState.showSnackbar(
                                                message = "Trade deleted",
                                                actionLabel = "Undo",
                                                duration = SnackbarDuration.Short
                                            )
                                            if (result == SnackbarResult.ActionPerformed) {
                                                onUndoDelete(t.copy(rowIndex = null)) { _, _ -> }
                                            }
                                        }
                                    } else {
                                        scope.launch { snackbarHostState.showSnackbar(error ?: "Could not delete trade") }
                                    }
                                }
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismissBox(
                        state = dismissState,
                        enableDismissFromStartToEnd = false,
                        backgroundContent = { DeleteBackground() }
                    ) {
                        TradeRow(trade = t, onClick = { selectedTrade = t })
                    }
                }
                item { androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 70.dp)) }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)
        )
    }

    selectedTrade?.let { t ->
        TradeDetailDialog(trade = t, onDismiss = { selectedTrade = null })
    }
}

@Composable
private fun DeleteBackground() {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(AppRed.copy(alpha = 0.85f))
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = androidx.compose.ui.graphics.Color.White)
    }
}

@Composable
private fun FilterButton(label: String, selected: Boolean, onClick: () -> Unit) {
    if (selected) {
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(containerColor = AppTeal.copy(alpha = 0.2f), contentColor = AppTeal),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) { Text(label, fontSize = 11.sp) }
    } else {
        OutlinedButton(
            onClick = onClick,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 6.dp)
        ) { Text(label, fontSize = 11.sp, color = AppMuted) }
    }
}
