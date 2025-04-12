package com.anime.aniwatch.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.R
import com.anime.aniwatch.models.Movie
import com.anime.aniwatch.databinding.SearchBinding
import com.anime.aniwatch.helpers.MovieAdapter

class SearchActivity : AppCompatActivity() {

    private lateinit var listOfMovies: MutableList<Movie>
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var binding: SearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageView = binding.backButton
        backButton.setOnClickListener { onBackPressed() }


        val listView: ListView = binding.list
        val searchView: SearchView = binding.search

        listOfMovies = mutableListOf(
            Movie(1, "One Piece", R.drawable.onepiece, "One Piece is a popular anime about pirates."),
            Movie(2, "Naruto", R.drawable.naruto, "Naruto follows the journey of a young ninja."),
            Movie(3, "Bleach", R.drawable.bleach, "Bleach is about a boy who becomes a Soul Reaper."),
            Movie(4, "Fairy Tail", R.drawable.fairytail, "Fairy Tail is about a powerful wizard guild."),
            Movie(5, "Black Clover", R.drawable.blackclover, "Asta, a boy with no magic, aims to be Wizard King."),
        )

        movieAdapter = MovieAdapter(
            this,
            listOfMovies,
            onClickItem = { movie ->
                val intent = Intent(this, MovieDetailActivity::class.java).apply {
                    putExtra("rank", movie.rank)
                    putExtra("name", movie.name)
                    putExtra("imageResId", movie.imageResId)
                    putExtra("description", movie.description)
                }
                startActivity(intent)
            }
        )

        listView.adapter = movieAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    movieAdapter.updateList(listOfMovies)
                } else {
                    val filteredList = listOfMovies.filter { movie ->
                        movie.name.contains(newText, ignoreCase = true)
                    }.toMutableList()

                    movieAdapter.updateList(filteredList)
                }
                return true
            }

        })
    }
}
