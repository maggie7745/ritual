package com.ritual.app.domain

data class Quote(val text: String, val author: String)

val QUOTES = listOf(
    Quote("We are what we repeatedly do. Excellence, then, is not an act, but a habit.", "Aristotle"),
    Quote("Small deeds done are better than great deeds planned.", "Peter Marshall"),
    Quote("The secret of getting ahead is getting started.", "Mark Twain"),
    Quote("It is not the mountain we conquer, but ourselves.", "Edmund Hillary"),
    Quote("Well begun is half done.", "Aristotle"),
    Quote("Little by little, one travels far.", "Proverb"),
    Quote("What you do every day matters more than what you do once in a while.", "Gretchen Rubin"),
)

fun quoteForDayOfYear(dayOfYear: Int): Quote = QUOTES[dayOfYear % QUOTES.size]
