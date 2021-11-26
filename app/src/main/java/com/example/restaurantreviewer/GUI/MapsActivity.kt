package com.example.restaurantreviewer.GUI

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.restaurantreviewer.Model.Restaurant
import com.example.restaurantreviewer.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_maps.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val LOCATION_PERMISSION_REQUEST = 99
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var selectedRestaurant: Restaurant
    private val markerMap: HashMap<Marker, Restaurant> = HashMap()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        getLocationPermission()

        // Zoom over Denmark by default
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(55.5129136, 9.9206371), 6.5F))

        // Receive restaurants from other activities
        val restaurants = getRestaurants()

        // Zoom onto restaurant if there is only one
        if (restaurants.size == 1) {
            val restaurant = restaurants[0];
            mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        restaurant.latitude,
                        restaurant.longitude
                    ), 7.5F
                )
            )
        }
        for (restaurant in restaurants) {
            val marker = mMap.addMarker(
                MarkerOptions()
                    .position(
                        LatLng
                            (restaurant.latitude, restaurant.longitude)
                    )
                    .title(restaurant.name)
            )
            markerMap[marker] = restaurant
        }

        mMap.setOnMarkerClickListener { marker ->
            btnOpenLocation.isEnabled = true
            marker.showInfoWindow()
            btnOpenLocation.text = "Open ${marker.title}"
            val filtered = markerMap[marker]?.id
            selectedRestaurant = filtered?.let { getClickedRestaurant(it) }!!
            true
        }
    }

    /**
     * Gets filtered list of restaurants from MainActivity
     */
    private fun getRestaurants(): ArrayList<Restaurant> {
        val extras: Bundle = intent.extras!!
        return extras.getSerializable(getString(R.string.ALL_RESTAURANTS_INTENT)) as ArrayList<Restaurant>;
    }

    /**
     * Get the clicked restaurant
     */
    private fun getClickedRestaurant(id: Int): Restaurant {
        val restaurants = getRestaurants()
        return restaurants.single { r -> r.id == id }
    }

    /**
     * Send clicked restaurant to detail view
     */
    fun onClickOpenLocation(view: View) {
        val intent = Intent(this, RestaurantActivity::class.java)
        intent.putExtra(getString(R.string.RESTAURANT_DETAILS_INTENT), selectedRestaurant)
        intent.putExtra("FROM_ACTIVITY", "MAPS");
        startActivity(intent)
        Log.e("xyz", selectedRestaurant.toString())
    }

    /**
     * Go back to the previous activity
     */
    fun onClickBackFromMaps(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /**
     * Check permission of device location
     */
    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        }
    }

    /**
     * Ask for permission of device location and enables location
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode === LOCATION_PERMISSION_REQUEST) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                mMap.isMyLocationEnabled = true
            } else {
                Log.e("xyz", "Permission denied")
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}