package com.anime.aniwatch.data

import com.anime.aniwatch.R

data class Movie(
    var rank: Int = 0,
    var name: String = "",
    var imageResId: Int = R.drawable.onepiece,
    var description: String = ""
)
