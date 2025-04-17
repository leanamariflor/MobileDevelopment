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
data class EpisodeSourceResponse(
    val success: Boolean,
    val data: EpisodeSourceData
)

data class EpisodeSourceData(
    val headers: Map<String, String>,
    val tracks: List<Track>,
    val intro: TimeRange?,
    val outro: TimeRange?,
    val sources: List<Source>,
    val anilistID: Int,
    val malID: Int
)

data class Track(
    val file: String,
    val label: String,
    val kind: String,
    val default: Boolean = false
)

data class TimeRange(
    val start: Int,
    val end: Int
)

data class Source(
    val url: String,
    val type: String
)