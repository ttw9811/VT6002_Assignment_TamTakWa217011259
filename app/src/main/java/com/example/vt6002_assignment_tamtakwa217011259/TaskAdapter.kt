package com.example.vt6002_assignment_tamtakwa217011259

import android.content.Context
import android.graphics.Bitmap
import android.util.Base64
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import java.util.*

/**
@Description/Purpose : Use when creating an adapter to provide display data
@Required Inputs : context, taskList
@Expected Outputs : display data on list
 */
class TaskAdapter (context: Context, taskList: MutableList<Task>) : BaseAdapter(){
    private val _inflater: LayoutInflater = LayoutInflater.from(context)
    private var _taskList = taskList
    private var _rowListener: TaskRowListener = context as TaskRowListener

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val objectId: String = _taskList.get(position).objectId as String
        val itemText: String = _taskList.get(position).name as String
        val priceText: String = _taskList.get(position).price as String
        val imageBit: String = _taskList.get(position).image as String
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
        listRowHolder.done.isChecked = done
        listRowHolder.done.setOnClickListener {
            if(listRowHolder.done.isChecked.toString()== "true"){
                _rowListener.onTaskChange(objectId, priceText, true)
            }else{
                _rowListener.onTaskChange(objectId, priceText, false)
            }

        }
        //listRowHolder.image.setImageBitmap(BitmapFactory.decodeByteArray(imageBit, 0, imageBit.size))
        val imageSubStr = imageBit.substring(imageBit.indexOf(",")+1)
        val imageByte = Base64.decode(imageSubStr,Base64.DEFAULT)
        val bitmap = BitmapFactory.decodeByteArray(imageByte, 0, imageByte.size)
        listRowHolder.image.setImageBitmap(bitmap)
        return view
    }

    /**
    @Description/Purpose : get data detail by index
    @Required Inputs : index
    @Expected Outputs : return data information
     */
    override fun getItem(index: Int): Any {
        return _taskList.get(index)
    }

    /**
    @Description/Purpose : get data name by index
    @Required Inputs : index
    @Expected Outputs : return data name
     */
    fun getItemName(index: Int): Any {
        return _taskList.get(index).name as String
    }

    /**
    @Description/Purpose : get data price by index
    @Required Inputs : index
    @Expected Outputs : return data price
     */
    fun getItemPrice(index: Int): Any {
        return _taskList.get(index).price as String
    }

    /**
    @Description/Purpose : get data detail by id
    @Required Inputs : id
    @Expected Outputs : return data information
     */
    override fun getItemId(index: Int): Long {
        return index.toLong()
    }

    /**
    @Description/Purpose : Take the data in total
    @Required Inputs :
    @Expected Outputs : return data total
     */
    override fun getCount(): Int {
        return _taskList.size
    }

    /**
    @Description/Purpose : set display location
     */
    private class ListRowHolder(row: View?) {
        val name: TextView = row!!.findViewById(R.id.textViewName) as TextView
        val price: TextView = row!!.findViewById(R.id.textViewDetail) as TextView
        val done: CheckBox = row!!.findViewById(R.id.chkDone) as CheckBox
        val image: ImageView =row!!.findViewById(R.id.imageView)  as ImageView
    }
}