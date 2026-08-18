package com.ritual.app.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ritual.app.domain.CalendarCell
import com.ritual.app.domain.RitualState
import com.ritual.app.domain.Task
import com.ritual.app.domain.calendarCells
import com.ritual.app.domain.currentStreak
import com.ritual.app.domain.TaskSnapshot
import com.ritual.app.domain.quoteForDayOfYear
import com.ritual.app.ui.components.CalendarIcon
import com.ritual.app.ui.components.CheckIcon
import com.ritual.app.ui.components.CloseIcon
import com.ritual.app.ui.components.Pill
import com.ritual.app.ui.components.PlusIcon
import com.ritual.app.ui.components.RitualCard
import com.ritual.app.ui.theme.CardBackgroundSubtle
import com.ritual.app.ui.theme.InstrumentSans
import com.ritual.app.ui.theme.InstrumentSerif
import com.ritual.app.ui.theme.RingTrack
import com.ritual.app.ui.theme.TextOnAccent
import com.ritual.app.ui.theme.TextPrimary
import com.ritual.app.ui.theme.whiteAlpha
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HomeScreen(state: RitualState, userName: String, modifier: Modifier = Modifier) {
    val today = LocalDate.now()
    val hour = LocalTime.now().hour
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 18 -> "Good afternoon"
        else -> "Good evening"
    }
    val dateLabel = today.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH))
    val quote = quoteForDayOfYear(today.dayOfYear)

    val activeTasks = state.tasks.filter { !it.done }
    val doneTasks = state.tasks.filter { it.done }
    val total = state.tasks.size
    val done = doneTasks.size

    val isTodaySelected = state.selOffset == 0
    val selDate = today.minusDays(state.selOffset.toLong())
    val pastRecord = state.history[selDate]
    val useDone = if (isTodaySelected) done else (pastRecord?.done ?: 0)
    val useTotal = if (isTodaySelected) total else (pastRecord?.total ?: 0)
    val selPct = if (useTotal == 0) 0 else (useDone * 100f / useTotal).roundToInt()
    val selDayLabel = if (isTodaySelected) "today" else selDate.format(DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH))
    val selFullLabel = selDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH))
    val verdict = when {
        useTotal == 0 -> "No data for this day yet."
        useDone >= useTotal -> "A perfect day."
        useDone >= useTotal * 0.7 -> "A solid day."
        else -> "A lighter day. That's okay."
    }
    val streak = currentStreak(state.history, today)
    // Fills left-to-right as a progress indicator (dot 1 lights at streak>=1, dot 2 at streak>=2, ...)
    // rather than mapping to specific calendar days.
    val weekDots = (0 until 7).map { i -> i < streak.coerceAtMost(7) }

    // The header reflects whichever day the calendar has selected, not always "today". The small
    // label carries the date, so the big title switches to a non-date phrase for past days —
    // otherwise the same date would print twice.
    val headerLabel = if (isTodaySelected) dateLabel else selDate.format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH))
    val headerTitle = if (isTodaySelected) "$greeting,\n$userName" else "Looking back,\n$userName"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Text(
                    text = headerLabel.uppercase(Locale.ENGLISH),
                    color = whiteAlpha(0.38f),
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                )
                Text(
                    text = headerTitle,
                    color = TextPrimary,
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    lineHeight = 32.sp,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            CalendarToggle(active = state.showCalendar, onClick = { state.showCalendar = !state.showCalendar })
        }

        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(fontFamily = InstrumentSerif, fontStyle = FontStyle.Italic, fontSize = 13.5.sp, color = whiteAlpha(0.45f))) {
                    append("“${quote.text}”")
                }
                append(" ")
                withStyle(SpanStyle(fontFamily = InstrumentSans, fontSize = 11.sp, color = whiteAlpha(0.3f))) {
                    append("· ${quote.author}")
                }
            },
            lineHeight = 20.sp,
            modifier = Modifier.padding(top = 12.dp),
        )

        AnimatedVisibility(visible = state.showCalendar) {
            CalendarPanel(
                today = today,
                selOffset = state.selOffset,
                onPickOffset = { state.selectDay(it) },
                onToday = { state.backToToday() },
                modifier = Modifier.padding(top = 18.dp),
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        ) {
            RingCard(pct = selPct, done = useDone, total = useTotal, dayLabel = selDayLabel, modifier = Modifier.weight(1f))
            StreakCard(streak = streak, weekDots = weekDots, modifier = Modifier.weight(1f))
        }

        if (isTodaySelected) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 4.dp),
            ) {
                Text("Today", color = TextPrimary, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
                val remaining = when {
                    total == 0 -> "no habits yet"
                    activeTasks.isNotEmpty() -> "${activeTasks.size} remaining"
                    else -> "all done"
                }
                Text(remaining, color = whiteAlpha(0.42f), fontFamily = InstrumentSans, fontSize = 12.sp)
            }

            AddTaskRow(state = state, modifier = Modifier.padding(top = 12.dp, bottom = 6.dp))

            RitualCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
            ) {
                activeTasks.forEach { task ->
                    ActiveTaskRow(
                        task = task,
                        onToggle = { state.toggleTask(task.id) },
                        onDelete = { state.removeTask(task.id) },
                    )
                }
                if (activeTasks.isEmpty()) {
                    Text(
                        if (total == 0) "No habits yet. Add your first one above." else "All done for today. Well earned.",
                        color = whiteAlpha(0.4f),
                        fontFamily = InstrumentSans,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 22.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }

            if (doneTasks.isNotEmpty()) {
                Text(
                    "COMPLETED · ${doneTasks.size}",
                    color = whiteAlpha(0.32f),
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp),
                )
                RitualCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = CardBackgroundSubtle,
                    borderAlpha = 0.05f,
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                ) {
                    doneTasks.forEach { task ->
                        DoneTaskRow(
                            task = task,
                            onToggle = { state.toggleTask(task.id) },
                            onDelete = { state.removeTask(task.id) },
                        )
                    }
                }
            }
        } else {
            RitualCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                contentPadding = PaddingValues(26.dp),
            ) {
                Text(
                    "$useDone of $useTotal",
                    color = TextPrimary,
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 30.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Text(
                    "habits completed on $selFullLabel",
                    color = whiteAlpha(0.5f),
                    fontFamily = InstrumentSans,
                    fontSize = 13.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Text(
                    verdict,
                    color = whiteAlpha(0.35f),
                    fontFamily = InstrumentSans,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
                Box(modifier = Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) {
                    Pill(text = "Back to today", onClick = { state.backToToday() })
                }
            }

            if (!pastRecord?.tasks.isNullOrEmpty()) {
                Text(
                    "HABITS THAT DAY",
                    color = whiteAlpha(0.32f),
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    modifier = Modifier.padding(top = 24.dp, bottom = 10.dp),
                )
                RitualCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp),
                ) {
                    pastRecord?.tasks?.forEach { snapshot -> PastTaskRow(snapshot) }
                }
            }
        }

        Spacer(Modifier.height(140.dp))
    }
}

@Composable
private fun CalendarToggle(active: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (active) TextPrimary else Color.Transparent)
            .border(BorderStroke(1.dp, if (active) TextPrimary else whiteAlpha(0.16f)), CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        CalendarIcon(color = if (active) TextOnAccent else whiteAlpha(0.75f))
    }
}

@Composable
private fun CalendarPanel(
    today: LocalDate,
    selOffset: Int,
    onPickOffset: (Int) -> Unit,
    onToday: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cells = remember(today, selOffset) { calendarCells(today, selOffset) }
    RitualCard(modifier = modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                today.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH)),
                color = TextPrimary,
                fontFamily = InstrumentSans,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
            )
            Pill(text = "Today", onClick = onToday)
        }
        Spacer(Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { w ->
                Text(
                    w,
                    color = whiteAlpha(0.35f),
                    fontFamily = InstrumentSans,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
                week.forEach { cell -> CalendarDayCell(cell, onPickOffset, Modifier.weight(1f)) }
                repeat(7 - week.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(cell: CalendarCell, onPick: (Int) -> Unit, modifier: Modifier) {
    if (cell.day == 0) {
        Spacer(modifier.aspectRatio(1f))
        return
    }
    val bg = if (cell.isSelected) TextPrimary else Color.Transparent
    val fg = if (cell.isSelected) TextOnAccent else if (cell.isFuture) whiteAlpha(0.18f) else TextPrimary
    val border = if (cell.isToday && !cell.isSelected) whiteAlpha(0.4f) else Color.Transparent
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(bg)
            .border(BorderStroke(1.dp, border), CircleShape)
            .clickable(
                enabled = !cell.isFuture,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onPick(cell.offsetFromToday) },
        contentAlignment = Alignment.Center,
    ) {
        Text(cell.day.toString(), color = fg, fontFamily = InstrumentSans, fontWeight = FontWeight.Medium, fontSize = 12.5.sp)
    }
}

@Composable
private fun RingCard(pct: Int, done: Int, total: Int, dayLabel: String, modifier: Modifier = Modifier) {
    RitualCard(modifier = modifier) {
        Box(modifier = Modifier.size(76.dp).align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center) {
            androidx.compose.foundation.Canvas(modifier = Modifier.size(76.dp)) {
                val strokePx = 6.dp.toPx()
                val radius = (size.minDimension - strokePx) / 2f
                drawCircle(RingTrack, radius = radius, style = Stroke(strokePx))
                val sweep = 360f * (pct / 100f)
                drawArc(
                    color = TextPrimary,
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    topLeft = Offset((size.width - radius * 2) / 2f, (size.height - radius * 2) / 2f),
                    size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
                    style = Stroke(strokePx, cap = StrokeCap.Round),
                )
            }
            Text("$pct%", color = TextPrimary, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        }
        Text(
            "$done of $total · $dayLabel",
            color = whiteAlpha(0.5f),
            fontFamily = InstrumentSans,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

@Composable
private fun StreakCard(streak: Int, weekDots: List<Boolean>, modifier: Modifier = Modifier) {
    RitualCard(modifier = modifier) {
        Text("$streak", color = TextPrimary, fontFamily = InstrumentSans, fontWeight = FontWeight.SemiBold, fontSize = 34.sp)
        Text("day streak", color = whiteAlpha(0.5f), fontFamily = InstrumentSans, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.padding(top = 8.dp)) {
            weekDots.forEach { filled ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (filled) TextPrimary else Color.Transparent)
                        .border(BorderStroke(1.dp, whiteAlpha(0.25f)), CircleShape),
                )
            }
        }
    }
}

@Composable
private fun AddTaskRow(state: RitualState, modifier: Modifier = Modifier) {
    RitualCard(
        modifier = modifier.fillMaxWidth(),
        cornerRadius = 22.dp,
        borderAlpha = 0.1f,
        contentPadding = PaddingValues(start = 18.dp, end = 8.dp, top = 8.dp, bottom = 8.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) {
                if (state.newTaskText.isEmpty()) {
                    Text("Add a task or habit…", color = whiteAlpha(0.3f), fontFamily = InstrumentSans, fontSize = 14.sp)
                }
                BasicTextField(
                    value = state.newTaskText,
                    onValueChange = { state.newTaskText = it },
                    textStyle = androidx.compose.ui.text.TextStyle(
                        color = TextPrimary,
                        fontFamily = InstrumentSans,
                        fontSize = 14.sp,
                    ),
                    singleLine = true,
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(TextPrimary),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { state.addTask() }),
                    modifier = Modifier.fillMaxWidth().height(32.dp),
                )
            }
            Spacer(Modifier.width(8.dp))
            Pill(
                text = if (state.newTaskDaily) "Daily" else "Once",
                filled = state.newTaskDaily,
                onClick = { state.newTaskDaily = !state.newTaskDaily },
            )
            Spacer(Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(TextPrimary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { state.addTask() },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                PlusIcon(color = TextOnAccent, size = 14.dp)
            }
        }
    }
}

@Composable
private fun ActiveTaskRow(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggle,
                    ),
            ) {
                androidx.compose.foundation.Canvas(Modifier.size(26.dp)) {
                    drawCircle(whiteAlpha(0.3f), style = Stroke(1.5.dp.toPx()))
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(task.name, color = TextPrimary, fontFamily = InstrumentSans, fontSize = 15.sp)
                Text(task.meta, color = whiteAlpha(0.38f), fontFamily = InstrumentSans, fontSize = 11.5.sp, modifier = Modifier.padding(top = 2.dp))
            }
            DeleteButton(onDelete = onDelete)
        }
        HorizontalDivider(color = whiteAlpha(0.05f), thickness = 1.dp)
    }
}

@Composable
private fun DoneTaskRow(task: Task, onToggle: () -> Unit, onDelete: () -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(TextPrimary)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggle,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                CheckIcon(color = TextOnAccent, size = 12.dp)
            }
            Spacer(Modifier.width(14.dp))
            Text(
                task.name,
                color = whiteAlpha(0.35f),
                fontFamily = InstrumentSans,
                fontSize = 15.sp,
                textDecoration = TextDecoration.LineThrough,
                modifier = Modifier.weight(1f),
            )
            DeleteButton(onDelete = onDelete)
        }
        HorizontalDivider(color = whiteAlpha(0.04f), thickness = 1.dp)
    }
}

@Composable
private fun PastTaskRow(snapshot: TaskSnapshot) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().height(52.dp)) {
            if (snapshot.done) {
                Box(
                    modifier = Modifier.size(26.dp).clip(CircleShape).background(TextPrimary),
                    contentAlignment = Alignment.Center,
                ) {
                    CheckIcon(color = TextOnAccent, size = 12.dp)
                }
            } else {
                androidx.compose.foundation.Canvas(Modifier.size(26.dp)) {
                    drawCircle(whiteAlpha(0.3f), style = Stroke(1.5.dp.toPx()))
                }
            }
            Spacer(Modifier.width(14.dp))
            Text(
                snapshot.name,
                color = if (snapshot.done) whiteAlpha(0.35f) else TextPrimary,
                fontFamily = InstrumentSans,
                fontSize = 15.sp,
                textDecoration = if (snapshot.done) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f),
            )
        }
        HorizontalDivider(color = whiteAlpha(0.05f), thickness = 1.dp)
    }
}

@Composable
private fun DeleteButton(onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDelete,
            ),
        contentAlignment = Alignment.Center,
    ) {
        CloseIcon(color = whiteAlpha(0.55f), size = 15.dp)
    }
}
