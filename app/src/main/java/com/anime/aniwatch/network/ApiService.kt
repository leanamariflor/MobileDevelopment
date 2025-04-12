package com.anime.aniwatch.network

import com.anime.aniwatch.data.Anime
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("api/v2/hianime/home")
    fun getHomeData(): Call<HomeResponse>
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