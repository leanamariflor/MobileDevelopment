package com.anime.aniwatch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.anime.aniwatch.adapter.FragmentAdapter
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val viewPager: ViewPager = view.findViewById(R.id.viewPager)
        val tabLayout: TabLayout = view.findViewById(R.id.tabLayout)

        val fragmentAdapter = FragmentAdapter(childFragmentManager)
        fragmentAdapter.addFragment(Day(), "Today")
        fragmentAdapter.addFragment(Week(), "Week")
        fragmentAdapter.addFragment(Month(), "Month")

        viewPager.adapter = fragmentAdapter

        tabLayout.setupWithViewPager(viewPager)

        return view
    }
}
