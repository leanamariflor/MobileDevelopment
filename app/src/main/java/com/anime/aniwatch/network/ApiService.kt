package com.anime.aniwatch.network

import com.anime.aniwatch.data.Anime
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/v2/hianime/home")
    fun getHomeData(): Call<HomeResponse>

    @GET("api/v2/hianime/anime/{animeId}")
    fun getAnimeDetails(@Path("animeId") animeId: String): Call<AnimeResponse>

    @GET("api/v2/hianime/anime/{animeId}/episodes")
    fun getAnimeEpisodes(@Path("animeId") animeId: String): Call<EpisodeResponse>

    @GET("api/v2/hianime/episode/sources")
    fun getEpisodeSources(@Query("animeEpisodeId") episodeId: String): Call<EpisodeSourceResponse>
}

data class HomeResponse(
    val success: Boolean,
    val data: HomeData
)

data class HomeData(
    val top10Animes: Top10Animes
)

data class Top10Animes(
    val today: List<Anime>,
    val week: List<Anime>,
    val month: List<Anime>
)