package com.forexjournal.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.forexjournal.app.ui.theme.AppPanelElevated
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import kotlin.math.max
import kotlin.math.min

@Composable
fun EquityChart(equity: List<Double>, modifier: Modifier = Modifier) {
    if (equity.isEmpty()) return

    val minE = min(0.0, equity.min())
    val maxE = max(0.0, equity.max())
    val range = (maxE - minE).let { if (it == 0.0) 1.0 else it }

    Canvas(modifier = modifier.fillMaxWidth().height(150.dp)) {
        val w = size.width
        val h = size.height
        val stepX = if (equity.size > 1) w / (equity.size - 1) else 0f

        val points = equity.mapIndexed { i, v ->
            val x = i * stepX
            val y = h - ((v - minE) / range).toFloat() * h
            Offset(x, y)
        }

        val linePath = Path().apply {
            points.forEachIndexed { i, p -> if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y) }
        }
        val fillPath = Path().apply {
            addPath(linePath)
            lineTo(points.last().x, h)
            lineTo(points.first().x, h)
            close()
        }

        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(listOf(AppTeal.copy(alpha = 0.30f), Color.Transparent))
        )
        drawPath(
            path = linePath,
            color = AppTeal,
            style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun DrawdownBars(drawdowns: List<Double>, modifier: Modifier = Modifier) {
    if (drawdowns.isEmpty()) return
    val maxDD = drawdowns.maxOrNull()?.takeIf { it > 0 } ?: 1.0

    Row(
        modifier = modifier.fillMaxWidth().height(36.dp),
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        drawdowns.forEach { d ->
            val fraction = (d / maxDD).toFloat().coerceIn(0.02f, 1f)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .height((36 * fraction).dp)
                    .background(if (d > 0) AppRed.copy(alpha = 0.75f) else AppPanelElevated)
            )
        }
    }
}
