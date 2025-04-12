package com.anime.aniwatch.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anime.aniwatch.data.Anime

class HomeViewModel : ViewModel() {
    val dayAnimeList = MutableLiveData<List<Anime>>()
    val weekAnimeList = MutableLiveData<List<Anime>>()
    val monthAnimeList = MutableLiveData<List<Anime>>()
}