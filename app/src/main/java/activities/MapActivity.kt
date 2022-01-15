package activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import models.Locationsave
import timber.log.Timber
import timber.log.Timber.*
import java.io.IOException

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    var location = Locationsave()
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())

        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_map)
        location= intent.extras?.getParcelable<Locationsave>("location")!!

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnSet.setOnClickListener() {
            i("Set Position Button Pressed")
            i("inside Map / Set Position Location"+location.lat.toString())
            val resultIntent = Intent()
            resultIntent.putExtra("location", location)
            setResult(Activity.RESULT_OK, resultIntent)
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
        map.setOnMarkerDragListener(this)
        map.setOnMarkerClickListener(this)


        getCurrentLocation {
            val pos = CameraPosition.fromLatLngZoom(it.latLng, 12f)
            map.moveCamera(CameraUpdateFactory.newCameraPosition(pos))
        }

    }

    private fun getCurrentLocation(onSuccess: (Location) -> Unit) {
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

                i("Map Current Locaton"+currentLatLng.toString())
                map.addMarker(options)
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