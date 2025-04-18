package com.anime.aniwatch.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.anime.aniwatch.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.anime.aniwatch.R
import com.anime.aniwatch.activities.MainActivity
import com.anime.aniwatch.activities.SignUpActivity

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPrefs: SharedPreferences
    private lateinit var rememberCheckBox: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences instance
        sharedPrefs = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        rememberCheckBox = findViewById(R.id.rememberMeCheckbox)

        checkRememberMe()




        binding.signinButton.setOnClickListener {
            val email = binding.signinEmail.text.toString()
            val password = binding.signinPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Save login credentials and "Remember Me" state
                            saveLoginCredentials(email, password, rememberCheckBox.isChecked)

                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val exception = task.exception?.message
                            Toast.makeText(this, "Error: $exception", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please enter a valid email and password.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.forgotPassword.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val view = layoutInflater.inflate(R.layout.forgot, null)
            val userEmail = view.findViewById<EditText>(R.id.editBox)

            builder.setView(view)
            val dialog = builder.create()

            // Handle Reset Button Click
            view.findViewById<Button>(R.id.btnReset).setOnClickListener {
                val email = userEmail.text.toString()
                if (email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(this, "Check your email", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    dialog.dismiss()
                } else {
                    Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                }
            }

            // Handle Cancel Button Click
            view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }

            // Show the dialog with a transparent background
            if (dialog.window != null) {
                dialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }

            dialog.show()
        }

        binding.signupRedirectText.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    // Save login credentials and "Remember Me" state in SharedPreferences
    private fun saveLoginCredentials(email: String, password: String, rememberMe: Boolean) {
        val editor = sharedPrefs.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("email", email)
        editor.putString("password", password)
        editor.putBoolean("rememberMe", rememberMe) // Save "Remember Me" state
        editor.apply()
    }

    // Check if "Remember Me" is active from previous session
    private fun checkRememberMe() {
        val isLoggedIn = sharedPrefs.getBoolean("isLoggedIn", false)
        val rememberMe = sharedPrefs.getBoolean("rememberMe", false)

        if (isLoggedIn && rememberMe) {
            val email = sharedPrefs.getString("email", null)
            val password = sharedPrefs.getString("password", null)

            // Pre-fill the email and password fields
            if (email != null && password != null) {
                binding.signinEmail.setText(email)
                binding.signinPassword.setText(password)
                rememberCheckBox.isChecked = true // Make sure the checkbox is checked
            }
        }
    }
}