package com.anime.aniwatch.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.BuildConfig
import com.anime.aniwatch.databinding.SplashBinding
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: SplashBinding
    private val versionUrl = "https://jonvicbarcenas.github.io/MobileDevelopment/version.json"
    private var latestVersion: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkForUpdate()
    }

    private fun checkForUpdate() {
        val handler = Handler(Looper.getMainLooper())
        val timeoutRunnable = Runnable {
            Toast.makeText(this, "Request timed out. Proceeding to the next screen.", Toast.LENGTH_SHORT).show()
            proceedToNextScreen()
        }

        handler.postDelayed(timeoutRunnable, 5000)

        Thread {
            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                    .readTimeout(5, java.util.concurrent.TimeUnit.SECONDS)
                    .build()
                val request = Request.Builder().url(versionUrl).build()
                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val responseBody = response.body?.string()
                    val jsonObject = JSONObject(responseBody ?: "")
                    latestVersion = jsonObject.getString("version").removePrefix("v")

                    runOnUiThread {
                        handler.removeCallbacks(timeoutRunnable)
                        if (latestVersion != BuildConfig.VERSION_NAME) {
                            Toast.makeText(
                                this,
                                "Update available: $latestVersion" +
                                        "\nCurrent version: ${BuildConfig.VERSION_NAME}",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            proceedToNextScreen()
                        }
                    }
                } else {
                    runOnUiThread {
                        handler.removeCallbacks(timeoutRunnable)
                        proceedToNextScreen()
                    }
                }
            } catch (e: IOException) {
                runOnUiThread {
                    handler.removeCallbacks(timeoutRunnable)
                    Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
                    proceedToNextScreen()
                }
            }
        }.start()
    }


    private fun proceedToNextScreen() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, 1500)
    }
}