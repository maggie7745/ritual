package com.ritual.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ritual.app.ui.theme.CardBackground
import com.ritual.app.ui.theme.whiteAlpha

@Composable
fun RitualCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = CardBackground,
    borderAlpha: Float = 0.07f,
    cornerRadius: Dp = 24.dp,
    contentPadding: PaddingValues = PaddingValues(18.dp),
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(cornerRadius))
            .border(BorderStroke(1.dp, whiteAlpha(borderAlpha)), RoundedCornerShape(cornerRadius))
            .padding(contentPadding),
        content = content,
    )
}
