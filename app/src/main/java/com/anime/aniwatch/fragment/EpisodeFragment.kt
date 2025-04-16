package com.anime.aniwatch.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.R
import com.anime.aniwatch.adapter.EpisodeAdapter
import com.anime.aniwatch.network.ApiService
import com.anime.aniwatch.network.EpisodeResponse
import com.anime.aniwatch.util.Constants
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EpisodeFragment : Fragment() {

    private lateinit var episodeAdapter: EpisodeAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_episode, container, false)
        recyclerView = view.findViewById(R.id.episodeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        episodeAdapter = EpisodeAdapter(emptyList())
        recyclerView.adapter = episodeAdapter

        val animeId = arguments?.getString("ANIME_ID") ?: ""
        fetchEpisodes(animeId)

        return view
    }

    private fun fetchEpisodes(animeId: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(OkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getAnimeEpisodes(animeId).enqueue(object : Callback<EpisodeResponse> {
            override fun onResponse(call: Call<EpisodeResponse>, response: Response<EpisodeResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val episodes = response.body()?.data?.episodes ?: emptyList()
                    episodeAdapter.updateEpisodes(episodes)
                } else {
                    Toast.makeText(context, "Failed to load episodes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<EpisodeResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}