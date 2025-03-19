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

        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)

        binding. loginRedirectText.setOnClickListener {
            Toast.makeText(this, "Navigating to Sign In", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
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
                        editor.apply() // Save the data

                        Toast.makeText(this, "Account created successfully.", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this, SignIn::class.java)
                        startActivity(intent)
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
