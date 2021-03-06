package com.example.vt6002_assignment_tamtakwa217011259

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.firebase.ui.auth.data.model.User
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.lang.Math.sqrt
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    //private lateinit var myViewModel: MyViewModel

    protected var mLastLocation: Location? = null
    protected var mLocationRequest: LocationRequest? = null
    protected var mGeocoder: Geocoder? = null
    protected var mLocationProvider: FusedLocationProviderClient? = null
    private var checkLocat:Boolean = false
    private lateinit var mMap: GoogleMap
    /**
    @Description/Purpose : location call back
    @Required Inputs : GPS
    @Expected Outputs : show location on map and display distance to target
     */
    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            mLastLocation = result.lastLocation
            // Add a marker in Sydney and move the camera
            val sydney = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
            //val sydney = LatLng(22.2834, 114.1563)
            if(checkLocat){
                mMap.clear()
                val cheongsamSydney = LatLng(22.28475, 114.155332)
                mMap.addMarker(MarkerOptions().position(cheongsamSydney).title("20s Hong Kong Qipao Experience"))
                mMap.addMarker(MarkerOptions().position(sydney).title("My Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16.0f))
                checkLocat = false;
            }
            val W = 6371;
            val x = (114.155332 - mLastLocation!!.longitude) * Math.cos((mLastLocation!!.latitude + 22.28475) / 2);
            val y = (22.28475 - mLastLocation!!.latitude);
            val distance = sqrt(x * x + y * y) * W;
            var mLongitudeText = findViewById<View>(R.id.distance) as TextView
            mLongitudeText.text = distance.toString()
            //stopLocationUpdates()
        }
    }

    private var sensorManager: SensorManager? = null
    private var acceleration = 0f
    private var currentAcceleration = 0f
    private var lastAcceleration = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /**
        @Description/Purpose : Permission to a location from a user
         */
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
            ActivityResultCallback<Map<String?, Boolean?>> { result: Map<String?, Boolean?> ->
                val fineLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_FINE_LOCATION, false
                )
                val coarseLocationGranted = result.getOrDefault(
                    Manifest.permission.ACCESS_COARSE_LOCATION, false
                )
                if (fineLocationGranted != null && fineLocationGranted) {
                    // Precise location access granted.
                    // permissionOk = true;
                } else if (coarseLocationGranted != null && coarseLocationGranted) {
                    // Only approximate location access granted.
                    // permissionOk = true;
                } else {
                    // permissionOk = false;
                    // No location access granted.
                }
            }
        )
        locationPermissionRequest.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )


        // LocationReques sets how often etc the app receives location updates
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 10
        mLocationRequest!!.fastestInterval = 5
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        Objects.requireNonNull(sensorManager)!!
            .registerListener(sensorListener, sensorManager!!
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

        acceleration = 10f
        currentAcceleration = SensorManager.GRAVITY_EARTH
        lastAcceleration = SensorManager.GRAVITY_EARTH

        //myViewModel = ViewModelProvider(this).get(MyViewModel::class.java)
        val openSignPageBtn:Button = findViewById(R.id.openLogin)
        Log.d("dsadsadsa","dsadsads ${MySignleton.openLoginPageOBj.openLoginPageBtn}")
        openSignPageBtn.text=MySignleton.openLoginPageOBj.openLoginPageBtn
        //openSignPageBtn.text = myViewModel.openBtnStr
    }

    /**
    @Description/Purpose : input google map and target location
    @Expected Outputs : display google map and target location
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(22.28475, 114.155332)
        mMap.addMarker(MarkerOptions().position(sydney).title("20s Hong Kong Qipao Experience"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16.0f))
    }

    /**
    @Description/Purpose : get location
    @Required Inputs : GPS
    @Expected Outputs : return location from gps
     */
    fun onStartClicked(local: View?){
        mLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        checkLocat = true
        mLocationProvider!!!!.requestLocationUpdates(
            mLocationRequest!!,
            mLocationCallBack, Looper.getMainLooper()
        )
    }


    /**
    @Description/Purpose : shake the phone to get location
    @Required Inputs : shake
    @Expected Outputs : return location from gps
     */
    private val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {

            // Fetching x,y,z values
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            lastAcceleration = currentAcceleration

            // Getting current accelerations
            // with the help of fetched x,y,z values
            currentAcceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta

            // Display a Toast message if
            // acceleration value is over 12
            if (acceleration > 12) {
                Toast.makeText(applicationContext, "Shake Location", Toast.LENGTH_SHORT).show()
                onStartClicked(null)
            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    override fun onResume() {
        sensorManager?.registerListener(sensorListener, sensorManager!!.getDefaultSensor(
            Sensor .TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL
        )
        super.onResume()
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(sensorListener)
        super.onPause()
    }

    /**
    @Description/Purpose : open sign in page
    @Expected Outputs : display sign in page
     */
    fun openSignIn(view:View){
        val btn:Button = findViewById(R.id.openLogin)
        if(btn.text=="Sign In"){
            val intent = Intent(this, LoginPage::class.java )
            startActivity(intent)
        }else{
            Log.d("dsadasdsa","fdsadsads log out")
            MySignleton.openLoginPageOBj.openLoginPageBtn = "Sign In"
            Log.d("dsadasdsa","fdsadsads ${MySignleton.openLoginPageOBj.openLoginPageBtn}")
            btn.text = MySignleton.openLoginPageOBj.openLoginPageBtn
            //myViewModel.openBtnStr = "Sign In"
            //Log.d("dsadasdsa","fdsadsadsa ${myViewModel.openBtnStr}")
            //val intent = Intent(this, MainActivity::class.java )
            //startActivity(intent)
        }
    }

    /**
    @Description/Purpose : open Cheongsam list page
    @Expected Outputs : display Cheongsam list page
     */
    fun openList(view:View){
        val intent = Intent(this, PhotoListActivity::class.java )
        startActivity(intent)
    }
}
