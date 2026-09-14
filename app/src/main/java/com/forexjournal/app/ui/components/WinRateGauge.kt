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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.ui.theme.AppAmber
import com.forexjournal.app.ui.theme.AppBorder
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont

/**
 * @param diameter lets this be reused both large (detail sheet, 148dp) and
 * small (home screen hero row, ~72dp) without duplicating the drawing logic.
 * @param showLabel hides the "WIN RATE" caption in compact contexts where
 * space is tight and the surrounding UI already labels it.
 */
@Composable
fun WinRateGauge(
    winRate: Int,
    modifier: Modifier = Modifier,
    diameter: Dp = 148.dp,
    showLabel: Boolean = true
) {
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

    val strokeWidth = (diameter.value * 0.088f).dp
    val valueFontSize = (diameter.value * 0.20f).sp
    val labelFontSize = (diameter.value * 0.068f).sp

    Box(modifier = modifier.size(diameter), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(diameter)
                .background(
                    Brush.radialGradient(
                        colors = listOf(baseColor.copy(alpha = 0.18f), Color.Transparent),
                        radius = diameter.value * 0.68f
                    )
                )
        )

        Canvas(modifier = Modifier.size(diameter)) {
            val strokePx = strokeWidth.toPx()
            val inset = strokePx / 2

            drawArc(
                color = AppBorder,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(size.width - strokePx, size.height - strokePx),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

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
                size = Size(size.width - strokePx, size.height - strokePx),
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$winRate%", fontSize = valueFontSize, fontWeight = FontWeight.Bold, fontFamily = MonoFont, color = AppText)
            if (showLabel) {
                Text("WIN RATE", fontSize = labelFontSize, color = AppMuted, letterSpacing = 1.2.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
