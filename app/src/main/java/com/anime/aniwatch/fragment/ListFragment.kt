package com.anime.aniwatch.fragment

import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.anime.aniwatch.Download
import com.anime.aniwatch.R
import com.anime.aniwatch.adapter.FragmentAdapter
import com.google.android.material.tabs.TabLayout

class ListFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity as? AppCompatActivity
        activity?.supportActionBar?.apply {
            show()
            title = "My List"
        }

        val viewPager: ViewPager = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        val fragmentAdapter = FragmentAdapter(childFragmentManager)
        fragmentAdapter.addFragment(WatchlistFragment(), "Watchlist")
        fragmentAdapter.addFragment(HistoryFragment(), "History")
        fragmentAdapter.addFragment(Download(), "Download")


        viewPager.adapter = fragmentAdapter
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val activity = activity as? AppCompatActivity
    }
}
