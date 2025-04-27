package com.anime.aniwatch.data

data class WatchlistEpisode(
    var animeId: String = "",
    var animeTitle: String = "",
    var episodeId: String = "",
    var episodeTitle: String = "",
    var episodeNumber: Int = 0,
    var animePosterUrl: String = "",
    var dateAdded: String = "",
    var reminderDate: Long = 0, // Timestamp for the reminder
    var hasReminder: Boolean = false // Flag to indicate if a reminder is set
) {
    constructor() : this("", "", "", "", 0, "", "", 0, false)
}
