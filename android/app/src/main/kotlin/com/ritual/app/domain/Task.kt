package com.ritual.app.domain

data class Task(
    val id: Long,
    val name: String,
    val meta: String,
    val done: Boolean,
    /** Daily habits reset to pending each new day; "Once" tasks keep whatever state they're left in. */
    val daily: Boolean = true,
)
