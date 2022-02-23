package activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.beust.klaxon.*
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityMapBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import main.MainApp
import models.Locationsave
import models.PlacemarkModel
import org.jetbrains.anko.async
import org.jetbrains.anko.uiThread
import timber.log.Timber.i
import java.net.URL


class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerDragListener, GoogleMap.OnMarkerClickListener{

    private lateinit var map: GoogleMap
    private lateinit var binding: ActivityMapBinding
    var location = Locationsave()
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    //lateinit var app: MainApp
    var placemarker = PlacemarkModel()

    private lateinit var fkip: LatLng
    private lateinit var monas: LatLng


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //setContentView(R.layout.activity_map)


        //handling the intents. Coming from PlacemarkActivity, StartActivity or RouteListActivity
        if(intent.hasExtra("location")) {
            location = intent.extras?.getParcelable<Locationsave>("location")!!
            Toast.makeText(this, "Select the location via Drag and Drop", Toast.LENGTH_LONG).show()}
        if(intent.hasExtra("startroute")){
            location = intent.extras?.getParcelable<Locationsave>("startroute")!!
            location.tag=1}
        if(intent.hasExtra("endroute")) {
            location = intent.extras?.getParcelable<Locationsave>("endroute")!!
            location.tag = 2}
        if(intent.extras==null){binding.btnSet.setText(R.string.back_map)}

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnSet.setOnClickListener() {
            i("Set Position Button Pressed")
            i("inside Map / Set Position Location"+location.lat.toString())
            val resultIntent = Intent()
            resultIntent.putExtra("location", location)
            resultIntent.putExtra("startroute", location)
            resultIntent.putExtra("endroute", location)
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
        if(intent.hasExtra("location")||intent.hasExtra("startroute")||intent.hasExtra("endroute")) {
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
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_blue))
                i("MapActivity: currentLatLng: $currentLatLng")

                if(intent.hasExtra("location")||intent.hasExtra("startroute")||intent.hasExtra("endroute")){
                    map.addMarker(options)
                }


                 i("Map Current Locaton"+currentLatLng.toString())

            }
        }.addOnFailureListener{
            i("Location not found")
        }
    }



    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onMarkerDrag(marker: Marker) {
        getAddress(marker)
    }

    override fun onMarkerDragEnd(marker: Marker) {
        location.lat = marker.position.latitude
        location.lng = marker.position.longitude
        location.zoom = map.cameraPosition.zoom
        location.address = getAddress(marker)
        i(location.lat.toString()+location.lng.toString())
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
        resultIntent.putExtra("startroute", location)
        resultIntent.putExtra("endroute", location)
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


