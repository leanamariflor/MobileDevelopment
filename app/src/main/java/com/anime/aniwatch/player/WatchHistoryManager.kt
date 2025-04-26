package com.anime.aniwatch.player

import android.content.Context
import android.widget.Toast
import com.anime.aniwatch.data.WatchHistory
import com.anime.aniwatch.util.WatchHistoryUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WatchHistoryManager(private val context: Context) {
    
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    fun saveWatchHistory(
        episodeId: String,
        animeId: String,
        animeTitle: String,
        episodeTitle: String,
        episodeNumber: Int,
        watchedTime: Long,
        totalTime: Long
    ) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val watchHistory = WatchHistory(
            animeId = animeId,
            animeTitle = animeTitle,
            episodeId = episodeId,
            episodeTitle = episodeTitle,
            episodeNumber = episodeNumber,
            watchedTime = watchedTime,
            totalTime = totalTime,
            dateWatched = currentDate
        )
        WatchHistoryUtil.saveWatchHistory(context, watchHistory)
    }
    
    fun getWatchedTime(episodeId: String, animeId: String, callback: (Long) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            callback(0)
            return
        }
        
        val historyRef = database.getReference("WatchHistory").child(userId)
        val uniqueKey = "$animeId-$episodeId"
        
        historyRef.child(uniqueKey).get().addOnSuccessListener { snapshot ->
            val savedWatchedTime = snapshot.child("watchedTime").getValue(Long::class.java) ?: 0L
            callback(savedWatchedTime)
        }.addOnFailureListener {
            Toast.makeText(context, "Failed to retrieve watch history", Toast.LENGTH_SHORT).show()
            callback(0)
        }
    }
}