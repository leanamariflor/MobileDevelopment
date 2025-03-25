package com.anime.aniwatch

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.anime.aniwatch.adapter.SubjectListViewAdapter
import com.anime.aniwatch.data.DownloadLists

class Download : Fragment() {
    private lateinit var listOfSubjects: MutableList<DownloadLists>
    private lateinit var arrayAdapter: SubjectListViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_download, container, false)

        val listView = view.findViewById<ListView>(R.id.listview)

        listOfSubjects = mutableListOf(
            DownloadLists("Episode 1", "Description 1", "2023-01-01"),
            DownloadLists("Episode 2", "Description 2", "2023-01-02"),
            DownloadLists("Episode 3", "Description 3", "2023-01-03")
        )

        arrayAdapter = SubjectListViewAdapter(
            requireContext(),
            listOfSubjects,
            onClickShowMore = { subject ->
                showOtherDetailsDialog(subject)
            },
            onClickItem = { subject ->
                listOfSubjects.add(subject)
                arrayAdapter.notifyDataSetChanged()
            },
            onLongPressed = { position ->
                showDeleteDialog(position)
            }
        )

        listView.adapter = arrayAdapter
        return view
    }

    private fun showOtherDetailsDialog(subject: DownloadLists) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(subject.name)
        builder.setMessage(subject.desc)
        builder.setPositiveButton("Okay", null)
        builder.show()
    }

    private fun showDeleteDialog(position: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Delete item")
        builder.setMessage("Are you sure to delete ${listOfSubjects[position].name}?")
        builder.setPositiveButton("Remove") { _, _ ->
            listOfSubjects.removeAt(position)
            arrayAdapter.notifyDataSetChanged()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }
}
