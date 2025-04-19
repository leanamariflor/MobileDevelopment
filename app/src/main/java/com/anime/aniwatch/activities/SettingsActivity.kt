package com.anime.aniwatch.activities

import android.os.Bundle
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.anime.aniwatch.R
import com.anime.aniwatch.adapter.ExpandableListAdapter

class SettingsActivity : AppCompatActivity() {

    private lateinit var expandableListView: ExpandableListView
    private lateinit var adapter: ExpandableListAdapter
    private lateinit var listData: HashMap<String, List<List<String>>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        // Set up the toolbar with a back button
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        // Initialize the ExpandableListView
        expandableListView = findViewById(R.id.expandableListView)
        expandableListView.divider = resources.getDrawable(android.R.color.transparent, null)
        expandableListView.dividerHeight = 0
        // Generate the data for the expandable list
        listData = generateData()

        // Set up the adapter
        adapter = ExpandableListAdapter(this, listData)  // Pass context as 'this'
        expandableListView.setAdapter(adapter)


        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val group = adapter.getGroup(groupPosition).toString()
            val child = adapter.getChild(groupPosition, childPosition) as List<String>

            // Example: Log click or show a Toast
            Toast.makeText(this, "Clicked: ${child[0]}", Toast.LENGTH_SHORT).show()

            true // important: return true to indicate the click was handled
        }



    }

    private fun generateData(): HashMap<String, List<List<String>>> {
        val data = HashMap<String, List<List<String>>>()

        // Group: Developers
        val developers = listOf(
            listOf(getString(R.string.person1_name), getString(R.string.person1_bio), getString(R.string.person1_experience)),
            listOf(getString(R.string.person2_name), getString(R.string.person2_bio), getString(R.string.person2_experience))
        )

        // Group: Privacy Policy
        val privacy = listOf(
            listOf(getString(R.string.policy_title1), getString(R.string.policy_section_1_desc)),
            listOf(getString(R.string.policy_title2), getString(R.string.policy_section_2_desc)),
            listOf(getString(R.string.policy_title3), getString(R.string.policy_section_3_desc)),
            listOf(getString(R.string.policy_title4), getString(R.string.policy_section_4_desc)),
            listOf(getString(R.string.policy_title5), getString(R.string.policy_section_5_desc)),
            listOf(getString(R.string.policy_title6), getString(R.string.policy_section_6_desc))
        )

        // Adding groups to the data
        data["Developers"] = developers
        data["Privacy Policy"] = privacy

        return data
    }
}
