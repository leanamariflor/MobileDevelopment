package com.anime.aniwatch.data

data class WatchlistEpisode(
    var animeId: String = "",
    var animeTitle: String = "",
    var episodeId: String = "",
    var episodeTitle: String = "",
    var episodeNumber: Int = 0,
    var animePosterUrl: String = "",
    var dateAdded: String = ""
) {
    constructor() : this("", "", "", "", 0, "", "")
}