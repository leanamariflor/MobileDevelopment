package com.anime.aniwatch.helper

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.data.Movie
import com.anime.aniwatch.MovieDetailActivity
import com.anime.aniwatch.R

class MovieAdapter(private var movieList: List<Movie>, private val context: Context) :
    RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    private var originalList = movieList.toMutableList()

    inner class MovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val rankView: TextView = itemView.findViewById(R.id.rankNumber)
        val imageView: ImageView = itemView.findViewById(R.id.movieImage)
        val titleView: TextView = itemView.findViewById(R.id.movieTitle)
        val descriptionView: TextView = itemView.findViewById(R.id.movieDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_movie, parent, false)
        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = movieList[position]

        holder.titleView.text = movie.name
        holder.descriptionView.text = movie.description
        holder.imageView.setImageResource(movie.imageResId)
        holder.rankView.text = (position + 1).toString()

        holder.itemView.setOnClickListener {
            val intent = Intent(context, MovieDetailActivity::class.java)
            intent.putExtra("movie", movie)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = movieList.size

    fun filter(query: String) {
        movieList = if (query.isEmpty()) {
            originalList
        } else {
            originalList.filter {
                it.name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }
}
