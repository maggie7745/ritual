package com.ritual.app.domain

import java.time.LocalDate

/** A single task's completion state as it stood on a given day, kept for the day's history. */
data class TaskSnapshot(val id: Long, val name: String, val done: Boolean)

/** A snapshot of exactly which habits were done vs. pending on a given calendar day. */
data class DayRecord(val date: LocalDate, val tasks: List<TaskSnapshot>) {
    val done: Int get() = tasks.count { it.done }
    val total: Int get() = tasks.size
    val checkedIn: Boolean get() = done >= 1
}
