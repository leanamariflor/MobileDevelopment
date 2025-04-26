package com.anime.aniwatch.util

import android.content.Context
import android.widget.Toast
import com.anime.aniwatch.data.WatchHistory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

    fun isInHistory(animeId: String, episodeId: String, callback: (Boolean) -> Unit) {
        val auth = FirebaseAuth.getInstance()
        val database: FirebaseDatabase = FirebaseDatabase.getInstance()
        val userId = auth.currentUser?.uid

        if (userId == null) {
            callback(false)
            return
        }

        val uniqueKey = "$animeId-$episodeId"
        val historyRef = database.getReference("WatchHistory").child(userId).child(uniqueKey)

        historyRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists())
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
    }
}
