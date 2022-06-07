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
        myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)

        var payPalButton:com.paypal.checkout.paymentbutton.PayPalButton = findViewById(R.id.payPalButton)
        payPalButton.onEligibilityStatusChanged = { buttonEligibilityStatus: PaymentButtonEligibilityStatus ->
            Log.d("paypal ", "OnEligibilityStatusChanged")
            Log.d("paypal ", "Button eligibility status: $buttonEligibilityStatus")
        }
        payPalButton.setup(
            createOrder =
            CreateOrder { createOrderActions ->
                val order =
                    Order(
                        intent = OrderIntent.CAPTURE,
                        appContext = AppContext(userAction = UserAction.PAY_NOW),
                        purchaseUnitList =
                        listOf(
                            PurchaseUnit(
                                amount =
                                Amount(currencyCode = CurrencyCode.HKD, value = "${myViewModel.priceMax.toString()}.00")
                            )
                        )
                    )
                createOrderActions.create(order)
            },
            onApprove =
            OnApprove { approval ->
                approval.orderActions.capture { captureOrderResult ->
                    Log.d("CaptureOrder", "CaptureOrderResult: $captureOrderResult")
                }
            },
            onCancel = OnCancel {
                Log.d("paypal ", "OnCancel")
                Log.d("paypal ", "Buyer cancelled the checkout experience.")
            },
            onError = OnError { errorInfo ->
                Log.d("paypal ", "OnError")
                Log.d("paypal ", "Error details: $errorInfo")
            }
        )
    }

    data class Candidate(val name : String, val price : String, val photo : Int)

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
                val imageBytes: ByteArray = Base64.decode(map.get("image") as String?, Base64.DEFAULT)

                task.image =  imageBytes
                _taskList!!.add(task)
            }
        }

        //alert adapter that has changed
        _adapter.notifyDataSetChanged()
    }

    override fun onTaskChange(objectId: String, price:String, isDone: Boolean) {
        Log.d("Select item ","item task ${objectId}")
        Log.d("Select item ","item task ${price}")
        Log.d("Select item ","item task ${isDone}")
        if(isDone){
            myViewModel.priceMax+=price.toInt()
        }else{
            myViewModel.priceMax-=price.toInt()
        }
        Log.d("Select item ","item task ${myViewModel.priceMax}")
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