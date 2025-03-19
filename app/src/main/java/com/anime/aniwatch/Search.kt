package com.anime.aniwatch

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.data.Movie
import com.anime.aniwatch.data.MovieData
import com.anime.aniwatch.databinding.SearchBinding

class SearchActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var listView: ListView
    private lateinit var movieList: MutableList<Movie>
    private lateinit var movieAdapter: ArrayAdapter<Movie>
    lateinit var binding: SearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val backButton: ImageView = binding.backButton
        backButton.setOnClickListener {
            onBackPressed()
        }

        searchView = binding.search
        listView = binding.list

        val movieTitles = resources.getStringArray(R.array.movie_titles)
        val movieDescriptions = resources.getStringArray(R.array.movie_descriptions)
        val movieImages = MovieData.movieImages

        val movieList = mutableListOf<Movie>()
        for (i in movieTitles.indices) {
            movieList.add(
                Movie(
                    rank = i + 1,
                    name = movieTitles[i],
                    imageResId = movieImages[i],
                    description = movieDescriptions[i]
                )
            )
        }

        movieAdapter = object : ArrayAdapter<Movie>(this, android.R.layout.simple_list_item_1, movieList) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_search, parent, false)

                val movie = getItem(position)

                val movieName = view.findViewById<TextView>(R.id.movieTitle)
                val movieImage = view.findViewById<ImageView>(R.id.movieImage)

                movieName.text = movie?.name
                movieImage.setImageResource(movie?.imageResId ?: R.drawable.logo_no_bg)

                return view
            }
        }

        listView.adapter = movieAdapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedMovie = movieList[position]
            val intent = Intent(this, MovieDetailActivity::class.java)
            intent.putExtra("movie", selectedMovie)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    val filteredList = movieList.filter { movie ->
                        movie.name.contains(it, ignoreCase = true)
                    }
                    movieAdapter.clear()
                    movieAdapter.addAll(filteredList)
                    movieAdapter.notifyDataSetChanged()
                }
                return true
            }
        })
    }
}
