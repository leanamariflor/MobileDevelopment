package com.anime.aniwatch

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SecurityActivity : AppCompatActivity() {

    private lateinit var rememberSwitch: Switch
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        // Set up the toolbar with a back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        rememberSwitch = findViewById(R.id.remember_switch)
        sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Get saved "Remember Me" state from SharedPreferences and set the Switch
        val isRememberMe = sharedPrefs.getBoolean("rememberMe", false)
        rememberSwitch.isChecked = isRememberMe

        // Save the state when the switch is toggled
        rememberSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPrefs.edit()
            editor.putBoolean("rememberMe", isChecked)
            editor.apply()

            // If switched off, clear the stored credentials
            if (!isChecked) {
                clearLoginCredentials()
            }
        }
    }

    private fun clearLoginCredentials() {
        val editor = sharedPrefs.edit()
        editor.remove("email")
        editor.remove("password")
        editor.apply()
    }
}
