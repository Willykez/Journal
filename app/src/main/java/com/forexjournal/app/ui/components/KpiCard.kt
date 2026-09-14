package com.forexjournal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont

@Composable
fun KpiCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = AppText,
    accentColor: Color = AppMuted,
    icon: ImageVector? = null,
    sub: String? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppPanel),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Colored accent stripe, ties each metric to a semantic color at a glance
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(accentColor.copy(alpha = 0.7f))
            )
            Column(modifier = Modifier.padding(12.dp).fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icon != null) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(5.dp))
                    }
                    Text(label.uppercase(), fontSize = 9.sp, color = AppMuted, letterSpacing = 0.5.sp, fontWeight = FontWeight.Medium)
                }
                Text(
                    value,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = MonoFont,
                    color = valueColor,
                    modifier = Modifier.padding(top = 5.dp)
                )
                if (sub != null) {
                    Text(sub, fontSize = 10.sp, color = AppMuted, modifier = Modifier.padding(top = 3.dp))
                }
            }
        }
    }
}
