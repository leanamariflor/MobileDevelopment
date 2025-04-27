package com.anime.aniwatch.data

import java.util.Date

data class DateItem(
    val date: Date,
    val dayOfWeek: String,
    val dayOfMonth: String,
    val formattedDate: String,
    var isSelected: Boolean = false
)