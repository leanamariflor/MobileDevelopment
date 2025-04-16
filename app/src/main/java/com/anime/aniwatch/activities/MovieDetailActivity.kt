package com.anime.aniwatch.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.anime.aniwatch.R

class MovieDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val movieName = intent.getStringExtra("name") ?: "Unknown"
        val movieImageResId = intent.getIntExtra("imageResId", R.drawable.onepiece)
        val movieDescription = intent.getStringExtra("description") ?: "No description available"

        findViewById<TextView>(R.id.detailName).text = movieName
        findViewById<ImageView>(R.id.detailImage).setImageResource(movieImageResId)
        findViewById<TextView>(R.id.detailDesc).text = movieDescription
    }
}
