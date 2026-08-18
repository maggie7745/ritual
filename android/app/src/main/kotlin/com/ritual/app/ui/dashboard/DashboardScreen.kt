package com.ritual.app.ui.dashboard

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritual.app.domain.RitualState
import com.ritual.app.domain.consistencyScore
import com.ritual.app.domain.currentStreak
import com.ritual.app.domain.longestStreak
import com.ritual.app.domain.sundayIndex
import com.ritual.app.ui.components.ChevronRightIcon
import com.ritual.app.ui.components.RitualCard
import com.ritual.app.ui.theme.CardBackground
import com.ritual.app.ui.theme.InstrumentSans
import com.ritual.app.ui.theme.RingTrack
import com.ritual.app.ui.theme.TextOnAccent
import com.ritual.app.ui.theme.TextPrimary
import com.ritual.app.ui.theme.TooltipBackground
import com.ritual.app.ui.theme.whiteAlpha
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val WEEKDAY_LABELS = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")

private data class DayPoint(val done: Int, val total: Int, val hasData: Boolean, val isFuture: Boolean)

@Composable
fun DashboardScreen(state: RitualState, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    val todayIndex = sundayIndex(today)
    val done = state.tasks.count { it.done }
    val total = state.tasks.size

    val streakValue = currentStreak(state.history, today)
    val longestStreakValue = longestStreak(state.history)
    val consistencyScoreValue = consistencyScore(state.history, today)
    val checkInsThisWeek = run {
        val weekStart = today.minusDays(todayIndex.toLong())
        (0..todayIndex).sumOf { i ->
            val d = weekStart.plusDays(i.toLong())
            if (d == today) done else (state.history[d]?.done ?: 0)
        }
    }

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
        Text(
            "THIS WEEK",
            color = whiteAlpha(0.38f),
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
        )
        Text(
            "Dashboard",
            color = TextPrimary,
            fontFamily = InstrumentSans,
            fontWeight = FontWeight.SemiBold,
            fontSize = 28.sp,
            modifier = Modifier.padding(top = 8.dp),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("$streakValue", "Current streak", modifier = Modifier.fillMaxWidth())
                StatTile("$checkInsThisWeek", "Check-ins this week", modifier = Modifier.fillMaxWidth())
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile("$longestStreakValue", "Longest streak", modifier = Modifier.fillMaxWidth())
                ConsistencyTile(score = consistencyScoreValue, modifier = Modifier.fillMaxWidth())
            }
        }

        WeeklyTrendCard(
            state = state,
            done = done,
            total = total,
            today = today,
            todayIndex = todayIndex,
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )

        RitualCard(modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text(
                "Habit consistency",
                color = TextPrimary,
                fontFamily = InstrumentSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 6.dp),
            )
            if (state.tasks.isEmpty()) {
                Text(
                    "No habits tracked yet. Add one from the Home tab.",
                    color = whiteAlpha(0.4f),
                    fontFamily = InstrumentSans,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                    textAlign = TextAlign.Center,
                )
            } else {
                // Each habit is real, but with no local persistence there's no history yet to
                // compute a real completion percentage from — show a "new" state instead of a fake number.
                state.tasks.forEach { task ->
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(task.name, color = TextPrimary, fontFamily = InstrumentSans, fontSize = 13.5.sp)
                            Text("new", color = whiteAlpha(0.4f), fontFamily = InstrumentSans, fontSize = 13.5.sp)
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .padding(top = 9.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(RingTrack),
                        )
                    }
                    HorizontalDivider(color = whiteAlpha(0.05f), thickness = 1.dp)
                }
                Text(
                    "Share of days each habit was completed in the last 28 days",
                    color = whiteAlpha(0.3f),
                    fontFamily = InstrumentSans,
                    fontSize = 10.5.sp,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        Spacer(Modifier.height(140.dp))
    }
}

@Composable
private fun WeeklyTrendCard(
    state: RitualState,
    done: Int,
    total: Int,
    today: LocalDate,
    todayIndex: Int,
    modifier: Modifier = Modifier,
) {
    val isCurrentWeek = state.weekOffset == 0
    val weekStart = today.minusDays(todayIndex.toLong()).plusWeeks(state.weekOffset.toLong())
    val weekEnd = weekStart.plusDays(6)
    val rangeFmt = DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)
    val rangeLabel = "${weekStart.format(rangeFmt)} – ${weekEnd.format(rangeFmt)}".uppercase(Locale.ENGLISH)
    val title = when (state.weekOffset) {
        0 -> "This week"
        -1 -> "Last week"
        else -> "Past week"
    }

    // Today's slot uses live task state; every other day reads from persisted history (or has no data yet).
    val dayPoints = List(7) { i ->
        val date = weekStart.plusDays(i.toLong())
        when {
            isCurrentWeek && i == todayIndex -> DayPoint(done, total, hasData = total > 0, isFuture = false)
            date.isAfter(today) -> DayPoint(0, 0, hasData = false, isFuture = true)
            else -> {
                val record = state.history[date]
                DayPoint(record?.done ?: 0, record?.total ?: 0, hasData = record != null, isFuture = false)
            }
        }
    }
    val values = dayPoints.map { it.done }
    val lastDrawn = if (isCurrentWeek) todayIndex else 6
    val selected = (state.selectedDay ?: if (isCurrentWeek) todayIndex else 6).coerceIn(0, lastDrawn)
    val yMax = maxOf(4, total, dayPoints.maxOf { it.total }).let { if (it % 2 == 0) it else it + 1 }

    RitualCard(modifier = modifier) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Text(title, color = TextPrimary, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                Text(
                    rangeLabel,
                    color = whiteAlpha(0.38f),
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            state.weekOffset -= 1
                            state.selectedDay = null
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    ChevronRightIcon(
                        color = whiteAlpha(0.75f),
                        modifier = Modifier.graphicsLayer { rotationZ = 180f },
                    )
                }
                val canGoForward = state.weekOffset < 0
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clickable(
                            enabled = canGoForward,
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                        ) {
                            state.weekOffset += 1
                            state.selectedDay = null
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    ChevronRightIcon(color = if (canGoForward) whiteAlpha(0.75f) else whiteAlpha(0.2f))
                }
            }
        }

        BoxWithConstraints(modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
            val chartWidth = this.maxWidth
            val gutter = 28.dp
            val slot = (chartWidth - gutter) / 7
            val chartHeight = 180.dp
            val selCenter = gutter + slot * selected + slot / 2

            Column {
                Box(modifier = Modifier.fillMaxWidth().height(chartHeight)) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(state.weekOffset, lastDrawn) {
                                detectTapGestures { tap ->
                                    val g = 28.dp.toPx()
                                    val sw = (size.width - g) / 7f
                                    val idx = ((tap.x - g) / sw).toInt().coerceIn(0, 6)
                                    if (idx <= lastDrawn) state.selectedDay = idx
                                }
                            },
                    ) {
                        val g = 28.dp.toPx()
                        val sw = (size.width - g) / 7f
                        val topPad = 8.dp.toPx()
                        val baseY = size.height - 1.dp.toPx()
                        fun xFor(i: Int) = g + sw * i + sw / 2
                        fun yFor(v: Int) = baseY - (v.toFloat() / yMax) * (baseY - topPad)

                        drawLine(whiteAlpha(0.1f), Offset(g, baseY), Offset(size.width, baseY), 1.dp.toPx())
                        val dash = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))
                        for (i in 0..6) {
                            drawLine(
                                whiteAlpha(0.08f),
                                Offset(xFor(i), topPad),
                                Offset(xFor(i), baseY),
                                1.dp.toPx(),
                                pathEffect = dash,
                            )
                        }

                        drawLine(whiteAlpha(0.22f), Offset(xFor(selected), topPad), Offset(xFor(selected), baseY), 1.5.dp.toPx())

                        if (lastDrawn >= 1) {
                            val path = Path().apply {
                                for (i in 0..lastDrawn) {
                                    if (i == 0) moveTo(xFor(i), yFor(values[i])) else lineTo(xFor(i), yFor(values[i]))
                                }
                            }
                            drawPath(
                                path,
                                color = whiteAlpha(0.55f),
                                style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
                            )
                        }

                        val selPoint = Offset(xFor(selected), yFor(values[selected]))
                        drawCircle(CardBackground, radius = 5.dp.toPx(), center = selPoint)
                        drawCircle(whiteAlpha(0.85f), radius = 5.dp.toPx(), center = selPoint, style = Stroke(2.dp.toPx()))
                    }

                    Box(modifier = Modifier.width(gutter).fillMaxHeight()) {
                        Text("$yMax", color = whiteAlpha(0.35f), fontFamily = InstrumentSans, fontSize = 10.sp, modifier = Modifier.align(Alignment.TopStart))
                        Text("${yMax / 2}", color = whiteAlpha(0.35f), fontFamily = InstrumentSans, fontSize = 10.sp, modifier = Modifier.align(Alignment.CenterStart))
                        Text("0", color = whiteAlpha(0.35f), fontFamily = InstrumentSans, fontSize = 10.sp, modifier = Modifier.align(Alignment.BottomStart))
                    }

                    val tooltipWidth = 140.dp
                    val leftX = selCenter - tooltipWidth - 12.dp
                    val tooltipX = if (leftX >= gutter) leftX else (selCenter + 12.dp).coerceAtMost(chartWidth - tooltipWidth)
                    val selDate = weekStart.plusDays(selected.toLong())
                    val selPointData = dayPoints[selected]

                    Column(
                        modifier = Modifier
                            .offset(x = tooltipX, y = 6.dp)
                            .width(tooltipWidth)
                            .background(TooltipBackground, RoundedCornerShape(16.dp))
                            .border(1.dp, whiteAlpha(0.08f), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                    ) {
                        Text(
                            selDate.format(DateTimeFormatter.ofPattern("EEE, MMM d", Locale.ENGLISH)),
                            color = whiteAlpha(0.5f),
                            fontFamily = InstrumentSans,
                            fontSize = 11.sp,
                        )
                        Text(
                            "${values[selected]}",
                            color = TextPrimary,
                            fontFamily = InstrumentSans,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                        Spacer(Modifier.height(8.dp))
                        if (selPointData.hasData) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Completed", color = whiteAlpha(0.55f), fontFamily = InstrumentSans, fontSize = 12.sp)
                                Text("${selPointData.done}", color = TextPrimary, fontFamily = InstrumentSans, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            }
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                            ) {
                                Text("Remaining", color = whiteAlpha(0.55f), fontFamily = InstrumentSans, fontSize = 12.sp)
                                Text("${selPointData.total - selPointData.done}", color = TextPrimary, fontFamily = InstrumentSans, fontWeight = FontWeight.Medium, fontSize = 12.sp)
                            }
                        } else {
                            Text(
                                if (isCurrentWeek && selected == todayIndex) "No habits yet" else "No data yet",
                                color = whiteAlpha(0.4f),
                                fontFamily = InstrumentSans,
                                fontSize = 11.5.sp,
                            )
                        }
                    }
                }

                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                    Spacer(Modifier.width(gutter))
                    WEEKDAY_LABELS.forEachIndexed { i, label ->
                        val isFuture = isCurrentWeek && i > lastDrawn
                        Text(
                            label,
                            color = when {
                                i == selected -> TextPrimary
                                isFuture -> whiteAlpha(0.25f)
                                else -> whiteAlpha(0.4f)
                            },
                            fontFamily = InstrumentSans,
                            fontWeight = if (i == selected) FontWeight.SemiBold else FontWeight.Medium,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .weight(1f)
                                .clickable(
                                    enabled = !isFuture,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                ) { state.selectedDay = i },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(value: String, label: String, modifier: Modifier = Modifier) {
    RitualCard(modifier = modifier) {
        Text(value, color = TextPrimary, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 30.sp)
        Text(label, color = whiteAlpha(0.5f), fontFamily = InstrumentSans, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun ConsistencyTile(score: Int, modifier: Modifier = Modifier) {
    RitualCard(modifier = modifier, backgroundColor = TextPrimary, borderAlpha = 0f) {
        Text("$score", color = TextOnAccent, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 30.sp)
        Text(
            "Consistency score",
            color = TextOnAccent.copy(alpha = 0.55f),
            fontFamily = InstrumentSans,
            fontSize = 12.sp,
            modifier = Modifier.padding(top = 6.dp),
        )
        Text(
            "% of check-ins kept · 28 days",
            color = TextOnAccent.copy(alpha = 0.4f),
            fontFamily = InstrumentSans,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 2.dp),
        )
    }
}
