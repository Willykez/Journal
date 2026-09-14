package com.forexjournal.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.data.Analysis
import com.forexjournal.app.data.Metrics
import com.forexjournal.app.ui.components.AiBriefingSheet
import com.forexjournal.app.ui.components.BreakdownSheet
import com.forexjournal.app.ui.components.EquityDetailSheet
import com.forexjournal.app.ui.components.PerformanceDetailSheet
import com.forexjournal.app.ui.components.WinRateGauge
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont

private enum class Sheet { NONE, PERFORMANCE, AI, EQUITY, BREAKDOWN }

/**
 * Deliberately NOT a long stack of full-detail cards. This is a glance
 * screen — a handful of compact rows you can scan in one look, each one
 * tappable to open a focused bottom sheet with the full detail. That
 * glance-then-drill-down pattern is what makes this read as a native app
 * screen rather than a scrolled web page.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OverviewScreen(metrics: Metrics, analysis: Analysis?, modifier: Modifier = Modifier) {
    var activeSheet by remember { mutableStateOf(Sheet.NONE) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ---- Hero row: gauge + net P&L + streak, tap for full performance detail ----
        item {
            GlanceRow(onClick = { activeSheet = Sheet.PERFORMANCE }) {
                WinRateGauge(winRate = metrics.winRate, diameter = 68.dp, showLabel = false)
                Spacer(modifier = Modifier.padding(start = 14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("NET P&L", fontSize = 9.sp, color = AppMuted, letterSpacing = 0.5.sp)
                    Text(
                        (if (metrics.totalPnL >= 0) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(metrics.totalPnL)),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = MonoFont,
                        color = if (metrics.totalPnL >= 0) AppGreen else AppRed
                    )
                    Row(modifier = Modifier.padding(top = 4.dp)) {
                        val streakColor = if (metrics.streakIsWin) AppGreen else AppRed
                        Text(
                            "${if (metrics.streakIsWin) "\uD83D\uDD25" else "\u2744\uFE0F"} ${metrics.streakCount} ${if (metrics.streakIsWin) "win" else "loss"} streak",
                            fontSize = 11.sp,
                            fontFamily = MonoFont,
                            color = streakColor
                        )
                    }
                }
                ChevronHint()
            }
        }

        // ---- AI teaser: one line + tap for full briefing ----
        item {
            GlanceRow(onClick = { activeSheet = Sheet.AI }) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AppTeal, modifier = Modifier.padding(end = 12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI ANALYST BRIEFING", fontSize = 9.sp, color = AppTeal, letterSpacing = 0.6.sp, fontWeight = FontWeight.Bold)
                    Text(
                        analysis?.summary ?: "Run analysis from the sheet menu to see insights here.",
                        fontSize = 12.sp,
                        color = AppText,
                        maxLines = 2,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
                ChevronHint()
            }
        }

        // ---- Equity mini sparkline: tap for full curve + drawdown ----
        item {
            GlanceRow(onClick = { activeSheet = Sheet.EQUITY }) {
                Icon(Icons.Filled.ShowChart, contentDescription = null, tint = AppTeal, modifier = Modifier.padding(end = 12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("EQUITY CURVE", fontSize = 9.sp, color = AppMuted, letterSpacing = 0.5.sp)
                    MiniSparkline(equity = metrics.equity, modifier = Modifier.padding(top = 6.dp).height(28.dp).fillMaxWidth())
                }
                ChevronHint()
            }
        }

        // ---- Breakdown: pair + session performance ----
        item {
            GlanceRow(onClick = { activeSheet = Sheet.BREAKDOWN }) {
                Icon(Icons.Filled.BarChart, contentDescription = null, tint = AppTeal, modifier = Modifier.padding(end = 12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("PAIR & SESSION BREAKDOWN", fontSize = 9.sp, color = AppMuted, letterSpacing = 0.5.sp)
                    val best = metrics.pairStats.firstOrNull()
                    Text(
                        if (best != null) "Best: ${best.pair} \u00B7 ${best.winRate}% WR" else "No data yet",
                        fontSize = 12.sp,
                        color = AppText,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
                ChevronHint()
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) } // room for the FAB
    }

    when (activeSheet) {
        Sheet.PERFORMANCE -> PerformanceDetailSheet(metrics, sheetState) { activeSheet = Sheet.NONE }
        Sheet.AI -> AiBriefingSheet(analysis, sheetState) { activeSheet = Sheet.NONE }
        Sheet.EQUITY -> EquityDetailSheet(metrics, sheetState) { activeSheet = Sheet.NONE }
        Sheet.BREAKDOWN -> BreakdownSheet(metrics, sheetState) { activeSheet = Sheet.NONE }
        Sheet.NONE -> {}
    }
}

@Composable
private fun GlanceRow(onClick: () -> Unit, content: @Composable RowScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = AppPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            content = content
        )
    }
}

@Composable
private fun ChevronHint() {
    Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = AppMuted)
}

@Composable
private fun MiniSparkline(equity: List<Double>, modifier: Modifier = Modifier) {
    if (equity.isEmpty()) return
    val minE = kotlin.math.min(0.0, equity.min())
    val maxE = kotlin.math.max(0.0, equity.max())
    val range = (maxE - minE).let { if (it == 0.0) 1.0 else it }
    val color = if (equity.last() >= 0) AppGreen else AppRed

    androidx.compose.foundation.Canvas(modifier = modifier) {
        val stepX = if (equity.size > 1) size.width / (equity.size - 1) else 0f
        val path = androidx.compose.ui.graphics.Path().apply {
            equity.forEachIndexed { i, v ->
                val x = i * stepX
                val y = size.height - ((v - minE) / range).toFloat() * size.height
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = color,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
    }
}
