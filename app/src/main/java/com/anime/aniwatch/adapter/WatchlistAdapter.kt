package com.anime.aniwatch.adapter

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.R
import com.anime.aniwatch.data.WatchlistEpisode
import com.bumptech.glide.Glide

class WatchlistAdapter(
    private var episodes: List<WatchlistEpisode>,
    private val onPlayClick: (WatchlistEpisode) -> Unit,
    private val onRemoveClick: (WatchlistEpisode) -> Unit,
    private val onReminderClick: (WatchlistEpisode) -> Unit
) : RecyclerView.Adapter<WatchlistAdapter.WatchlistViewHolder>() {

    class WatchlistViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animePoster: ImageView = view.findViewById(R.id.animePoster)
        val animeTitle: TextView = view.findViewById(R.id.animeTitle)
        val episodeInfo: TextView = view.findViewById(R.id.episodeInfo)
        val playButton: Button = view.findViewById(R.id.playButton)
        val removeButton: Button = view.findViewById(R.id.removeButton)
        val reminderButton: ImageView = view.findViewById(R.id.reminderButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WatchlistViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watchlist, parent, false)
        return WatchlistViewHolder(view)
    }

    override fun onBindViewHolder(holder: WatchlistViewHolder, position: Int) {
        val episode = episodes[position]

        Glide.with(holder.itemView.context)
            .load(episode.animePosterUrl)
            .placeholder(ColorDrawable(Color.GRAY))
            .into(holder.animePoster)

        holder.animeTitle.text = episode.animeTitle
        holder.episodeInfo.text = "Episode ${episode.episodeNumber}: ${episode.episodeTitle}"

        // Update reminder button appearance based on whether a reminder is set
        if (episode.hasReminder) {
            holder.reminderButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_orange_light))
        } else {
            holder.reminderButton.setColorFilter(Color.WHITE)
        }

        holder.playButton.setOnClickListener {
            onPlayClick(episode)
        }

        holder.removeButton.setOnClickListener {
            onRemoveClick(episode)
        }

        holder.reminderButton.setOnClickListener {
            onReminderClick(episode)
        }
    }

    override fun getItemCount(): Int = episodes.size

    fun updateEpisodes(newEpisodes: List<WatchlistEpisode>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }
}
