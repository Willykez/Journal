package com.forexjournal.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.forexjournal.app.data.Metrics
import com.forexjournal.app.ui.theme.AppBg
import com.forexjournal.app.ui.theme.AppGreen
import com.forexjournal.app.ui.theme.AppMuted
import com.forexjournal.app.ui.theme.AppPanel
import com.forexjournal.app.ui.theme.AppPanelElevated
import com.forexjournal.app.ui.theme.AppRed
import com.forexjournal.app.ui.theme.AppTeal
import com.forexjournal.app.ui.theme.AppText
import com.forexjournal.app.ui.theme.MonoFont

enum class DrawerDestination(val label: String) { OVERVIEW("Overview"), TRADES("Trade Blotter"), CALENDAR("Execution Calendar") }

@Composable
fun AppDrawer(
    metrics: Metrics?,
    tradeCount: Int,
    currentDestination: DrawerDestination,
    onDestinationSelected: (DrawerDestination) -> Unit,
    onConnectClick: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = AppPanel,
        modifier = Modifier.fillMaxWidth(fraction = 0.82f)
    ) {
        // ---- Brand header ----
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Brush.linearGradient(listOf(AppTeal, AppTeal.copy(alpha = 0.5f)))),
                contentAlignment = Alignment.Center
            ) {
                Text("TJ", color = AppBg, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.padding(start = 12.dp))
            Column {
                Text("Trade Journal", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = AppText)
                Text("TERMINAL ANALYST", fontSize = 9.sp, color = AppMuted, letterSpacing = 1.sp)
            }
        }

        HorizontalDivider(color = AppPanelElevated)

        // ---- Account summary, mirrors an at-a-glance trading account card ----
        Column(modifier = Modifier.padding(20.dp)) {
            Text("ACTIVE ACCOUNT", fontSize = 9.sp, color = AppMuted, letterSpacing = 0.8.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            val netPnl = metrics?.totalPnL ?: 0.0
            Text(
                (if (netPnl >= 0) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(netPnl)),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = MonoFont,
                color = if (netPnl >= 0) AppGreen else AppRed
            )
            Text("$tradeCount trades logged", fontSize = 11.sp, color = AppMuted, modifier = Modifier.padding(top = 2.dp))
        }

        HorizontalDivider(color = AppPanelElevated)

        Spacer(modifier = Modifier.height(8.dp))

        DrawerNavItem(Icons.Filled.Dashboard, DrawerDestination.OVERVIEW, currentDestination, onDestinationSelected)
        DrawerNavItem(Icons.Filled.ReceiptLong, DrawerDestination.TRADES, currentDestination, onDestinationSelected)
        DrawerNavItem(Icons.Filled.CalendarMonth, DrawerDestination.CALENDAR, currentDestination, onDestinationSelected)

        Spacer(modifier = Modifier.height(8.dp))
        HorizontalDivider(color = AppPanelElevated)
        Spacer(modifier = Modifier.height(8.dp))

        NavigationDrawerItem(
            icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
            label = { Text("Connect Sheet", fontSize = 13.sp) },
            selected = false,
            onClick = onConnectClick,
            colors = NavigationDrawerItemDefaults.colors(
                unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                unselectedIconColor = AppMuted,
                unselectedTextColor = AppMuted
            ),
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.weight(1f, fill = true))

        Text(
            "v1.0 \u00B7 Native Compose",
            fontSize = 9.sp,
            color = AppMuted.copy(alpha = 0.6f),
            modifier = Modifier.padding(20.dp)
        )
    }
}

@Composable
private fun DrawerNavItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    destination: DrawerDestination,
    current: DrawerDestination,
    onSelect: (DrawerDestination) -> Unit
) {
    val selected = destination == current
    NavigationDrawerItem(
        icon = { Icon(icon, contentDescription = null) },
        label = { Text(destination.label, fontSize = 13.sp, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal) },
        selected = selected,
        onClick = { onSelect(destination) },
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = AppTeal.copy(alpha = 0.14f),
            selectedIconColor = AppTeal,
            selectedTextColor = AppTeal,
            unselectedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
            unselectedIconColor = AppMuted,
            unselectedTextColor = AppText
        ),
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
    )
}
