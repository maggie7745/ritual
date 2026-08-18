package com.ritual.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ritual.app.data.HistoryStore
import com.ritual.app.data.TaskStore
import com.ritual.app.domain.DayRecord
import com.ritual.app.domain.RitualState
import com.ritual.app.domain.Tab
import com.ritual.app.domain.Task
import com.ritual.app.domain.TaskSnapshot
import com.ritual.app.ui.components.BottomNav
import com.ritual.app.ui.dashboard.DashboardScreen
import com.ritual.app.ui.home.HomeScreen
import com.ritual.app.ui.profile.ProfileScreen
import com.ritual.app.ui.theme.AppBackground
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

private fun dayRecordFor(date: LocalDate, tasks: List<Task>): DayRecord =
    DayRecord(date = date, tasks = tasks.map { TaskSnapshot(id = it.id, name = it.name, done = it.done) })

// Task.id is a System.currentTimeMillis() timestamp from when it was added.
private fun Task.createdDate(): LocalDate =
    Instant.ofEpochMilli(id).atZone(ZoneId.systemDefault()).toLocalDate()

@Composable
fun RitualApp() {
    val state = remember { RitualState() }
    val appContext = LocalContext.current.applicationContext
    val taskStore = remember { TaskStore(appContext) }
    val historyStore = remember { HistoryStore(appContext) }

    LaunchedEffect(Unit) {
        val savedTasks = taskStore.tasksFlow.first()
        val lastOpenedDate = taskStore.lastOpenedDateFlow.first()
        val today = LocalDate.now()

        // "Once" tasks belong only to the day they were created — drop any that are stale on
        // every launch (checked against their own creation date, not just a once-a-day flag, so
        // a stale task can't slip through if that flag was already marked processed for today
        // under an older build). Their history for the day they existed is already saved.
        val freshTasks = savedTasks.filter { it.daily || it.createdDate() == today }

        // Daily habits carry over and reset to pending on a new day.
        val isNewDay = lastOpenedDate != today
        val rolledTasks = if (isNewDay) {
            freshTasks.map { if (it.daily && it.done) it.copy(done = false) else it }
        } else {
            freshTasks
        }
        state.tasks.clear()
        state.tasks.addAll(rolledTasks)

        val savedHistory = historyStore.historyFlow.first()
        state.history.clear()
        state.history.putAll(savedHistory)

        if (isNewDay) taskStore.saveLastOpenedDate(today)
        if (isNewDay || rolledTasks != savedTasks) {
            taskStore.saveTasks(rolledTasks)
            val record = dayRecordFor(today, rolledTasks)
            state.history[today] = record
            historyStore.saveDay(record)
        }

        // Skip the emission that reflects our own load above — nothing new to persist yet.
        snapshotFlow { state.tasks.toList() }
            .drop(1)
            .collect { tasks ->
                taskStore.saveTasks(tasks)
                val record = dayRecordFor(LocalDate.now(), tasks)
                state.history[record.date] = record
                historyStore.saveDay(record)
            }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
    ) {
        val scroll = rememberScrollState()
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .statusBarsPadding()
                .padding(top = 12.dp),
        ) {
            when (state.tab) {
                Tab.Home -> HomeScreen(state = state, userName = "Priyanshu")
                Tab.Dashboard -> DashboardScreen(state = state)
                Tab.Profile -> ProfileScreen()
            }
        }

        BottomNav(
            current = state.tab,
            onSelect = { state.tab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 18.dp, vertical = 14.dp),
        )
    }
}
