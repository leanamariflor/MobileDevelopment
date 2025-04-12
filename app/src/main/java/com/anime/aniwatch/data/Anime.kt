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