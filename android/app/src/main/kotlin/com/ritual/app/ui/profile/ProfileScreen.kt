package com.ritual.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritual.app.ui.components.ChevronRightIcon
import com.ritual.app.ui.components.RitualCard
import com.ritual.app.ui.theme.InstrumentSans
import com.ritual.app.ui.theme.TextOnAccent
import com.ritual.app.ui.theme.TextPrimary
import com.ritual.app.ui.theme.whiteAlpha

private val SETTINGS = listOf("Notifications", "Appearance", "About Ritual")

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Ritual", color = TextPrimary, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 24.sp)
        Text(
            "Small habits, done daily. Create an account to keep your streaks safe.",
            color = whiteAlpha(0.5f),
            fontFamily = InstrumentSans,
            fontSize = 14.sp,
            lineHeight = 21.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )

        Column(modifier = Modifier.fillMaxWidth().padding(top = 32.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            AccountButton(text = "Create account", filled = true)
            AccountButton(text = "Log in", filled = false)
        }

        RitualCard(
            modifier = Modifier.fillMaxWidth().padding(top = 28.dp),
            contentPadding = PaddingValues(horizontal = 18.dp, vertical = 0.dp),
        ) {
            SETTINGS.forEach { label ->
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                ) {
                    Text(label, color = TextPrimary, fontFamily = InstrumentSans, fontSize = 14.5.sp)
                    ChevronRightIcon(color = whiteAlpha(0.3f))
                }
                if (label != SETTINGS.last()) HorizontalDivider(color = whiteAlpha(0.05f), thickness = 1.dp)
            }
        }

        Text(
            "Version 1.0 · Made calmly",
            color = whiteAlpha(0.28f),
            fontFamily = InstrumentSans,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 24.dp, bottom = 140.dp),
        )
    }
}

@Composable
private fun AccountButton(text: String, filled: Boolean) {
    val bg = if (filled) TextPrimary else androidx.compose.ui.graphics.Color.Transparent
    val fg = if (filled) TextOnAccent else TextPrimary
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(bg, RoundedCornerShape(26.dp))
            .border(BorderStroke(1.dp, if (filled) bg else whiteAlpha(0.18f)), RoundedCornerShape(26.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
    ) {
        Text(text, color = fg, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}
