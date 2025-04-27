package com.anime.aniwatch.data

data class ScheduleResponse(
    val success: Boolean,
    val data: ScheduleData
)

data class ScheduleData(
    val scheduledAnimes: List<ScheduledAnime>
)

data class ScheduledAnime(
    val id: String,
    val time: String,
    val name: String,
    val jname: String,
    val airingTimestamp: Long,
    val secondsUntilAiring: Long,
    val episode: Int
)