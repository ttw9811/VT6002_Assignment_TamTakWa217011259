package com.example.vt6002_assignment_tamtakwa217011259

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.*
import com.paypal.checkout.approve.OnApprove
import com.paypal.checkout.cancel.OnCancel
import com.paypal.checkout.createorder.CreateOrder
import com.paypal.checkout.createorder.CurrencyCode
import com.paypal.checkout.createorder.OrderIntent
import com.paypal.checkout.createorder.UserAction
import com.paypal.checkout.error.OnError
import com.paypal.checkout.order.Amount
import com.paypal.checkout.order.AppContext
import com.paypal.checkout.order.Order
import com.paypal.checkout.order.PurchaseUnit
import com.paypal.checkout.paymentbutton.PaymentButtonEligibilityStatus

class PhotoListActivity : AppCompatActivity(), TaskRowListener {
    private lateinit var myViewModel: MyViewModel
    lateinit var _db: DatabaseReference
    private var _taskList: MutableList<Task>? = null
    lateinit var _adapter: TaskAdapter

    var _taskListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            loadTaskList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)
        //connect firebase database
        _db = FirebaseDatabase.getInstance("https://ass-974e1-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

        _taskList = mutableListOf()
        _adapter = TaskAdapter(this, _taskList!!)

        val listviewTask = findViewById<ListView>(R.id.listViewComplex)
        listviewTask!!.setAdapter(_adapter)
        listviewTask.setOnItemClickListener { parent, view, position, id ->
            Toast.makeText(this, "You clicked ", Toast.LENGTH_LONG).show()

            Log.d("list page","Name ${_adapter.getItemName(position)}")
            Log.d("list page","Price ${_adapter.getItemPrice(position)}")


            //val intent = Intent(this, testPay::class.java )
            //startActivity(intent)
        }
        _db.orderByKey().addValueEventListener(_taskListener)

    }

    data class Candidate(val name : String, val price : String, val photo : Int)

    /**
    @Description/Purpose : load Cheongsam item in list and display
    @Required Inputs : dataSnapshot
    @Expected Outputs : display Cheongsam item
     */
    private fun loadTaskList(dataSnapshot: DataSnapshot) {
        Log.d("MainActivity", "loadTaskList")
        val tasks = dataSnapshot.children.iterator()
        //Check if current database contains any collection
        if (tasks.hasNext()) {
            _taskList!!.clear()
            val listIndex = tasks.next()
            val itemsIterator = listIndex.children.iterator()

            //check if the collection has any task or not
            while (itemsIterator.hasNext()) {
                //get current task
                val currentItem = itemsIterator.next()
                val task = Task.create()
                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>
                //key will return the Firebase ID
                task.objectId = currentItem.key
                task.done = map.get("done") as Boolean?
                task.name = map.get("name") as String?
                task.price = map.get("price") as String?
                //val imageBytes: ByteArray = Base64.decode(map.get("image") as String?, Base64.DEFAULT)
                //val imageBytes = Base64.decode(map.get("image") as String?,Base64.DEFAULT)
                task.image =  map.get("image")as String?
                _taskList!!.add(task)
            }
        }

        //alert adapter that has changed
        _adapter.notifyDataSetChanged()
    }

    /**
    @Description/Purpose : select Cheongsam item and change MySignleton.priceMax.priceMax
    @Required Inputs : objectId,price,isDone
    @Expected Outputs : update MySignleton.priceMax.priceMax
     */
    override fun onTaskChange(objectId: String, price:String, isDone: Boolean) {
        Log.d("Select item ","item task ${objectId}")
        Log.d("Select item ","item task ${price}")
        Log.d("Select item ","item task ${isDone}")
        if(isDone){
            MySignleton.priceMax.priceMax = MySignleton.priceMax.priceMax+price.toInt()
        }else{
            MySignleton.priceMax.priceMax = MySignleton.priceMax.priceMax+price.toInt()
        }
        Log.d("Select item ","item task ${MySignleton.priceMax.priceMax}")
    }

    fun openPaymentPage(view:View){
        val intent = Intent(this, PaymentPage::class.java )
        startActivity(intent)
    }
}



class CandidateAdapter(
    context: Context,
    resource: Int,
    objects: MutableList<PhotoListActivity.Candidate>
) : ArrayAdapter<PhotoListActivity.Candidate>(context, resource, objects), ListAdapter {
    private var resource = resource
    private var candidates = objects
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if(v==null){
            val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = layoutInflater.inflate(resource, parent, false)
        }

        var imageView = v!!.findViewById<ImageView>(R.id.imageView)
        var textViewName = v!!.findViewById<TextView>(R.id.textViewName)
        var textViewDetail = v!!.findViewById<TextView>(R.id.textViewDetail)
        imageView.setImageResource(candidates[position].photo)
        textViewName.text = candidates[position].name
        textViewDetail.text = candidates[position].price
        return v!!
    }
}