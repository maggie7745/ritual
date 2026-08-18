package com.ritual.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritual.app.domain.Tab
import com.ritual.app.ui.theme.CardBackground
import com.ritual.app.ui.theme.InstrumentSans
import com.ritual.app.ui.theme.TextPrimary
import com.ritual.app.ui.theme.whiteAlpha

@Composable
fun BottomNav(current: Tab, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CardBackground.copy(alpha = 0.92f), RoundedCornerShape(30.dp))
            .border(BorderStroke(1.dp, whiteAlpha(0.08f)), RoundedCornerShape(30.dp))
            .padding(8.dp, 6.dp),
    ) {
        NavItem(Tab.Home, "Home", current == Tab.Home, onSelect) { color -> HomeIcon(color) }
        NavItem(Tab.Dashboard, "Dashboard", current == Tab.Dashboard, onSelect) { color -> DashboardIcon(color) }
        NavItem(Tab.Profile, "Profile", current == Tab.Profile, onSelect) { color -> ProfileIcon(color) }
    }
}

@Composable
private fun RowScope.NavItem(
    tab: Tab,
    label: String,
    selected: Boolean,
    onSelect: (Tab) -> Unit,
    icon: @Composable (androidx.compose.ui.graphics.Color) -> Unit,
) {
    val color = if (selected) TextPrimary else whiteAlpha(0.34f)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = { onSelect(tab) },
            )
            .padding(vertical = 8.dp),
    ) {
        icon(color)
        Text(label, color = color, fontFamily = InstrumentSans, fontWeight = FontWeight.Medium, fontSize = 10.sp)
    }
}
