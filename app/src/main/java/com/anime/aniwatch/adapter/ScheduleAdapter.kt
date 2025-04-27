package com.anime.aniwatch.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.anime.aniwatch.R
import com.anime.aniwatch.data.ScheduledAnime

class ScheduleAdapter(
    private val context: Context,
    private val scheduleList: List<ScheduledAnime>,
    private val onItemClick: (ScheduledAnime) -> Unit
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(
            R.layout.item_schedule,
            parent,
            false
        )

        val timeTextView = view.findViewById<TextView>(R.id.textview_time)
        val nameTextView = view.findViewById<TextView>(R.id.textview_name)
        val jnameTextView = view.findViewById<TextView>(R.id.textview_jname)
        val episodeTextView = view.findViewById<TextView>(R.id.textview_episode)

        val anime = scheduleList[position]

        timeTextView.text = anime.time
        nameTextView.text = anime.name
        jnameTextView.text = anime.jname
        episodeTextView.text = "Episode ${anime.episode}"

        view.setOnClickListener {
            onItemClick(anime)
        }

        return view
    }

    override fun getCount(): Int = scheduleList.size

    override fun getItem(position: Int): Any = scheduleList[position]

    override fun getItemId(position: Int): Long = position.toLong()
}