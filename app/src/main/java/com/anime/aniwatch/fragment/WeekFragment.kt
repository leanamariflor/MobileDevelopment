package com.anime.aniwatch.fragment

import AnimeAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.R
import com.anime.aniwatch.network.ApiService
import com.anime.aniwatch.network.HomeResponse
import com.anime.aniwatch.util.Constants
import com.anime.aniwatch.viewmodel.HomeViewModel
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeekFragment : Fragment() {

    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var animeAdapter: AnimeAdapter
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_day, container, false)
        shimmerLayout = view.findViewById(R.id.shimmerLayout)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        homeViewModel.weekAnimeList.observe(viewLifecycleOwner) { animeList ->
            if (animeList.isNullOrEmpty()) {
                shimmerLayout.startShimmer()
                shimmerLayout.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                shimmerLayout.stopShimmer()
                shimmerLayout.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                animeAdapter = AnimeAdapter(animeList)
                recyclerView.adapter = animeAdapter
            }
        }

        // Set GridLayoutManager with 2 columns
        recyclerView.layoutManager = GridLayoutManager(context, 2)

        if (homeViewModel.weekAnimeList.value == null) {
            fetchAnimeData()
        }

        return view
    }

    private fun fetchAnimeData() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(ApiService::class.java)
        apiService.getHomeData().enqueue(object : Callback<HomeResponse> {
            override fun onResponse(call: Call<HomeResponse>, response: Response<HomeResponse>) {
                if (response.isSuccessful) {
                    val animeList = response.body()?.data?.top10Animes?.week ?: emptyList()
                    homeViewModel.weekAnimeList.postValue(animeList)
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
}