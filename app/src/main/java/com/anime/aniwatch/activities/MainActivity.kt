package com.anime.aniwatch.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anime.aniwatch.*
import com.anime.aniwatch.databinding.ActivityMainBinding
import com.anime.aniwatch.fragment.AccountFragment
import com.anime.aniwatch.fragment.HomeFragment
import com.anime.aniwatch.fragment.ListFragment
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isFragmentTransactionInProgress = false
    private val fragmentTransactionDebounceTime = 300L // 300ms debounce time

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Show disclaimer dialog
        showDisclaimerDialog()

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
    }

    private fun replaceFragment(fragment: Fragment) {
        // If a transaction is already in progress, ignore this request
        if (isFragmentTransactionInProgress) {
            return
        }

        val fragmentManager = supportFragmentManager
        val currentFragment = fragmentManager.findFragmentById(R.id.frame_layout)

        // Avoid replacing with the same fragment
        if (currentFragment != null && currentFragment::class == fragment::class) {
            return
        }

        // Set the flag to indicate a transaction is in progress
        isFragmentTransactionInProgress = true

        val fragmentTransaction = fragmentManager.beginTransaction()

        if (fragment is HomeFragment) {
            fragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        }

        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commitAllowingStateLoss()

        // Update UI based on the fragment type
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

        // Reset the flag after a delay to allow the transaction to complete
        binding.root.postDelayed({
            isFragmentTransactionInProgress = false
        }, fragmentTransactionDebounceTime)
    }
    private fun enableBackButton() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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
            // Update BottomNavigationView to reflect HomeFragment
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

    private fun hideSearchButton() {
        val searchButton = binding.toolbar.menu.findItem(R.id.action_search)
        searchButton?.isVisible = false
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

    private fun showDisclaimerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_disclaimer, null)
        val dialog = AlertDialog.Builder(this).create()
        dialog.setView(dialogView)
        dialog.setCancelable(false)

        val title = dialogView.findViewById<TextView>(R.id.dialog_title)
        val message = dialogView.findViewById<TextView>(R.id.dialog_message)
        val button = dialogView.findViewById<Button>(R.id.dialog_button)

        title.text = "⚠️ Disclaimer"
        message.text = "AniWatch is developed solely for educational purposes and is intended to serve as a learning tool for exploring concepts such as API communication, integration, and mobile application development. The app demonstrates how to interact with APIs, manage data, and implement various Android features like RecyclerView, custom adapters, and user interface components.\n\n" +
                "We emphasize that AniWatch does not host, stream, or distribute any copyrighted content. The app does not provide access to any illegal or unauthorized sources of anime or other media. All content displayed within the app is either user-provided or simulated for demonstration purposes.\n\n" +
                "By using AniWatch, users agree to take full responsibility for ensuring compliance with copyright laws and regulations in their respective regions. The developers of AniWatch do not condone or support piracy in any form. This project is strictly for educational exploration and should not be used for any activities that violate intellectual property rights."

        button.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
