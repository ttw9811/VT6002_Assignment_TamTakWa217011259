package com.example.vt6002_assignment_tamtakwa217011259

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.internal.ContextUtils.getActivity
import com.paypal.pyplcheckout.services.Repository
import org.junit.Assert.*
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*

class MainActivityTest{
    @Test
    fun onMapReadyTest() {
        val googleMap = mock(GoogleMap::class.java)
        val activity = mock(Activity::class.java)
        val sydney = LatLng(22.28475, 114.155332)
        //simulate the behaviour of the objects

        `when`(MapsInitializer.initialize(activity))
            .thenReturn(any())
        `when`(googleMap.addMarker(MarkerOptions().position(sydney).title("20s Hong Kong Qipao Experience")))
            .thenReturn(any())
        `when`(googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,16.0f))).thenReturn(any())

        val repository = MainActivity()
        repository.onMapReady(googleMap)
        //verify is the method call
        assertEquals(any(),repository)
    }
}