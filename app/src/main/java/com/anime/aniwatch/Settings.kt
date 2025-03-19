package com.anime.aniwatch

import android.os.Bundle
import android.provider.Settings.Global.getString
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar



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
    }

    private fun generateData(): HashMap<String, List<List<String>>> {
        val data = HashMap<String, List<List<String>>>()

        // Group: Developers with sub-items (Person 1, Person 2)
        val developers = listOf(
            listOf(getString(R.string.person1_name), getString(R.string.person1_bio), getString(R.string.person1_experience)),  // Person 1 details
            listOf(getString(R.string.person2_name), getString(R.string.person2_bio), getString(R.string.person2_experience))  // Person 2 details
        )

        // Adding groups to the data
        data["Developers"] = developers

        return data
    }
}



