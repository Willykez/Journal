package com.forexjournal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.data.Analysis
import com.forexjournal.app.data.Metrics
import com.forexjournal.app.ui.theme.AppAmber
import com.forexjournal.app.ui.theme.AppBg
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont
import kotlin.math.roundToInt

@Composable
private fun SheetTitle(text: String) {
    Text(text, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppText, modifier = Modifier.padding(bottom = 14.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceDetailSheet(metrics: Metrics, sheetState: SheetState, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = AppPanel) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp).padding(bottom = 28.dp)) {
            SheetTitle("Performance Detail")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                WinRateGauge(winRate = metrics.winRate, diameter = 130.dp)
            }
            Spacer(modifier = Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(260.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    KpiCard(
                        "Net P&L",
                        (if (metrics.totalPnL >= 0) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(metrics.totalPnL)),
                        icon = Icons.Filled.AccountBalance,
                        accentColor = if (metrics.totalPnL >= 0) AppGreen else AppRed,
                        valueColor = if (metrics.totalPnL >= 0) AppGreen else AppRed
                    )
                }
                item {
                    KpiCard(
                        "Profit Factor",
                        if (metrics.profitFactor.isInfinite()) "\u221E" else String.format("%.2f", metrics.profitFactor),
                        icon = Icons.Filled.ShowChart,
                        accentColor = AppTeal,
                        sub = "Gross win \u00F7 gross loss"
                    )
                }
                item {
                    KpiCard(
                        "Expectancy / Trade",
                        (if (metrics.expectancy >= 0) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(metrics.expectancy)),
                        icon = if (metrics.expectancy >= 0) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                        accentColor = if (metrics.expectancy >= 0) AppGreen else AppRed,
                        valueColor = if (metrics.expectancy >= 0) AppGreen else AppRed
                    )
                }
                item {
                    KpiCard(
                        "Kelly Criterion",
                        "${(metrics.kelly * 100).roundToInt()}%",
                        icon = Icons.Filled.Percent,
                        accentColor = AppAmber,
                        sub = "Suggested sizing"
                    )
                }
                item {
                    KpiCard(
                        "Max Drawdown",
                        "-$" + String.format("%.2f", metrics.maxDrawdown),
                        icon = Icons.Filled.Speed,
                        accentColor = AppRed,
                        valueColor = AppRed
                    )
                }
                item {
                    KpiCard(
                        "Discipline",
                        "${metrics.slCompliancePct}%",
                        icon = Icons.Filled.Shield,
                        accentColor = if (metrics.slCompliancePct >= 70) AppGreen else AppAmber,
                        sub = "have a stop loss set",
                        valueColor = if (metrics.slCompliancePct >= 70) AppGreen else AppAmber
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiBriefingSheet(analysis: Analysis?, sheetState: SheetState, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = AppPanel) {
        Column(modifier = Modifier.padding(horizontal = 4.dp).padding(bottom = 28.dp)) {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) { SheetTitle("AI Analyst Briefing") }
            AiBriefingCard(analysis = analysis, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquityDetailSheet(metrics: Metrics, sheetState: SheetState, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = AppPanel) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
            SheetTitle("Equity Curve & Drawdown")
            EquityChart(equity = metrics.equity)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("UNDERWATER DRAWDOWN", fontSize = 10.sp, color = AppMuted, letterSpacing = 0.5.sp)
                Text("MAX -$" + String.format("%.0f", metrics.maxDrawdown), fontSize = 10.sp, color = AppRed, fontFamily = MonoFont)
            }
            Spacer(modifier = Modifier.height(8.dp))
            DrawdownBars(drawdowns = metrics.drawdowns)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BreakdownSheet(metrics: Metrics, sheetState: SheetState, onDismiss: () -> Unit) {
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = AppPanel) {
        Column(modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 28.dp)) {
            SheetTitle("Pair & Session Breakdown")

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 10.dp)) {
                androidx.compose.material3.Icon(Icons.Filled.CandlestickChart, contentDescription = null, tint = AppTeal, modifier = Modifier.padding(end = 6.dp))
                Text("PAIR ATTRIBUTION", fontSize = 11.sp, color = AppMuted, letterSpacing = 0.5.sp, fontWeight = FontWeight.SemiBold)
            }
            val maxAbs = metrics.pairStats.maxOfOrNull { kotlin.math.abs(it.pnl) }?.takeIf { it > 0 } ?: 1.0
            metrics.pairStats.forEach { p ->
                Column(modifier = Modifier.padding(bottom = 12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(p.pair, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = AppText)
                        Text(
                            (if (p.pnl >= 0) "+$" else "-$") + String.format("%.0f", kotlin.math.abs(p.pnl)) + "  ${p.winRate}% WR \u00B7 ${p.count}T",
                            fontFamily = MonoFont,
                            fontSize = 11.sp,
                            color = if (p.pnl >= 0) AppGreen else AppRed
                        )
                    }
                    val fraction = (kotlin.math.abs(p.pnl) / maxAbs).toFloat().coerceIn(0.03f, 1f)
                    Box(
                        modifier = Modifier.fillMaxWidth().height(6.dp).padding(top = 6.dp)
                            .clip(RoundedCornerShape(3.dp)).background(AppMuted.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth(fraction).height(6.dp)
                                .clip(RoundedCornerShape(3.dp)).background(if (p.pnl >= 0) AppGreen else AppRed)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 10.dp, top = 8.dp)) {
                androidx.compose.material3.Icon(Icons.Filled.Schedule, contentDescription = null, tint = AppTeal, modifier = Modifier.padding(end = 6.dp))
                Text("SESSION PERFORMANCE", fontSize = 11.sp, color = AppMuted, letterSpacing = 0.5.sp, fontWeight = FontWeight.SemiBold)
            }
            metrics.sessionStats.forEachIndexed { i, s ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(s.session + if (i == 0) "  \u00B7 PRIME" else "", fontSize = 12.sp, color = AppText)
                    Text(
                        (if (s.pnl >= 0) "+$" else "-$") + String.format("%.0f", kotlin.math.abs(s.pnl)) + "  (${s.winRate}% \u00B7 ${s.count}T)",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = if (s.pnl >= 0) AppGreen else AppRed
                    )
                }
            }
        }
    }
}
