package com.ritual.app.domain

import java.time.LocalDate
import java.time.YearMonth

data class CalendarCell(
    val day: Int, // 0 = blank leading cell
    val isSelected: Boolean,
    val isToday: Boolean,
    val isFuture: Boolean,
    val offsetFromToday: Int,
)

/** Sunday-first weekday index: Sunday=0 .. Saturday=6. */
fun sundayIndex(date: LocalDate): Int = date.dayOfWeek.value % 7

fun calendarCells(today: LocalDate, selOffset: Int): List<CalendarCell> {
    val ym = YearMonth.from(today)
    val firstDow = sundayIndex(ym.atDay(1))
    val daysInMonth = ym.lengthOfMonth()
    val cells = mutableListOf<CalendarCell>()
    repeat(firstDow) { cells.add(CalendarCell(0, isSelected = false, isToday = false, isFuture = false, offsetFromToday = 0)) }
    for (day in 1..daysInMonth) {
        val future = day > today.dayOfMonth
        val offset = today.dayOfMonth - day
        cells.add(
            CalendarCell(
                day = day,
                isSelected = !future && selOffset == offset,
                isToday = day == today.dayOfMonth,
                isFuture = future,
                offsetFromToday = offset,
            )
        )
    }
    return cells
}

/**
 * Consecutive checked-in days ending today. If today has no check-in yet, the streak still
 * reflects the run ending yesterday — today only breaks it once the day is over with nothing done.
 */
fun currentStreak(history: Map<LocalDate, DayRecord>, today: LocalDate): Int {
    fun checkedIn(d: LocalDate) = history[d]?.checkedIn == true
    var day = if (checkedIn(today)) today else today.minusDays(1)
    var streak = 0
    while (checkedIn(day)) {
        streak++
        day = day.minusDays(1)
    }
    return streak
}

/** Longest run of consecutive checked-in calendar days across all recorded history. */
fun longestStreak(history: Map<LocalDate, DayRecord>): Int {
    val checkedInDates = history.values.filter { it.checkedIn }.map { it.date }.sorted()
    if (checkedInDates.isEmpty()) return 0
    var longest = 1
    var run = 1
    for (i in 1 until checkedInDates.size) {
        run = if (checkedInDates[i] == checkedInDates[i - 1].plusDays(1)) run + 1 else 1
        longest = maxOf(longest, run)
    }
    return longest
}

/** Fraction of active days (days with any habits) in the last [days] that had a check-in. */
fun consistencyScore(history: Map<LocalDate, DayRecord>, today: LocalDate, days: Int = 28): Int {
    val windowStart = today.minusDays((days - 1).toLong())
    val activeDays = history.values.filter { it.date >= windowStart && it.date <= today && it.total > 0 }
    if (activeDays.isEmpty()) return 0
    val checkedIn = activeDays.count { it.checkedIn }
    return (checkedIn * 100) / activeDays.size
}
