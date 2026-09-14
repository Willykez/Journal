package com.forexjournal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun TradeRow(trade: Trade, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val pnlColor = if (trade.isWin) AppGreen else AppRed

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AppPanel)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (trade.isLong) "▲" else "▼",
                    color = if (trade.isLong) AppGreen else AppRed,
                    fontSize = 11.sp
                )
                Text(
                    "  ${trade.pair}",
                    fontWeight = FontWeight.SemiBold,
                    color = AppText,
                    fontSize = 13.sp
                )
            }
            Text(
                "${trade.date} · ${trade.session}",
                fontSize = 11.sp,
                color = AppMuted,
                modifier = Modifier.padding(top = 2.dp)
            )
            if (trade.notes.isNotBlank()) {
                Text(
                    trade.notes,
                    fontSize = 11.sp,
                    color = AppMuted,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        Text(
            (if (trade.pnl >= 0) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(trade.pnl)),
            fontWeight = FontWeight.Bold,
            fontFamily = MonoFont,
            fontSize = 13.sp,
            color = pnlColor
        )
    }
}
