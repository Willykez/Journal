package com.forexjournal.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.ui.theme.AppAmber
import com.forexjournal.app.ui.theme.AppBorder
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont

@Composable
fun WinRateGauge(winRate: Int, modifier: Modifier = Modifier) {
    val baseColor = when {
        winRate >= 60 -> AppGreen
        winRate >= 45 -> AppAmber
        else -> AppRed
    }
    val animatedSweep by animateFloatAsState(
        targetValue = winRate.coerceIn(0, 100) * 3.6f,
        animationSpec = tween(durationMillis = 800),
        label = "gaugeSweep"
    )

    Box(modifier = modifier.size(148.dp), contentAlignment = Alignment.Center) {
        // Soft ambient glow behind the ring, tinted to the current state color
        Box(
            modifier = Modifier
                .size(148.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(baseColor.copy(alpha = 0.18f), Color.Transparent),
                        radius = 100f
                    )
                )
        )

        Canvas(modifier = Modifier.size(148.dp)) {
            val strokeWidth = 13.dp.toPx()
            val inset = strokeWidth / 2

            // Track
            drawArc(
                color = AppBorder,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Progress arc with a subtle gradient sweep for depth
            drawArc(
                brush = Brush.sweepGradient(
                    0f to baseColor.copy(alpha = 0.55f),
                    1f to baseColor,
                    center = Offset(size.width / 2, size.height / 2)
                ),
                startAngle = -90f,
                sweepAngle = animatedSweep,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - strokeWidth, size.height - strokeWidth),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$winRate%", fontSize = 30.sp, fontWeight = FontWeight.Bold, fontFamily = MonoFont, color = AppText)
            Text("WIN RATE", fontSize = 10.sp, color = AppMuted, letterSpacing = 1.5.sp, fontWeight = FontWeight.Medium)
        }
    }
}
