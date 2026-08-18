package com.ritual.app.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import java.time.LocalDate

enum class Tab { Home, Dashboard, Profile }

class RitualState {
    var tab by mutableStateOf(Tab.Home)

    /** Starts empty for a first-run experience — no fabricated history for a new user. */
    val tasks: SnapshotStateList<Task> = mutableStateListOf()

    /** Loaded from disk on launch; one entry per day that has ever had tasks. */
    val history: SnapshotStateMap<LocalDate, DayRecord> = mutableStateMapOf()

    var newTaskText by mutableStateOf("")
    var newTaskDaily by mutableStateOf(true)

    /** Days before today; 0 = today. */
    var selOffset by mutableStateOf(0)
    var showCalendar by mutableStateOf(false)

    /** Selected day (Sun=0) in the dashboard week chart; null = default (today, or Saturday for past weeks). */
    var selectedDay by mutableStateOf<Int?>(null)

    /** Week shown in the dashboard chart: 0 = current week, negative = weeks back. */
    var weekOffset by mutableStateOf(0)

    fun toggleTask(id: Long) {
        val idx = tasks.indexOfFirst { it.id == id }
        if (idx != -1) tasks[idx] = tasks[idx].copy(done = !tasks[idx].done)
    }

    fun removeTask(id: Long) {
        tasks.removeAll { it.id == id }
    }

    fun addTask() {
        val name = newTaskText.trim()
        if (name.isEmpty()) return
        tasks.add(
            0,
            Task(
                id = System.currentTimeMillis(),
                name = name,
                meta = if (newTaskDaily) "Daily" else "Once",
                done = false,
                daily = newTaskDaily,
            )
        )
        newTaskText = ""
    }

    fun selectDay(offset: Int) {
        selOffset = offset
        showCalendar = false
    }

    fun backToToday() {
        selOffset = 0
        showCalendar = false
    }
}
