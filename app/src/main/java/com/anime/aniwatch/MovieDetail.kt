package com.anime.aniwatch

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.anime.aniwatch.data.Movie

class MovieDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        // Set up the toolbar with a back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)



        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val movie: Movie? = intent.getParcelableExtra("movie")

        if (movie != null) {
            findViewById<TextView>(R.id.detailName).text = movie.name
            findViewById<ImageView>(R.id.detailImage).setImageResource(movie.imageResId)
            findViewById<TextView>(R.id.detailDesc).text = movie.description
        }

    }
}
