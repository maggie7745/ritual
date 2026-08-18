package com.ritual.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritual.app.ui.theme.InstrumentSans
import com.ritual.app.ui.theme.whiteAlpha

@Composable
fun Pill(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    textColor: Color = if (filled) Color(0xFF111111) else whiteAlpha(0.65f),
    fillColor: Color = if (filled) Color(0xFFF2F1EE) else Color.Transparent,
    borderColor: Color = if (filled) fillColor else whiteAlpha(0.16f),
) {
    Text(
        text = text,
        color = textColor,
        fontFamily = InstrumentSans,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .background(fillColor, RoundedCornerShape(999.dp))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(999.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
}
