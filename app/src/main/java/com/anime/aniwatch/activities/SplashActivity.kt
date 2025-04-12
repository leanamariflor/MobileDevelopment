package com.anime.aniwatch.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.databinding.SplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: SplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.start.setOnClickListener {
            Toast.makeText(this, "Navigating to SignIn", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)

        }
    }
}
