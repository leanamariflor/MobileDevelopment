package com.anime.aniwatch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.R
import com.anime.aniwatch.data.WatchHistory
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class HistoryAdapter(private val historyList: List<WatchHistory>) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val animePoster: ImageView = view.findViewById(R.id.animePoster)
        val animeTitle: TextView = view.findViewById(R.id.animeTitle)
        val episodeNumber: TextView = view.findViewById(R.id.episodeNumber)
        val episodeTitle: TextView = view.findViewById(R.id.episodeTitle)
        val dateWatched: TextView = view.findViewById(R.id.dateWatched)
        val watchedTime: TextView = view.findViewById(R.id.watchedTime)
        val watchProgress: LinearProgressIndicator = view.findViewById(R.id.watchProgress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_watch_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.animeTitle.text = history.animeTitle
        holder.episodeNumber.text = "Episode ${history.episodeNumber}:"
        holder.episodeTitle.text = history.episodeTitle

        val dateWatched = formatDateWatched(history.dateWatched)
        holder.dateWatched.text = "Last watched: $dateWatched"
        holder.watchedTime.text = when {
            history.watchedTime >= 60000 -> {
                val totalSeconds = history.watchedTime / 1000
                val minutes = totalSeconds / 60
                val seconds = totalSeconds % 60
                "Progress: $minutes min $seconds sec"
            }
            else -> "Progress: ${history.watchedTime / 1000} sec"
        }
        val progress = if (history.totalTime > 0) {
            ((history.totalTime - history.watchedTime.toDouble()) / history.totalTime * 100).toInt()
        } else {
            0
        }
        holder.watchProgress.progress = 100 - progress // Invert to show watched progress instead of remaining

        Glide.with(holder.itemView.context)
            .load(history.animePosterUrl)
            .into(holder.animePoster)
    }

    private fun formatDateWatched(dateString: String): String {
        try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = inputFormat.parse(dateString) ?: return dateString

            val calendar = Calendar.getInstance()
            val today = calendar.time
            calendar.add(Calendar.DAY_OF_YEAR, -1)
            val yesterday = calendar.time

            val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())

            return when {
                isSameDay(date, today) -> "Today at ${timeFormat.format(date)}"
                isSameDay(date, yesterday) -> "Yesterday at ${timeFormat.format(date)}"
                else -> dateFormat.format(date)
            }
        } catch (e: Exception) {
            return dateString
        }
    } fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    override fun getItemCount(): Int = historyList.size
}