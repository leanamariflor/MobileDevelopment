package com.anime.aniwatch.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anime.aniwatch.activities.ProfileEditActivity
import com.anime.aniwatch.activities.SecurityActivity
import com.anime.aniwatch.activities.SplashActivity
import com.anime.aniwatch.databinding.FragmentAccountBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.StorageReference
import com.anime.aniwatch.R
import com.anime.aniwatch.activities.NotificationsActivity
import com.anime.aniwatch.activities.SettingsActivity

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        val userEmail = auth.currentUser?.email
        binding.email.text = userEmail ?: "No Email"

        if (uid != null) {
            databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val username = snapshot.child("username").getValue(String::class.java)
                        Log.d("AccountFragment", "Fetched username: $username")
                        binding.fullName.text = username ?: "Unknown User"
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to load profile", Toast.LENGTH_SHORT).show()
                }
            })
        }

        val sharedPreferences = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val savedImageResId = sharedPreferences.getInt("profileImageRes", R.drawable.account)

        val profileImageView: ImageView = binding.profile
        profileImageView.setImageResource(savedImageResId)

        binding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.notifications.setOnClickListener {
            val intent = Intent(requireContext(), NotificationsActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.settings.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.security.setOnClickListener {
            val intent = Intent(requireContext(), SecurityActivity::class.java)
            startActivity(intent)
        }

        binding.logout.setOnClickListener {
            showLogoutDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 100) {

            val updatedUsername = data?.getStringExtra("updatedUsername")
            val updatedEmail = data?.getStringExtra("updatedEmail")

            if (!updatedUsername.isNullOrEmpty()) {
                binding.fullName.text = updatedUsername
            }

            updateUserProfile(updatedUsername, updatedEmail)

            val imageResId = data?.getIntExtra("selectedImage", R.drawable.account) ?: R.drawable.account
            val profileImageView: ImageView = binding.profile
            profileImageView.setImageResource(imageResId)
        }
    }

    private fun updateUserProfile(updatedUsername: String?, updatedEmail: String?) {
        val user = auth.currentUser
        val uid = user?.uid
        if (uid != null) {

            val userUpdates = mapOf<String, Any>(
                "username" to updatedUsername.orEmpty(),
                "email" to updatedEmail.orEmpty()
            )
            databaseReference.child(uid).updateChildren(userUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(updatedUsername)
            .setPhotoUri(null)
            .build()

        user?.updateProfile(profileUpdates)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("AccountFragment", "User profile updated.")
            }
        }

        updatedEmail?.let {
            user?.updateEmail(it)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("AccountFragment", "Email updated.")
                } else {
                    Toast.makeText(requireContext(), "Failed to update email", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showLogoutDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirm)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancel)

        btnConfirm.setOnClickListener {
            logoutUser()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun logoutUser() {
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("email", "") ?: ""
        val savedPassword = sharedPreferences.getString("password", "") ?: ""
        val isRememberMe = sharedPreferences.getBoolean("isLoggedIn", false)

        sharedPreferences.edit().apply {
            clear()
            putString("email", savedEmail)
            putString("password", savedPassword)
            putBoolean("isLoggedIn", isRememberMe)
            apply()
        }

        auth.signOut()

        val intent = Intent(activity, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
