package com.anime.aniwatch.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.anime.aniwatch.R
import com.anime.aniwatch.data.Movie

class MovieAdapter(
    val context: Context,
    private var movieList: MutableList<Movie>,
    val onClickItem: (Movie) -> Unit
) : BaseAdapter() {

    override fun getView(position: Int, contentView: View?, parent: ViewGroup?): View {
        val view = contentView ?: LayoutInflater.from(context).inflate(
            R.layout.item_search, parent, false
        )

        val movie = movieList[position]

        val movieName = view.findViewById<TextView>(R.id.movieTitle)
        val movieImage = view.findViewById<ImageView>(R.id.movieImage)

        movieName.text = movie.name
        movieImage.setImageResource(movie.imageResId)

        view.setOnClickListener {
            onClickItem(movie)
        }

        return view
    }

    override fun getCount(): Int = movieList.size
    override fun getItem(position: Int): Any = movieList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    fun updateList(newList: List<Movie>) {
        movieList.clear()
        movieList.addAll(newList)
        notifyDataSetChanged()
    }
}
