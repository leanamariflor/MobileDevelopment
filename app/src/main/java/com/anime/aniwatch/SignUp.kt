package com.anime.aniwatch

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.databinding.ActivitySignUpBinding
import java.util.regex.Pattern

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        binding.loginRedirectText.setOnClickListener {
            Toast.makeText(this, "Navigating to Sign In", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignIn::class.java))
        }

        binding.signupButton.setOnClickListener {
            val email = binding.signupEmail.text.toString()
            val password = binding.signupPassword.text.toString()
            val confirmPassword = binding.signupConfirm.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (isValidEmail(email)) {
                    if (password == confirmPassword) {
                        val editor = sharedPreferences.edit()
                        editor.putString("email", email)
                        editor.putString("password", password)
                        editor.apply()

                        Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, SignIn::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Password does not match.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Invalid email format.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
        return Pattern.compile(emailPattern).matcher(email).matches()
    }
}
