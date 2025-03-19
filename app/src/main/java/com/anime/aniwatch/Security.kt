package com.anime.aniwatch

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class SecurityActivity : AppCompatActivity() {

    private lateinit var rememberSwitch: Switch
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var changePassword: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security)

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        rememberSwitch = findViewById(R.id.remember_switch)
        changePassword = findViewById(R.id.change_pass)

        checkRememberMe()

        rememberSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveRememberMeState(isChecked)
        }

        changePassword.setOnClickListener {
            startActivity(Intent(this, ChangePassword::class.java))
        }
    }

    private fun saveRememberMeState(isChecked: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean("rememberMe", isChecked)
        editor.apply()

        if (!isChecked) {
            clearLoginCredentials()
        }
    }

    private fun checkRememberMe() {
        val isRememberMe = sharedPrefs.getBoolean("rememberMe", false)
        rememberSwitch.isChecked = isRememberMe
    }

    private fun clearLoginCredentials() {
        val editor = sharedPrefs.edit()
        editor.remove("email")
        editor.remove("password")
        editor.apply()
    }
}
