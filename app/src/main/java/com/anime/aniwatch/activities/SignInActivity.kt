package com.anime.aniwatch.activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import java.util.concurrent.TimeUnit

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Initialize the checkbox state based on SharedPreferences
        val rememberMe = sharedPreferences.getBoolean("rememberMe", false)
        binding.rememberMeCheckbox.isChecked = rememberMe

        // Check if session is still valid
        val sessionExpiration = sharedPreferences.getLong("sessionExpiration", 0L)
        val currentTime = System.currentTimeMillis()
        val isSessionValid = currentTime < sessionExpiration

        // Set listener for the checkbox
        binding.rememberMeCheckbox.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean("rememberMe", isChecked)
            editor.apply()
        }

        if (rememberMe) {
            // Autofill email and password if "Remember Me" is true
            val savedEmail = sharedPreferences.getString("email", "") ?: ""
            val savedPassword = sharedPreferences.getString("password", "") ?: ""
            binding.signinEmail.setText(savedEmail)
            binding.signinPassword.setText(savedPassword)

            // Auto sign in only if session is valid
            if (isSessionValid && savedEmail.isNotEmpty() && savedPassword.isNotEmpty()) {
                verifyUserExistsInFirebase(savedEmail, savedPassword)
            }
        } else {
            // Clear fields if "Remember Me" is false
            binding.signinEmail.setText("")
            binding.signinPassword.setText("")
        }

        binding.signinButton.setOnClickListener {
            val email = binding.signinEmail.text.toString()
            val password = binding.signinPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val editor = sharedPreferences.edit()

                            // Calculate expiration time (30 seconds from now)
                            val expirationTime = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7)
                            editor.putLong("sessionExpiration", expirationTime)

                            if (binding.rememberMeCheckbox.isChecked) {
                                editor.putString("email", email)
                                editor.putString("password", password)
                                editor.putBoolean("rememberMe", true)
                            } else {
                                editor.remove("email")
                                editor.remove("password")
                                editor.putBoolean("rememberMe", false)
                            }

                            editor.apply()
                            navigateToMainActivity()
                        } else {
                            // Check specifically for deleted account error
                            if (task.exception is FirebaseAuthInvalidUserException) {
                                val errorCode = (task.exception as FirebaseAuthInvalidUserException).errorCode
                                if (errorCode == "ERROR_USER_NOT_FOUND") {
                                    Toast.makeText(this, "Account does not exist or has been deleted.", Toast.LENGTH_SHORT).show()
                                    clearSessionData()
                                } else {
                                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter a valid email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signupRedirectText.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.forgotPassword.setOnClickListener {
            val email = binding.signinEmail.text.toString()
            if (email.isNotEmpty()) {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Password reset email sent.", Toast.LENGTH_SHORT).show()
                        } else {
                            if (task.exception is FirebaseAuthInvalidUserException) {
                                Toast.makeText(this, "No account found with this email.", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter your email to reset password.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verifyUserExistsInFirebase(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update session expiration time
                    val editor = sharedPreferences.edit()
                    val expirationTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30)
                    editor.putLong("sessionExpiration", expirationTime)
                    editor.apply()

                    navigateToMainActivity()
                } else {
                    if (task.exception is FirebaseAuthInvalidUserException) {
                        val errorCode = (task.exception as FirebaseAuthInvalidUserException).errorCode
                        if (errorCode == "ERROR_USER_NOT_FOUND") {
                            Toast.makeText(this, "Your account has been deleted. Please sign up again.", Toast.LENGTH_SHORT).show()
                            clearSessionData()
                        } else {
                            clearSessionData()
                            Toast.makeText(this, "Session expired. Please sign in again.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        clearSessionData()
                        Toast.makeText(this, "Session expired. Please sign in again.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun clearSessionData() {
        val editor = sharedPreferences.edit()
        editor.remove("sessionExpiration")
        // Don't remove email/password if remember me is checked
        // Just invalidate the session
        editor.apply()
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}