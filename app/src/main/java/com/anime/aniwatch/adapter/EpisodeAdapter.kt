package com.anime.aniwatch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.R
import com.anime.aniwatch.network.Episode

class EpisodeAdapter(private var episodes: List<Episode>) :
    RecyclerView.Adapter<EpisodeAdapter.EpisodeViewHolder>() {

    class EpisodeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val episodeNumber: TextView = view.findViewById(R.id.episodeNumber)
        val episodeTitle: TextView = view.findViewById(R.id.episodeTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EpisodeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_episode, parent, false)
        return EpisodeViewHolder(view)
    }

    override fun onBindViewHolder(holder: EpisodeViewHolder, position: Int) {
        val episode = episodes[position]
        holder.episodeNumber.text = "Episode ${episode.number}"
        holder.episodeTitle.text = episode.title
    }

    override fun getItemCount(): Int = episodes.size

    fun updateEpisodes(newEpisodes: List<Episode>) {
        episodes = newEpisodes
        notifyDataSetChanged()
    }
}