package com.anime.aniwatch.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.anime.aniwatch.R
import com.anime.aniwatch.data.DownloadLists

class SubjectListViewAdapter(
    val context: Context,
    val subjectList: List<DownloadLists>,
    val onClickShowMore : (DownloadLists) -> Unit,
    val onClickItem: (DownloadLists) -> Unit,
    val onLongPressed: (position: Int) -> Unit
): BaseAdapter() {

    override fun getView(position: Int, contentView: View?, parent: ViewGroup?): View {
        val view = contentView ?: LayoutInflater.from(context).inflate(
            R.layout.item_list_of_downloads,
            parent,
            false
        )

        val name = view.findViewById<TextView>(R.id.textview_name)
        val desc = view.findViewById<TextView>(R.id.textview_desc)
        val savedDate = view.findViewById<TextView>(R.id.textview_savedDate)

        val subject = subjectList[position]

        name.setText(subject.name)
        desc.setText(subject.desc)
        savedDate.setText(subject.savedDate)

        view.setOnClickListener {
            onClickItem(subject)
        }

        view.setOnLongClickListener {
            onLongPressed(position)
            true
        }

        return view
    }

    override fun getCount(): Int = subjectList.size

    override fun getItem(position: Int): Any = subjectList[position]

    override fun getItemId(position: Int): Long = position.toLong()
}