package com.anime.aniwatch.data

data class Anime(
    val id: String,
    val rank: Int,
    val name: String,
    val jname: String,
    val poster: String,
    val episodes: Episodes
)

data class Episodes(
    val sub: Int?,
    val dub: Int?
)

data class WatchHistory(
    var animeId: String = "",
    var animeTitle: String = "",
    var episodeId: String = "",
    var episodeTitle: String = "",
    var episodeNumber: Int = 0,
    var watchedTime: Long = 0L,
    var totalTime: Long = 0L,
    var dateWatched: String = "",
    var animePosterUrl: String = ""
) {
    constructor() : this("", "", "", "", 0, 0L, 0L, "", "")
}