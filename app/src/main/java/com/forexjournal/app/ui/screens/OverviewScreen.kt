package com.forexjournal.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import com.forexjournal.app.ui.components.AiBriefingCard
import com.forexjournal.app.ui.components.DrawdownBars
import com.forexjournal.app.ui.components.EquityChart
import com.forexjournal.app.ui.components.KpiCard
import com.forexjournal.app.ui.components.WinRateGauge
import com.forexjournal.app.ui.theme.AppAmber
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont
import kotlin.math.roundToInt

private val cardElevation = 3.dp

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = AppTeal, modifier = Modifier.size(13.dp))
        Spacer(modifier = Modifier.padding(start = 6.dp))
        Text(title, fontSize = 11.sp, color = AppMuted, letterSpacing = 0.6.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun OverviewScreen(metrics: Metrics, analysis: Analysis?, modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppPanel),
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WinRateGauge(winRate = metrics.winRate)
                    Spacer(modifier = Modifier.height(12.dp))
                    val streakColor = if (metrics.streakIsWin) AppGreen else AppRed
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(streakColor.copy(alpha = 0.12f))
                            .padding(horizontal = 14.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (metrics.streakIsWin) "\uD83D\uDD25" else "\u2744\uFE0F", fontSize = 13.sp)
                        Spacer(modifier = Modifier.padding(start = 6.dp))
                        Text(
                            "${metrics.streakCount} ${if (metrics.streakIsWin) "win" else "loss"} streak",
                            fontFamily = MonoFont,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = streakColor
                        )
                    }
                }
            }
        }

        item { AiBriefingCard(analysis = analysis) }

        item {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.height(250.dp),
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
                        accentColor = AppTeal
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

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppPanel),
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SectionHeader(Icons.Filled.ShowChart, "EQUITY CURVE")
                    EquityChart(equity = metrics.equity, modifier = Modifier.padding(top = 12.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    SectionHeader(Icons.Filled.Speed, "UNDERWATER DRAWDOWN")
                    Spacer(modifier = Modifier.height(8.dp))
                    DrawdownBars(drawdowns = metrics.drawdowns)
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppPanel),
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SectionHeader(Icons.Filled.CandlestickChart, "PAIR ATTRIBUTION")
                    Spacer(modifier = Modifier.height(12.dp))
                    val maxAbs = metrics.pairStats.maxOfOrNull { kotlin.math.abs(it.pnl) }?.takeIf { it > 0 } ?: 1.0
                    metrics.pairStats.take(6).forEachIndexed { idx, p ->
                        Column(modifier = Modifier.padding(bottom = if (idx == metrics.pairStats.take(6).lastIndex) 0.dp else 12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${p.pair}", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = AppText)
                                Text(
                                    (if (p.pnl >= 0) "+$" else "-$") + String.format("%.0f", kotlin.math.abs(p.pnl)) + "  ${p.winRate}% WR",
                                    fontFamily = MonoFont,
                                    fontSize = 11.sp,
                                    color = if (p.pnl >= 0) AppGreen else AppRed
                                )
                            }
                            val fraction = (kotlin.math.abs(p.pnl) / maxAbs).toFloat().coerceIn(0.03f, 1f)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .padding(top = 6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(AppMuted.copy(alpha = 0.12f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(fraction)
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(if (p.pnl >= 0) AppGreen else AppRed)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = AppPanel),
                elevation = CardDefaults.cardElevation(defaultElevation = cardElevation),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    SectionHeader(Icons.Filled.Schedule, "SESSION PERFORMANCE")
                    Spacer(modifier = Modifier.height(10.dp))
                    metrics.sessionStats.forEachIndexed { i, s ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(s.session, fontSize = 12.sp, color = AppText, fontWeight = FontWeight.Medium)
                                if (i == 0) {
                                    Spacer(modifier = Modifier.padding(start = 6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AppGreen.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("PRIME", fontSize = 8.sp, color = AppGreen, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
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

        item { Spacer(modifier = Modifier.height(80.dp)) } // room for the FAB
    }
}
