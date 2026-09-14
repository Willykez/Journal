package com.forexjournal.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.data.Trade
import com.forexjournal.app.ui.components.TradeDetailDialog
import com.forexjournal.app.ui.components.TradeRow
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppTeal

@Composable
fun TradesScreen(trades: List<Trade>, modifier: Modifier = Modifier) {
    var sessionFilter by remember { mutableStateOf("ALL") }
    var selectedTrade by remember { mutableStateOf<Trade?>(null) }

    val sessions = remember(trades) { trades.map { it.session }.distinct() }
    val filtered = if (sessionFilter == "ALL") trades else trades.filter { it.session == sessionFilter }
    val reversed = remember(filtered) { filtered.asReversed() }

    Column(modifier = modifier.fillMaxSize()) {
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
            "${filtered.size} / ${trades.size} trades",
            fontSize = 11.sp,
            color = AppMuted,
            modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 6.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reversed.size) { i ->
                val t = reversed[i]
                TradeRow(trade = t, onClick = { selectedTrade = t })
            }
            item { androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(bottom = 70.dp)) }
        }
    }

    selectedTrade?.let { t ->
        TradeDetailDialog(trade = t, onDismiss = { selectedTrade = null })
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
