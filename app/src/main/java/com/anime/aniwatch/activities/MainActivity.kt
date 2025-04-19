package com.anime.aniwatch.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.anime.aniwatch.*
import com.anime.aniwatch.databinding.ActivityMainBinding
import com.anime.aniwatch.fragment.AccountFragment
import com.anime.aniwatch.fragment.HomeFragment
import com.anime.aniwatch.fragment.ListFragment
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import android.Manifest


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)
        supportActionBar?.title = null

        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }

        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val fragment = when (menuItem.itemId) {
                R.id.home -> HomeFragment()
                R.id.list -> ListFragment()
                R.id.account -> AccountFragment()
                else -> null
            }
            fragment?.let { replaceFragment(it) }
            fragment != null
        }

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@addOnCompleteListener
                }

                val token = task.result
                Log.d("FCM", "FCM Token: $token")

            }

        // Request notification permission if needed (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        if (fragment is HomeFragment) {
            fragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        when (fragment) {
            is HomeFragment -> {
                supportActionBar?.show()
                disableBackButton()
                supportActionBar?.setDisplayShowTitleEnabled(false)
                showSearchButton()
            }
            is ListFragment -> {
                supportActionBar?.show()
                supportActionBar?.setDisplayShowTitleEnabled(true)
                supportActionBar?.title = "My List"
                disableBackButton()
            }
            is AccountFragment -> {
                supportActionBar?.hide()
            }
        }
    }


    private fun disableBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    private fun showSearchButton() {
        val searchButton = binding.toolbar.menu.findItem(R.id.action_search)
        searchButton?.isVisible = true
    }

    override fun onCreateOptionsMenu(menu: android.view.Menu?): Boolean {
        menuInflater.inflate(R.menu.action, menu)
        return true
    }

    override fun onBackPressed() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
        if (currentFragment is HomeFragment) {
            binding.bottomNavigationView.selectedItemId = R.id.home
            finish()
        } else {
            super.onBackPressed()
            val newFragment = supportFragmentManager.findFragmentById(R.id.frame_layout)
            if (newFragment is HomeFragment) {
                binding.bottomNavigationView.selectedItemId = R.id.home
            } else if (newFragment is ListFragment) {
                binding.bottomNavigationView.selectedItemId = R.id.list
            } else if (newFragment is AccountFragment) {
                binding.bottomNavigationView.selectedItemId = R.id.account
            }
        }
    }


    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.action_search -> {
                val intent = Intent(this, SearchActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
