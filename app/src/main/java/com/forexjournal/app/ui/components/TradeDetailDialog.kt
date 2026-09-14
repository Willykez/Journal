package com.forexjournal.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.data.Trade
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont

@Composable
fun TradeDetailDialog(trade: Trade, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppPanel,
        title = { Text("${trade.pair} — ${trade.date}", color = AppText) },
        text = {
            Column {
                DetailRow("Direction", trade.direction)
                DetailRow("Entry / Exit", "${trade.entry ?: "—"} / ${trade.exit ?: "—"}")
                DetailRow("Size", trade.size?.toString() ?: "—")
                DetailRow("Stop Loss", trade.stopLoss?.toString() ?: "not set")
                DetailRow("Take Profit", trade.takeProfit?.toString() ?: "not set")
                DetailRow(
                    "Net P&L",
                    (if (trade.pnl >= 0) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(trade.pnl)),
                    valueColor = if (trade.pnl >= 0) AppGreen else AppRed
                )
                DetailRow("Session", trade.session)
                if (trade.notes.isNotBlank()) {
                    Text("Notes", fontSize = 11.sp, color = AppMuted, modifier = Modifier.padding(top = 8.dp))
                    Text(trade.notes, fontSize = 13.sp, color = AppText, modifier = Modifier.padding(top = 2.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Dismiss") }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, valueColor: androidx.compose.ui.graphics.Color = AppText) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 12.sp, color = AppMuted)
        Text(value, fontSize = 12.sp, fontFamily = MonoFont, fontWeight = FontWeight.Medium, color = valueColor)
    }
}
