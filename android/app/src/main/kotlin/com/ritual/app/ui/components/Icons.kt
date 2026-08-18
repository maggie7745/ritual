package com.ritual.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Hairline line-art icons matching the design's custom SVG set, drawn on a `viewBoxPx` square grid. */
@Composable
private fun LineIcon(
    modifier: Modifier = Modifier,
    size: Dp,
    viewBoxPx: Float = 24f,
    strokeWidthPx: Float = 1.6f,
    draw: DrawScope.(scale: Float, stroke: Stroke) -> Unit,
) {
    Canvas(modifier = modifier.size(size)) {
        val scale = this.size.width / viewBoxPx
        val stroke = Stroke(width = strokeWidthPx * scale, cap = StrokeCap.Round, join = StrokeJoin.Round)
        draw(scale, stroke)
    }
}

@Composable
fun HomeIcon(color: Color, size: Dp = 21.dp, modifier: Modifier = Modifier) {
    LineIcon(modifier = modifier, size = size) { s, stroke ->
        val path = Path().apply {
            moveTo(4 * s, 11.5f * s)
            lineTo(12 * s, 4.5f * s)
            lineTo(20 * s, 11.5f * s)
            lineTo(20 * s, 19.5f * s)
            lineTo(4 * s, 19.5f * s)
            close()
        }
        drawPath(path, color = color, style = stroke)
    }
}

@Composable
fun DashboardIcon(color: Color, size: Dp = 21.dp, modifier: Modifier = Modifier) {
    LineIcon(modifier = modifier, size = size) { s, stroke ->
        drawLine(color, Offset(6 * s, 20 * s), Offset(6 * s, 11 * s), strokeWidth = stroke.width, cap = stroke.cap)
        drawLine(color, Offset(12 * s, 20 * s), Offset(12 * s, 4 * s), strokeWidth = stroke.width, cap = stroke.cap)
        drawLine(color, Offset(18 * s, 20 * s), Offset(18 * s, 14 * s), strokeWidth = stroke.width, cap = stroke.cap)
    }
}

@Composable
fun ProfileIcon(color: Color, size: Dp = 21.dp, modifier: Modifier = Modifier) {
    LineIcon(modifier = modifier, size = size) { s, stroke ->
        drawCircle(color, radius = 3.4f * s, center = Offset(12 * s, 8 * s), style = stroke)
        val path = Path().apply {
            moveTo(5 * s, 20 * s)
            cubicTo(6.2f * s, 16.6f * s, 9 * s, 15 * s, 12 * s, 15 * s)
            cubicTo(15 * s, 15 * s, 17.8f * s, 16.6f * s, 19 * s, 20 * s)
        }
        drawPath(path, color = color, style = stroke)
    }
}

@Composable
fun CalendarIcon(color: Color, size: Dp = 19.dp, modifier: Modifier = Modifier) {
    LineIcon(modifier = modifier, size = size) { s, stroke ->
        drawRoundRect(
            color = color,
            topLeft = Offset(3.5f * s, 5 * s),
            size = Size(17 * s, 15.5f * s),
            cornerRadius = CornerRadius(4 * s, 4 * s),
            style = stroke,
        )
        drawLine(color, Offset(3.5f * s, 10 * s), Offset(20.5f * s, 10 * s), strokeWidth = stroke.width, cap = stroke.cap)
        drawLine(color, Offset(8 * s, 3 * s), Offset(8 * s, 6.5f * s), strokeWidth = stroke.width, cap = stroke.cap)
        drawLine(color, Offset(16 * s, 3 * s), Offset(16 * s, 6.5f * s), strokeWidth = stroke.width, cap = stroke.cap)
    }
}

@Composable
fun ChevronRightIcon(color: Color, size: Dp = 12.dp, modifier: Modifier = Modifier) {
    LineIcon(modifier = modifier, size = size, viewBoxPx = 12f, strokeWidthPx = 1.6f) { s, stroke ->
        val path = Path().apply {
            moveTo(1 * s, 1 * s)
            lineTo(6 * s, 6 * s)
            lineTo(1 * s, 11 * s)
        }
        drawPath(path, color = color, style = stroke)
    }
}

@Composable
fun PlusIcon(color: Color, size: Dp = 14.dp, modifier: Modifier = Modifier) {
    LineIcon(modifier = modifier, size = size, viewBoxPx = 14f, strokeWidthPx = 1.8f) { s, stroke ->
        drawLine(color, Offset(7 * s, 1 * s), Offset(7 * s, 13 * s), strokeWidth = stroke.width, cap = stroke.cap)
        drawLine(color, Offset(1 * s, 7 * s), Offset(13 * s, 7 * s), strokeWidth = stroke.width, cap = stroke.cap)
    }
}

@Composable
fun CloseIcon(color: Color, size: Dp = 12.dp, modifier: Modifier = Modifier) {
    // Both strokes must be a single Path drawn in one pass — two separate drawLine calls with a
    // translucent color double-blend where they cross, leaving a bright smudge at the center.
    LineIcon(modifier = modifier, size = size, viewBoxPx = 12f, strokeWidthPx = 2.1f) { s, stroke ->
        val path = Path().apply {
            moveTo(2.5f * s, 2.5f * s)
            lineTo(9.5f * s, 9.5f * s)
            moveTo(9.5f * s, 2.5f * s)
            lineTo(2.5f * s, 9.5f * s)
        }
        drawPath(path, color = color, style = stroke)
    }
}

@Composable
fun CheckIcon(color: Color, size: Dp = 12.dp, modifier: Modifier = Modifier) {
    LineIcon(modifier = modifier, size = size, viewBoxPx = 12f, strokeWidthPx = 1.8f) { s, stroke ->
        val path = Path().apply {
            moveTo(2 * s, 6.5f * s)
            lineTo(4.8f * s, 9.2f * s)
            lineTo(10 * s, 3 * s)
        }
        drawPath(path, color = color, style = stroke)
    }
}
