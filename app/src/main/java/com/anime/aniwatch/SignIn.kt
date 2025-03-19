package com.anime.aniwatch

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.databinding.ActivitySignInBinding

class SignIn : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var rememberCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        rememberCheckBox = binding.rememberMeCheckbox

        checkRememberMe()

        binding.signinButton.setOnClickListener {
            val email = binding.signinEmail.text.toString()
            val password = binding.signinPassword.text.toString()

            val storedEmail = sharedPrefs.getString("email", "")
            val storedPassword = sharedPrefs.getString("password", "")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (email == storedEmail && password == storedPassword) {
                    saveLoginCredentials(email, password, rememberCheckBox.isChecked)

                    Toast.makeText(this, "Navigating to Home", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupRedirectText.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
    }

    private fun saveLoginCredentials(email: String, password: String, rememberMe: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean("rememberMe", rememberMe)

        if (rememberMe) {
            editor.putString("email", email)
            editor.putString("password", password)
        } else {
            editor.remove("email")
            editor.remove("password")
        }

        editor.apply()
    }

    private fun checkRememberMe() {
        val rememberMe = sharedPrefs.getBoolean("rememberMe", false)
        if (rememberMe) {
            binding.signinEmail.setText(sharedPrefs.getString("email", ""))
            binding.signinPassword.setText(sharedPrefs.getString("password", ""))
            rememberCheckBox.isChecked = true
        }
    }
}
