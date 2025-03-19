package com.anime.aniwatch

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.GridView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.anime.aniwatch.adapter.ProfileImageAdapter
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
                    binding.profileImage.setImageURI(imageUri)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Edit Profile"

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val userEmail = sharedPreferences.getString("userEmail", "")
        val username = sharedPreferences.getString("username", "")
        binding.emailadd.setText(userEmail)
        binding.username.setText(username)

        binding.profileImage.setOnClickListener {
            showImagePickerDialog()
        }

        binding.update.setOnClickListener {
            updateProfile()
        }
    }

    private fun showImagePickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.profile_images, null)
        val gridView = dialogView.findViewById<GridView>(R.id.gridViewImages)

        val imageIds = arrayOf(
            R.drawable.zoro, R.drawable.luffy, R.drawable.sanji

        )

        val imageAdapter = ProfileImageAdapter(this, imageIds)
        gridView.adapter = imageAdapter

        val dialog = android.app.AlertDialog.Builder(this)
            .setTitle("Select Profile Image")
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()

        gridView.setOnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> setProfileImage(R.drawable.zoro) 1 -> setProfileImage(R.drawable.luffy) 2 -> setProfileImage(R.drawable.sanji)
            }
            dialog.dismiss()
        }
    }

    private fun setProfileImage(imageResId: Int) {
        binding.profileImage.setImageResource(imageResId)

        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putInt("profileImageRes", imageResId)
        editor.apply()
    }

    private fun loadProfileImage() {
        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val savedImageResId = sharedPreferences.getInt("profileImageRes", R.drawable.zoro)
        binding.profileImage.setImageResource(savedImageResId)
    }

    private fun updateProfile() {
        val username = binding.username.text.toString().trim()
        val email = binding.emailadd.text.toString().trim()

        if (username.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        val sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.putString("userEmail", email)
        editor.apply()

        if (imageUri != null) {

        }

        Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show()

        val resultIntent = Intent()
        resultIntent.putExtra("updatedUsername", username)
        setResult(RESULT_OK, resultIntent)
        finish()
    }
}
