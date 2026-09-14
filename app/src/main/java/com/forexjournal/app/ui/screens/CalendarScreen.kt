package com.forexjournal.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.data.Trade
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppPanelElevated
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont
import java.util.Calendar

@Composable
fun CalendarScreen(trades: List<Trade>, modifier: Modifier = Modifier) {
    // Group trades by their raw date string (expects "yyyy-MM-dd" from the sheet)
    val dayMap = remember(trades) {
        trades.groupBy { it.date }.mapValues { (_, list) -> list.sumOf { it.pnl } }
    }
    val dayTrades = remember(trades) { trades.groupBy { it.date } }

    var cal by remember {
        mutableStateOf(Calendar.getInstance())
    }
    var selectedDay by remember { mutableStateOf<String?>(null) }

    val year = cal.get(Calendar.YEAR)
    val month = cal.get(Calendar.MONTH) // 0-based
    val monthLabel = remember(year, month) {
        val fmt = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.US)
        fmt.format(cal.time)
    }

    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayCal = Calendar.getInstance().apply { set(year, month, 1) }
    // Convert Sunday=1..Saturday=7 to Monday-first offset (0..6)
    val offset = (firstDayCal.get(Calendar.DAY_OF_WEEK) + 5) % 7

    val maxAbsDay = dayMap.values.maxOfOrNull { kotlin.math.abs(it) }?.takeIf { it > 0 } ?: 1.0

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(monthLabel, fontSize = 16.sp, color = AppText)
            Row {
                TextButton(onClick = {
                    cal = (cal.clone() as Calendar).apply { add(Calendar.MONTH, -1) }
                }) { Text("‹ Prev", color = AppTeal) }
                TextButton(onClick = {
                    cal = (cal.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
                }) { Text("Next ›", color = AppTeal) }
            }
        }

        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            listOf("M", "T", "W", "T", "F", "S", "S").forEach { d ->
                Text(
                    d,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    fontSize = 10.sp,
                    color = AppMuted
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.padding(top = 6.dp)
        ) {
            items(offset) {
                androidx.compose.foundation.layout.Box(modifier = Modifier.aspectRatio(1f).padding(2.dp))
            }
            items(daysInMonth) { i ->
                val day = i + 1
                val key = String.format("%04d-%02d-%02d", year, month + 1, day)
                val pnl = dayMap[key]
                val hasTrades = pnl != null

                val bg = when {
                    pnl == null -> AppPanelElevated
                    pnl > 0 -> AppGreen.copy(alpha = (0.25f + (kotlin.math.abs(pnl) / maxAbsDay).toFloat() * 0.5f).coerceIn(0.25f, 0.85f))
                    pnl < 0 -> AppRed.copy(alpha = (0.25f + (kotlin.math.abs(pnl) / maxAbsDay).toFloat() * 0.5f).coerceIn(0.25f, 0.85f))
                    else -> AppPanelElevated
                }

                Column(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(bg)
                        .then(if (hasTrades) Modifier.clickable { selectedDay = key } else Modifier)
                        .padding(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("$day", fontSize = 10.sp, color = AppMuted)
                    if (hasTrades) {
                        Text(
                            (if (pnl!! >= 0) "+" else "") + pnl.toInt(),
                            fontSize = 9.sp,
                            fontFamily = MonoFont,
                            color = AppText
                        )
                    }
                }
            }
        }
    }

    selectedDay?.let { day ->
        val list = dayTrades[day].orEmpty()
        val net = list.sumOf { it.pnl }
        AlertDialog(
            onDismissRequest = { selectedDay = null },
            containerColor = AppPanel,
            title = { Text(day, color = AppText) },
            text = {
                Column {
                    Text(
                        (if (net >= 0) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(net)) + " · ${list.size} trades",
                        color = if (net >= 0) AppGreen else AppRed,
                        fontFamily = MonoFont,
                        fontSize = 13.sp
                    )
                    Column(modifier = Modifier.padding(top = 10.dp)) {
                        list.forEach { t ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(t.pair, color = AppText, fontSize = 12.sp)
                                Text(
                                    (if (t.pnl >= 0) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(t.pnl)),
                                    color = if (t.pnl >= 0) AppGreen else AppRed,
                                    fontFamily = MonoFont,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedDay = null }) { Text("Close") }
            }
        )
    }
}
