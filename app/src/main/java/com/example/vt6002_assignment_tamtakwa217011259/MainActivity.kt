package com.example.vt6002_assignment_tamtakwa217011259

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.text.DateFormat
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    protected var mLastLocation: Location? = null
    protected var mLocationRequest: LocationRequest? = null
    protected var mGeocoder: Geocoder? = null
    protected var mLocationProvider: FusedLocationProviderClient? = null

    private lateinit var mMap: GoogleMap
    var mLocationCallBack: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            mLastLocation = result.lastLocation
            // Add a marker in Sydney and move the camera
            //val sydney = LatLng(mLastLocation!!.latitude, mLastLocation!!.longitude)
            val sydney = LatLng(22.2834, 114.1563)
            mMap.addMarker(MarkerOptions().position(sydney).title("My Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10.0f))
            val W = 6371;
            val x = (114.155332 - 114.1563) * Math.cos((22.2834 + 22.28475) / 2);
            val y = (22.28475 - 22.2834);
            val distance = Math.sqrt(x * x + y * y) * W;
            var mLongitudeText = findViewById<View>(R.id.distance) as TextView
            mLongitudeText!!.text = distance.toString()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

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
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(22.28475, 114.155332)
        mMap.addMarker(MarkerOptions().position(sydney).title("20s Hong Kong Qipao Experience"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16.0f))
    }

    fun onStartClicked(local: View){
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
        mLocationProvider!!!!.requestLocationUpdates(
            mLocationRequest!!,
            mLocationCallBack, Looper.getMainLooper()
        )
    }
}
