package com.anime.aniwatch.fragment

import android.app.AlarmManager
import android.app.DatePickerDialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anime.aniwatch.R
import com.anime.aniwatch.activities.PlayerActivity
import com.anime.aniwatch.adapter.WatchlistAdapter
import com.anime.aniwatch.data.WatchlistEpisode
import com.anime.aniwatch.receiver.AlarmReceiver
import com.anime.aniwatch.util.WatchlistUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar

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

                val intent = Intent(requireContext(), PlayerActivity::class.java).apply {
                    putExtra("EPISODE_ID", episode.episodeId)
                    putExtra("ANIME_ID", episode.animeId)
                }
                startActivity(intent)
            },
            onRemoveClick = { episode ->

                context?.let { ctx ->
                    WatchlistUtil.removeFromWatchlist(ctx, "", episode.episodeId)

                    watchlistEpisodes.remove(episode)
                    watchlistAdapter.updateEpisodes(watchlistEpisodes)
                    updateEmptyView()
                }
            },
            onReminderClick = { episode ->

                showReminderDateTimePicker(episode)
            }
        )

        recyclerView.adapter = watchlistAdapter
    }

    private fun showReminderDateTimePicker(episode: WatchlistEpisode) {
        context?.let { ctx ->

            if (episode.hasReminder) {
                val options = arrayOf("Change reminder", "Remove reminder", "Cancel")
                androidx.appcompat.app.AlertDialog.Builder(ctx)
                    .setTitle("Reminder options")
                    .setItems(options) { dialog, which ->
                        when (which) {
                            0 -> showDatePicker(ctx, episode)
                            1 -> cancelReminder(episode)
                            2 -> dialog.dismiss()
                        }
                    }
                    .show()
            } else {
                showDatePicker(ctx, episode)
            }
        }
    }

    private fun showDatePicker(context: Context, episode: WatchlistEpisode) {
        val calendar = Calendar.getInstance()

        if (episode.hasReminder && episode.reminderDate > 0) {
            calendar.timeInMillis = episode.reminderDate
        }

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(context, { _, selectedYear, selectedMonth, selectedDay ->
            calendar.set(Calendar.YEAR, selectedYear)
            calendar.set(Calendar.MONTH, selectedMonth)
            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

            showTimePicker(context, calendar, episode)
        }, year, month, day).show()
    }

    private fun showTimePicker(context: Context, calendar: Calendar, episode: WatchlistEpisode) {
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour)
            calendar.set(Calendar.MINUTE, selectedMinute)
            calendar.set(Calendar.SECOND, 0)

            scheduleReminder(context, calendar.timeInMillis, episode)
        }, hour, minute, true).show()
    }

    private fun scheduleReminder(context: Context, reminderTime: Long, episode: WatchlistEpisode) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_ANIME_TITLE, episode.animeTitle)
            putExtra(AlarmReceiver.EXTRA_EPISODE_NUMBER, episode.episodeNumber)
            putExtra(AlarmReceiver.EXTRA_EPISODE_TITLE, episode.episodeTitle)
        }

        val requestCode = episode.episodeId.hashCode()

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderTime, pendingIntent)
        }

        episode.reminderDate = reminderTime
        episode.hasReminder = true

        saveReminderToFirebase(episode)

        val calendar = Calendar.getInstance().apply { timeInMillis = reminderTime }
        val dateTime = android.text.format.DateFormat.format("MMM dd, yyyy HH:mm", calendar).toString()
        Toast.makeText(context, "Reminder set for $dateTime", Toast.LENGTH_LONG).show()

        watchlistAdapter.notifyDataSetChanged()
    }

    private fun cancelReminder(episode: WatchlistEpisode) {
        context?.let { ctx ->
            val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(ctx, AlarmReceiver::class.java)
            val requestCode = episode.episodeId.hashCode()

            val pendingIntent = PendingIntent.getBroadcast(
                ctx,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.cancel(pendingIntent)

            episode.reminderDate = 0
            episode.hasReminder = false

            saveReminderToFirebase(episode)

            Toast.makeText(ctx, "Reminder removed", Toast.LENGTH_SHORT).show()

            watchlistAdapter.notifyDataSetChanged()
        }
    }

    private fun saveReminderToFirebase(episode: WatchlistEpisode) {
        val userId = auth.currentUser?.uid ?: return
        val episodeRef = database.getReference("Watchlist").child(userId).child(episode.episodeId)
        episodeRef.setValue(episode)
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
