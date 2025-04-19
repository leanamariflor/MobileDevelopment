package com.anime.aniwatch.activities


import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.anime.aniwatch.R
import com.google.firebase.messaging.FirebaseMessaging

class NotificationsActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var notificationSwitch: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notifications)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        notificationSwitch = findViewById(R.id.notif_switch)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle toolbar navigation
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)

        // Set the initial state of the switch
        val notificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        notificationSwitch.isChecked = notificationsEnabled

        // Set up the Switch to toggle notifications
        notificationSwitch.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            try {
                if (isChecked) {
                    enableNotifications() // Enable notifications
                    Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
                } else {
                    disableNotifications() // Disable notifications
                    Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
                }
                // Save the user's preference
                sharedPreferences.edit().putBoolean("notifications_enabled", isChecked).apply()
            } catch (e: Exception) {
                e.printStackTrace() // Log the exception to the console
                Toast.makeText(this, "Error handling switch action", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun enableNotifications() {
        FirebaseMessaging.getInstance().subscribeToTopic("notifications")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Successfully subscribed to notifications")
                    Toast.makeText(this, "Successfully subscribed to notifications", Toast.LENGTH_SHORT).show()
                } else {
                    println("Failed to subscribe to notifications: ${task.exception?.message}")
                    Toast.makeText(this, "Failed to subscribe to notifications", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun disableNotifications() {
        FirebaseMessaging.getInstance().unsubscribeFromTopic("notifications")
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    println("Successfully unsubscribed from notifications")
                    Toast.makeText(this, "Successfully unsubscribed from notifications", Toast.LENGTH_SHORT).show()
                } else {
                    println("Failed to unsubscribe from notifications: ${task.exception?.message}")
                    Toast.makeText(this, "Failed to unsubscribe from notifications", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
