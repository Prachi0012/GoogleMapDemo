package com.example.demo

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.example.demo.databinding.ActivityMapRouteBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import java.io.IOException

class MapRouteActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var addedMarker: Marker
    private lateinit var myLocation: LatLng
    private lateinit var mySecondLocation: LatLng
    private lateinit var mMap: GoogleMap
    var polyline: Polyline? = null

    private lateinit var binding: ActivityMapRouteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initView()    }

    private fun initView() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // adding on query listener for our search view.
        binding.idSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // on below line we are getting the
                // location name from search view.
                val location = binding.idSearchView.query.toString()

                // below line is to create a list of address
                // where we will store the list of all address.
                var addressList: List<Address>? = null

                // checking if the entered location is null or not.
                if (location != null || location == "") {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(this@MapRouteActivity)
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(location, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    var address = addressList!![0]

                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    myLocation = LatLng(address.latitude, address.longitude)

                    Log.e(
                        "TAG",
                        "latitude:-  " + address.latitude + " " + "longitude:- " + address.longitude
                    )


                    // on below line we are adding marker to that position.
                    addedMarker =
                        mMap.addMarker(MarkerOptions().position(myLocation).title(location))!!

                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 5f))


                }

                return false

            }


            override fun onQueryTextChange(newText: String): Boolean {

                return false

            }

        })

        // at last we calling our map fragment to update.
        mapFragment!!.getMapAsync(this)


        binding.btnnext.setOnClickListener {
            var i = Intent(this,RouteActivity::class.java)
            startActivity(i)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // adding on query listener for our search view.
        binding.secondSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                val secondLocation = binding.secondSearchView.query.toString()

                var addressList: List<Address>? = null

                if (secondLocation != null || secondLocation == "") {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(this@MapRouteActivity)
                    try {
                        // on below line we are getting location from the
                        // location name and adding that location to address list.
                        addressList = geocoder.getFromLocationName(secondLocation, 1)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    // on below line we are getting the location
                    // from our list a first position.
                    var secondAddress = addressList!![0]
                    // on below line we are creating a variable for our location
                    // where we will add our locations latitude and longitude.
                    mySecondLocation = LatLng(secondAddress.latitude, secondAddress.longitude)
                    Log.e("TAG", "latitude:-  " + secondAddress.latitude + " " + "longitude:- " + secondAddress.longitude)
                    drawMarker(mySecondLocation, false)
                    drawline(
                        mySecondLocation.latitude,
                        mySecondLocation.longitude,
                        TransportMode.DRIVING
                    )
                    // on below line we are adding marker to that position.
                    addedMarker = mMap.addMarker(MarkerOptions().position(mySecondLocation).title(secondLocation))!!
                    // below line is to animate camera to that position.
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mySecondLocation, 5f))
                }
                return false
            }
            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
        mMap.setOnMapClickListener {

            drawMarker(it, false)
            drawline(it.latitude, it.longitude, TransportMode.DRIVING)

        }
    }
    private fun drawMarker(latLng: LatLng, flag: Boolean) {
        if (mMap != null) {

            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)


            //            markerOptions.title(title);
            if (flag) {
//                markerOptions.icon(BitmapFromVector(this, R.drawable.ic_map_event));
                markerOptions.icon(BitmapFromVector(this, R.drawable.ic_arrow))
            } else {
                Log.e("TAG", "drawMarker: mark")
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            }
            mMap!!.addMarker(markerOptions)

            mMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))


        }
    }

    private fun BitmapFromVector(context: Context, vectorResId: Int): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun drawline(lat: Double, longt: Double, mode: String) {
        try {
//            drawMarker(LatLng(lat, longt), false)
            GoogleDirection.withServerKey("AIzaSyBv6cUUv3hbIEDcG69F297b37KqrTjepSg")
                .from(LatLng(myLocation!!.latitude, myLocation!!.longitude))
                .to(LatLng(lat, longt))
                .avoid(AvoidType.FERRIES)
                .avoid(AvoidType.HIGHWAYS)
                .transportMode(mode)
                .execute(object : DirectionCallback {
                    override fun onDirectionSuccess(direction: Direction?) {
                        directionsuccess(direction)
                    }

                    override fun onDirectionFailure(t: Throwable) {}
                })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("TAG", "drawline:exce " + e.message)
        }
    }

    private fun directionsuccess(direction: Direction?) {
        try {
            if (direction!!.isOK) {
                val route = direction.routeList[0]
                if (route != null && !route.legList.isEmpty()) {
                    val distance = route.legList[0].distance
                    val duration = route.legList[0].duration
                    val directionPositionList = route.legList[0].directionPoint
                    if (!directionPositionList.isEmpty()) {
                        if (polyline != null) {
                            polyline!!.remove()
                        }
                        polyline = mMap!!.addPolyline(
                            DirectionConverter.createPolyline(
                                this,
                                directionPositionList,
                                4,
                                ContextCompat.getColor(this, R.color.Blue)
                            )
                        )
                        setCameraWithCoordinationBounds(route)
                    } else {
                        Toast.makeText(this, "noroute_available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "noroute_available", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "noroute_available", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCameraWithCoordinationBounds(route: Route) {
        val southwest = route.bound.southwestCoordination.coordination
        val northeast = route.bound.northeastCoordination.coordination
        val bounds = LatLngBounds(southwest, northeast)
        mMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))
    }


}