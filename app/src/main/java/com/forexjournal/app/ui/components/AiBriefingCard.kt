package com.forexjournal.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.data.Analysis
import com.forexjournal.app.ui.theme.AppAmber
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppPanelElevated
import com.forexjournal.app.ui.theme.AppTeal
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont

@Composable
fun AiBriefingCard(analysis: Analysis?, modifier: Modifier = Modifier) {
    val a = analysis ?: Analysis(summary = "No analysis yet — run \"Run analysis now\" from the sheet menu.")

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            width = 1.dp,
            brush = Brush.horizontalGradient(listOf(AppTeal.copy(alpha = 0.55f), AppTeal.copy(alpha = 0.05f)))
        )
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = AppTeal, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.padding(start = 6.dp))
                Text(
                    "AI ANALYST BRIEFING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTeal,
                    letterSpacing = 1.2.sp
                )
            }
            Text(
                a.summary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                fontFamily = MonoFont,
                color = AppText,
                modifier = Modifier.padding(top = 12.dp, bottom = 14.dp)
            )

            a.patterns.forEach { p ->
                Row(modifier = Modifier.padding(bottom = 6.dp)) {
                    Icon(
                        Icons.Filled.ChevronRight,
                        contentDescription = null,
                        tint = AppMuted,
                        modifier = Modifier.size(14.dp).padding(top = 1.dp)
                    )
                    Spacer(modifier = Modifier.padding(start = 2.dp))
                    Text(p, fontSize = 12.sp, color = AppMuted, lineHeight = 17.sp)
                }
            }

            if (a.riskFlags.isNotEmpty()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppAmber.copy(alpha = 0.12f))
                        .padding(12.dp)
                ) {
                    Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = AppAmber, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.padding(start = 6.dp))
                    Text(a.riskFlags.first(), fontSize = 12.sp, color = AppAmber, lineHeight = 17.sp)
                }
            }

            if (a.recommendations.isNotEmpty()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    a.recommendations.forEachIndexed { i, r ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 7.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppPanelElevated)
                                .padding(11.dp)
                        ) {
                            Text(
                                "${i + 1}".padStart(2, '0') + "  ",
                                fontSize = 11.sp,
                                fontFamily = MonoFont,
                                fontWeight = FontWeight.Bold,
                                color = AppTeal
                            )
                            Text(r, fontSize = 11.sp, color = AppMuted, lineHeight = 16.sp)
                        }
                    }
                }
            }
        }
    }
}
