package com.anime.aniwatch.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.anime.aniwatch.R
import com.anime.aniwatch.adapter.FragmentAdapter
import com.google.android.material.tabs.TabLayout

class HomeFragment : Fragment() {

    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        viewPager = view.findViewById(R.id.viewPager)
        tabLayout = view.findViewById(R.id.tabLayout)

        val fragmentAdapter = FragmentAdapter(childFragmentManager)
        fragmentAdapter.addFragment(DayFragment(), "Today")
        fragmentAdapter.addFragment(WeekFragment(), "Week")
        fragmentAdapter.addFragment(MonthFragment(), "Month")

        viewPager?.adapter = fragmentAdapter

        tabLayout?.setupWithViewPager(viewPager)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()

        viewPager?.adapter = null
        viewPager = null
        tabLayout = null
    }
}
