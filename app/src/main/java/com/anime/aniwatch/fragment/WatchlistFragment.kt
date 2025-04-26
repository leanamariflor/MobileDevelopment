package com.anime.aniwatch.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.R
import com.anime.aniwatch.activities.PlayerActivity
import com.anime.aniwatch.adapter.WatchlistAdapter
import com.anime.aniwatch.data.WatchlistEpisode
import com.anime.aniwatch.util.WatchlistUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class WatchlistFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: TextView
    private lateinit var watchlistAdapter: WatchlistAdapter
    private val watchlistEpisodes = mutableListOf<WatchlistEpisode>()

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_watchlist, container, false)

        recyclerView = view.findViewById(R.id.watchlistRecyclerView)
        emptyView = view.findViewById(R.id.emptyWatchlistText)

        setupRecyclerView()

        return view
    }

    override fun onResume() {
        super.onResume()
        loadWatchlistEpisodes()
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(context)

        watchlistAdapter = WatchlistAdapter(
            watchlistEpisodes,
            onPlayClick = { episode ->
                // Play the episode
                val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                    putExtra("EPISODE_ID", episode.episodeId)
                    putExtra("ANIME_ID", episode.animeId)
                }
                startActivity(intent)
            },
            onRemoveClick = { episode ->
                // Remove from watchlist
                context?.let { ctx ->
                    WatchlistUtil.removeFromWatchlist(ctx, episode.animeId, episode.episodeId)
                    // Refresh the list
                    watchlistEpisodes.remove(episode)
                    watchlistAdapter.notifyDataSetChanged()
                    updateEmptyView()
                }
            }
        )

        recyclerView.adapter = watchlistAdapter
    }

    private fun loadWatchlistEpisodes() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            updateEmptyView()
            return
        }

        val watchlistRef = database.getReference("Watchlist").child(userId)

        watchlistRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                watchlistEpisodes.clear()

                for (episodeSnapshot in snapshot.children) {
                    val episode = episodeSnapshot.getValue(WatchlistEpisode::class.java)
                    if (episode != null) {
                        watchlistEpisodes.add(episode)
                    }
                }

                // Sort by date added (newest first)
                watchlistEpisodes.sortByDescending { it.dateAdded }

                watchlistAdapter.notifyDataSetChanged()
                updateEmptyView()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                updateEmptyView()
            }
        })
    }

    private fun updateEmptyView() {
        if (watchlistEpisodes.isEmpty()) {
            emptyView.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyView.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }
}
