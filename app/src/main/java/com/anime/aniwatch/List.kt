package com.anime.aniwatch

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as? AppCompatActivity
        activity?.supportActionBar?.apply {
            show()  // Ensure toolbar is visible
            title = "My List"  // Set title
        }

        // Hide the logo when List fragment is opened
        val logo: ImageView? = activity?.findViewById(R.id.logo)
        logo?.visibility = View.GONE  // Hide the logo
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Restore the logo when leaving List fragment
        val activity = activity as? AppCompatActivity
        val logo: ImageView? = activity?.findViewById(R.id.logo)
        logo?.visibility = View.VISIBLE  // Show the logo again
    }
}
