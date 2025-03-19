package com.anime.aniwatch

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.anime.aniwatch.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("userPrefs", Context.MODE_PRIVATE)
        val savedEmail = sharedPreferences.getString("userEmail", "No Email") ?: "No Email"
        val savedUsername = sharedPreferences.getString("username", "Unknown User") ?: "Unknown User"
        var savedImageResId = sharedPreferences.getInt("profileImageRes", R.drawable.account)

        binding.email.text = savedEmail
        binding.fullName.text = savedUsername

        try {
            if (savedImageResId == -1) {
                savedImageResId = R.drawable.account
            }
            binding.profile.setImageResource(savedImageResId)
        } catch (e: Resources.NotFoundException) {
            e.printStackTrace()
            binding.profile.setImageResource(R.drawable.account)
        }


        binding.editProfile.setOnClickListener {
            val intent = Intent(requireContext(), ProfileEditActivity::class.java)
            startActivityForResult(intent, 100)
        }

        binding.security.setOnClickListener {
            startActivity(Intent(requireContext(), SecurityActivity::class.java))
        }

        binding.settings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        binding.notifications.setOnClickListener {
            startActivity(Intent(requireContext(), Notifications::class.java))
        }

        binding.logout.setOnClickListener {
            showLogoutDialog()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && requestCode == 100) {
            // Get the updated username (if any)
            val updatedUsername = data?.getStringExtra("updatedUsername")
            val updatedEmail = data?.getStringExtra("updatedEmail")

            if (!updatedUsername.isNullOrEmpty()) {
                binding.fullName.text = updatedUsername
            }

            val imageResId = data?.getIntExtra("selectedImage", R.drawable.account) ?: R.drawable.account
            val profileImageView: ImageView = binding.profile
            profileImageView.setImageResource(imageResId)
        }
    }

    private fun showLogoutDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_logout, null)
        val dialog = AlertDialog.Builder(requireContext()).setView(dialogView).create()

        dialogView.findViewById<Button>(R.id.btnConfirm)?.setOnClickListener {
            logoutUser()
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnCancel)?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun logoutUser() {
        val sharedPreferences = requireContext().getSharedPreferences("loginPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            remove("userEmail")
            remove("userPassword")
            apply()
        }

        val intent = Intent(activity, SignIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
