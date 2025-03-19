package com.anime.aniwatch


import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.anime.aniwatch.databinding.SearchBinding

class SearchActivity : AppCompatActivity() {

    lateinit var binding: SearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = arrayListOf("Anime1", "Anime2", "Anime3", "Anime4", "Anime5", "Anime6", "Anime7", "Anime8")

        // Create an ArrayAdapter
        val userAdapter: ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, user
        )

        // Set the adapter to the ListView
        binding.list.adapter = userAdapter

        // Search functionality
        binding.search.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.search.clearFocus()
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                query?.let {
                    val filteredList = user.filter { it.contains(query, ignoreCase = true) }
                    userAdapter.clear()
                    userAdapter.addAll(filteredList)
                    userAdapter.notifyDataSetChanged()
                }
                return false
            }
        })
    }
}
