package com.anime.aniwatch.util

import android.content.Context
import android.widget.Toast
import com.anime.aniwatch.data.WatchHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object WatchHistoryUtil {

    fun saveWatchHistory(context: Context, watchHistory: WatchHistory) {
        val auth = FirebaseAuth.getInstance()
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid ?: return
        val historyRef: DatabaseReference = database.getReference("WatchHistory").child(userId)

        val uniqueKey = "${watchHistory.animeId}-${watchHistory.episodeId}"

        historyRef.child(uniqueKey).setValue(watchHistory).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(context, "Watch history saved", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Failed to save watch history", Toast.LENGTH_SHORT).show()
            }
        }
    }
}