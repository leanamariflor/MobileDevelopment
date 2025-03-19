package com.anime.aniwatch

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ImageView
import android.widget.TextView

class ExpandableListAdapter(
    private val context: Context,
    private val data: HashMap<String, List<List<String>>>
) : BaseExpandableListAdapter() {

    private val headers = data.keys.toList()

    override fun getGroupCount(): Int = headers.size

    override fun getChildrenCount(groupPosition: Int): Int =
        data[headers[groupPosition]]?.size ?: 0

    override fun getGroup(groupPosition: Int): Any = headers[groupPosition]

    override fun getChild(groupPosition: Int, childPosition: Int): Any =
        data[headers[groupPosition]]!![childPosition]

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getChildId(groupPosition: Int, childPosition: Int): Long =
        childPosition.toLong()

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(parent?.context)
            .inflate(android.R.layout.simple_expandable_list_item_1, parent, false)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = getGroup(groupPosition).toString()
        return view
    }

    override fun getChildView(
        groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?
    ): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.activity_developer, parent, false)

        val child = getChild(groupPosition, childPosition) as List<String>

        val personName: TextView = view.findViewById(R.id.personName)
        val personBio: TextView = view.findViewById(R.id.personBio)
        val personImage: ImageView = view.findViewById(R.id.imageView)
        val vision: TextView = view.findViewById(R.id.vision)
        val visionLabel: TextView = view.findViewById(R.id.visionLabel)


        personName.text = child[0]
        personBio.text = child[1]

        if (childPosition == 0) {
            visionLabel.visibility = View.VISIBLE
            vision.visibility = View.VISIBLE
        } else {
            visionLabel.visibility = View.GONE
            vision.visibility = View.GONE
        }

        if (childPosition == 0) {
            personImage.setImageResource(R.drawable.profile1)
        } else {
            personImage.setImageResource(R.drawable.profile2)
        }

        return view
    }


    override fun getChildType(groupPosition: Int, childPosition: Int): Int {
        return 0
    }

    override fun getChildTypeCount(): Int {
        return 1
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }
}
