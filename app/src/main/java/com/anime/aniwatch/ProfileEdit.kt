package com.anime.aniwatch

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.anime.aniwatch.databinding.ActivityProfileEditBinding

class ProfileEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private var imageUri: Uri? = null
    private val TAG = "PROFILE_EDIT_TAG"

    private val imagePicker =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data = result.data
                if (data != null && data.data != null) {
                    imageUri = data.data
                    binding.profileImage.setImageURI(imageUri) // Show selected image
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the toolbar with a back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Load saved email and username
        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "")
        val username = sharedPreferences.getString("username", "")
        binding.emailadd.setText(userEmail)
        binding.username.setText(username)

        // Handle profile image click - show predefined images to select
        binding.profileImage.setOnClickListener {
            // Show predefined profile images from drawable
            showImagePickerDialog()
        }

        binding.update.setOnClickListener {
            updateProfile()
        }
    }

    private fun showImagePickerDialog() {
        // Inflate the dialog layout
        val dialogView = layoutInflater.inflate(R.layout.profile_images, null)
        val gridView = dialogView.findViewById<GridView>(R.id.gridViewImages)

        // Dynamically initialize the image IDs array
        val imageIds = arrayOf(
            R.drawable.zoro, R.drawable.luffy, R.drawable.sanji

        )

        // Initialize the adapter and set it to the GridView
        val imageAdapter = ProfileImageAdapter(this, imageIds)
        gridView.adapter = imageAdapter

        // Create the dialog
        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Select Profile Image")
            .setView(dialogView)
            .setCancelable(true)  // Allow dialog to be canceled by tapping outside
            .create()

        // Show the dialog
        dialog.show()

        gridView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> setProfileImage(R.drawable.zoro) 1 -> setProfileImage(R.drawable.luffy) 2 -> setProfileImage(R.drawable.sanji)
            }
            dialog.dismiss()
        }
    }

    private fun setProfileImage(imageResId: Int) {
        // Set the selected image to the profile image view
        binding.profileImage.setImageResource(imageResId)

        // Save the selected image to SharedPreferences
        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("profileImageRes", imageResId)
        editor.apply()  // Save the changes
    }

    private fun loadProfileImage() {
        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val savedImageResId = sharedPreferences.getInt("profileImageRes", R.drawable.zoro)  // Default image if not set
        binding.profileImage.setImageResource(savedImageResId)
    }

    private fun updateProfile() {
        val username = binding.username.text.toString().trim()
        val email = binding.emailadd.text.toString().trim()

        if (username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        // Save the updated username and email to SharedPreferences
        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("userEmail", email)
        editor.apply()

        // Optionally update the image if a new one was selected
        if (imageUri != null) {
            // If you want to store the image URI locally, you could do so here
            // For example: sharedPreferences.edit().putString("profileImageUri", imageUri.toString()).apply()
        }

        // Notify the user of the update
        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()

        // Send updated username back to AccountFragment
        val resultIntent = Intent()
        resultIntent.putExtra("updatedUsername", username)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
