package com.anime.aniwatch.network

import com.anime.aniwatch.data.Episodes

data class AnimeResponse(
    val success: Boolean,
    val data: AnimeData
)

data class AnimeData(
    val anime: AnimeDetails,
    val seasons: List<Season>
)

data class AnimeDetails(
    val info: AnimeInfo,
    val moreInfo: MoreInfo,
)

data class Season(
    val id: String,
    val name: String,
    val title: String,
    val poster: String,
    val isCurrent: Boolean
)

data class AnimeInfo(
    val id: String,
    val name: String,
    val poster: String,
    val description: String,
    val stats: Stats
)

data class Stats(
    val episodes: Episodes,
    val type: String
)

data class MoreInfo(
    val genres: List<String>,
    val malscore: String
)

data class EpisodeResponse(
    val success: Boolean,
    val data: EpisodeData
)

data class EpisodeData(
    val totalEpisodes: Int,
    val episodes: List<Episode>
)

data class Episode(
    val title: String,
    val episodeId: String,
    val number: Int,
    val isFiller: Boolean
)