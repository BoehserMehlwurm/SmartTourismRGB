package activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.*
import com.squareup.picasso.Picasso
import main.MainApp
import models.Locationsave
import models.PlacemarkModel
import timber.log.Timber
import timber.log.Timber.*




class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    var location = Locationsave()
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //lateinit var app: MainApp
    var placemarker = PlacemarkModel()




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        plant(DebugTree())

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_map)

        if(intent.hasExtra("location")) {
            location = intent.extras?.getParcelable<Locationsave>("location")!!
        }else{
            //placemark = intent.extras?.getParcelable<PlacemarkModel>("placemark")!!
        }


        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnSet.setOnClickListener() {
            i("Set Position Button Pressed")
            i("inside Map / Set Position Location"+location.lat.toString())
            val resultIntent = Intent()
            resultIntent.putExtra("location", location)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        /**
        val loc = LatLng(location.lat, location.lng)
        val options = MarkerOptions()
            .title("Placemark")
            .snippet("GPS : $loc")
            .draggable(true)
            .position(loc)
         * Implemented in the currentLocation to set an inital Marker at the current Location.
         * and not an explicit place
        */

        //map.addMarker(options)
        //map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, location.zoom))
        if(intent.hasExtra("location")) {
        map.setOnMarkerDragListener(this)
        map.setOnMarkerClickListener(this)
        }

        //setPlacemarks(app.placemarks.findAll())

        getCurrentLocation()
        setPlacemarks()

    }


    private fun setPlacemarks() {

        val app: MainApp = application as MainApp

        /**
        val markerlist = app.placemarks.findAll()
        for (point in markerlist) {
            map.addMarker(MarkerOptions().position(LatLng(point.lat, point.lng)))
        }*/

        app.placemarks.findAll().forEach {
            //val placemarkLatLng = LatLng(49.006963935696014, 12.091747932136057)


            val options = MarkerOptions()
                .title(it.title)
                .snippet(it.address)
                .position(LatLng(it.lat, it.lng))
            map.addMarker(options)
        }


        //var list = app.placemarks.findAll()
        /**


        val placemarkLatLng = LatLng(placemark.lat, placemark.lng)
        val options = MarkerOptions()
        .title(placemark.title)
        .position(placemarkLatLng)
        map.addMarker(options)



        var m1: Marker? = map.addMarker(
        MarkerOptions()
        .position(LatLng(49.006963935696014, 12.091747932136057))
        .title(app.placemarks.findAll().get(0).address)
        .snippet("Snippet1")

        ) */


        /**
        val placemarkLatLng = LatLng(placemarker.get(0).lat, placemarker.get(0).lng)

        val options = MarkerOptions()
        .title(placemarker.get(0).address)
        .position(placemarkLatLng)

        map.addMarker(options)



        val placemarker = app.placemarks.findAll()


        placemark.title.forEach {
        val placemarkLatLng = LatLng(49.006963935696014, 12.091747932136057)

        val options = MarkerOptions()
        .title(it.title)
        .snippet(it.address)
        .position(placemarkLatLng)

        map.addMarker(options)
        }

         */

        /**
        val placemarkLatLng = LatLng(placemarkmarker.get(0).lat, placemarkmarker.get(0).lng)

        val options = MarkerOptions()
        .title(placemarkmarker.get(0).title)
        .snippet(placemarkmarker.get(0).address)
        .position(placemarkLatLng)
        map.addMarker(options)
         */


    }



    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        map.isMyLocationEnabled = true

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                //map.animate would be possible to. Then it will give the zoom feeling

                //next is to set the initalmarker to the current Location. Therefore it is not hard coded to Regensburg
                //val initalmarker = LatLng(currentLatLng.latitude, currentLatLng.longitude)

                val options = MarkerOptions()
                    .title("Placemark")
                    .snippet(currentLatLng.toString())
                    .draggable(true)
                    .position(currentLatLng)
                map.addMarker(options)

                 i("Map Current Locaton"+currentLatLng.toString())

            }
        }.addOnFailureListener{
            Timber.i("Location not found")
        }
    }



    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onMarkerDrag(marker: Marker) {
    }

    override fun onMarkerDragEnd(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
        location.address = getAddress(marker)
        Timber.i(location.lat.toString()+location.lng.toString())
    }

    override fun onMarkerDragStart(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
        location.address = getAddress(marker)
        //val adress = getAdress(location)
        //location.title = (adress)

    }

    override fun onBackPressed() {

        i("inside Map / onBackPressed Location"+location.lat.toString())

        val resultIntent = Intent()
        resultIntent.putExtra("location", location)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
        super.onBackPressed()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        //val loc = LatLng(location.lat, location.lng)
        //marker.snippet = "GPS : $loc"

        //val loc = LatLng(location.lat, location.lng)

        //marker.snippet = "GPS : ${getAddress()}"
        getAddress(marker)
        return false
    }

    private fun getAddress(marker: Marker): String {

        val geocoder = Geocoder(this)
        val addresses: List<Address> = geocoder.getFromLocation(location.lat, location.lng, 1) //get the Location from the Geocoding API
        val addressText = addresses[0].getAddressLine(0) //+ addresses[1].getAddressLine(1) //put it into the String

        marker.snippet = addressText //put the address text into the snippet of the marker via the location data model

        /**
        val city: String = addresses[0].getLocality()
        val state: String = addresses[0].getAdminArea()
        val country: String = addresses[0].getCountryName()
        val postalCode: String = addresses[0].getPostalCode()
        val knownName: String =
        addresses[0].getFeatureName() // Only if available else return NULL
         */

    return addressText
    }

}