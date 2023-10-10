package com.example.demo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.akexorcist.googledirection.DirectionCallback
import com.akexorcist.googledirection.GoogleDirection
import com.akexorcist.googledirection.constant.AvoidType
import com.akexorcist.googledirection.constant.TransportMode
import com.akexorcist.googledirection.model.Direction
import com.akexorcist.googledirection.model.Route
import com.akexorcist.googledirection.util.DirectionConverter
import com.example.demo.databinding.ActivityMapRouteBinding
import com.example.demo.databinding.ActivityRouteBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

class RouteActivity : AppCompatActivity() ,OnMapReadyCallback{
    private lateinit var addedMarker: Marker
    private lateinit var myLocation: LatLng
    private lateinit var mySecondLocation: LatLng
    private lateinit var mMap: GoogleMap
    private var polyline: Polyline? = null

    //current
//    lateinit var currentLocation: Location
    lateinit var currentlocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101


    private lateinit var binding: ActivityRouteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRouteBinding.inflate(layoutInflater)
        setContentView(binding.root)



        initView()       }

    private fun initView() {

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationProviderClient =  LocationServices.getFusedLocationProviderClient(this@RouteActivity)

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), permissionCode)
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener {
            if (it != null) {
                currentlocation = it
                Toast.makeText(applicationContext, currentlocation.latitude.toString() + "" +
                        currentlocation.longitude, Toast.LENGTH_SHORT).show()
                val supportMapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)!!
                supportMapFragment.getMapAsync(this@RouteActivity)


                myLocation = LatLng(currentlocation.latitude, currentlocation.longitude)

                Log.e(
                    "TAG",
                    "latitude:-  " + currentlocation.latitude + " " + "longitude:- " + currentlocation.longitude
                )


                // on below line we are adding marker to that position.
                addedMarker =
                    mMap.addMarker(MarkerOptions().position(myLocation).title("location"))!!

                // below line is to animate camera to that position.
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 5f))
            }
        }
        mapFragment!!.getMapAsync(this)


    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

//        val latLng = LatLng(currentlocation.latitude, currentlocation.longitude)
//        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
//        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
//        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5f))
//        googleMap?.addMarker(markerOptions)

        // adding on query listener for our search view.
        binding.secondSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {

                val secondLocation = binding.secondSearchView.query.toString()

                var addressList: List<Address>? = null

                if (secondLocation != null || secondLocation == "") {
                    // on below line we are creating and initializing a geo coder.
                    val geocoder = Geocoder(this@RouteActivity)
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