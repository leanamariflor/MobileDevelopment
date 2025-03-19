package com.anime.aniwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.anime.aniwatch.databinding.ActivitySignInBinding

class SignIn : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)



        binding.signinButton.setOnClickListener {
            val email = binding.signinEmail.text.toString()
            val password = binding.signinPassword.text.toString()

            val storedEmail = sharedPreferences.getString("email", "")
            val storedPassword = sharedPreferences.getString("password", "")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Check if the entered credentials match the stored ones
                if (email == storedEmail && password == storedPassword) {
                    // Successful login
                    Toast.makeText(this, "Navigating to Home", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please enter a valid email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupRedirectText.setOnClickListener {
            // Redirect to SignUp activity
            Toast.makeText(this, "Navigating to SignUp", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }
    }
}
