package com.example.demo

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.IOException

class CurrentRouteActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    lateinit var searchView: SearchView
    var addedMarker: Marker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_current_route)
        initView()
    }

    private fun initView() {
        // initializing our search view.
        searchView = findViewById(R.id.idSearchView)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?

        // adding on query listener for our search view.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val location = searchView.query.toString()

                var addressList: List<Address>? = null

                // checking if the entered location is null or not.
                if (location != null || location == "") {
                    // on below line, create and initialize a geo coder.
                    val geocoder = Geocoder(this@CurrentRouteActivity)
                    try {
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    // get the location from the list at the first position.
                    val address = addressList!![0]
                    val latLng = LatLng(address.latitude, address.longitude)

                    // add a marker to the destination position.
                    addedMarker = mMap.addMarker(MarkerOptions().position(latLng).title(location))

                    // animate the camera to the destination position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))

                    // Calculate the path between the current location and the destination
                    val currentLocation = mMap.myLocation
                    if (currentLocation != null) {
                        val currentLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                        val path = PolylineOptions()
                        path.add(currentLatLng)
                        path.add(latLng)
                        path.color(Color.BLUE)
                        mMap.addPolyline(path)
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                addedMarker?.remove()
                return false
            }
        })

        // Initialize the map fragment.
        mapFragment?.getMapAsync(this)
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable the My Location layer.
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mMap.isMyLocationEnabled = true
    }
}