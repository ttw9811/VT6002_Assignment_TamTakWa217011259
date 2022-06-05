package com.example.vt6002_assignment_tamtakwa217011259

import android.content.Context
import android.graphics.BitmapFactory
import android.media.Image
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.text.DateFormat.DEFAULT
import java.util.*

class TaskAdapter (context: Context, taskList: MutableList<Task>) : BaseAdapter(){
    private val _inflater: LayoutInflater = LayoutInflater.from(context)
    private var _taskList = taskList

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val objectId: String = _taskList.get(position).objectId as String
        val itemText: String = _taskList.get(position).name as String
        val priceText: String = _taskList.get(position).price as String
        val imageBit: ByteArray = _taskList.get(position).image as ByteArray
        val done: Boolean = _taskList.get(position).done as Boolean



        val view: View

        val listRowHolder: ListRowHolder
        if (convertView == null) {
            view = _inflater.inflate(R.layout.list_item, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }



        listRowHolder.name.text = itemText
        listRowHolder.price.text = priceText
        listRowHolder.image.setImageBitmap(BitmapFactory.decodeByteArray(imageBit, 0, imageBit.size))
        return view
    }

    override fun getItem(index: Int): Any {
        return _taskList.get(index)
    }
    fun getItemName(index: Int): Any {
        return _taskList.get(index).name as String
    }

    fun getItemPrice(index: Int): Any {
        return _taskList.get(index).price as String
    }
    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    override fun getCount(): Int {
        return _taskList.size
    }

    private class ListRowHolder(row: View?) {
        val name: TextView = row!!.findViewById(R.id.textViewName) as TextView
        val price: TextView = row!!.findViewById(R.id.textViewDetail) as TextView
        val image: ImageView =row!!.findViewById(R.id.imageView)  as ImageView
    }
}