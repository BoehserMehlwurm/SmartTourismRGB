package activities

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import ar.PlaceNode
//import api.NearbyPlacesResponse
//import api.PlacesService
import ar.PlacesArFragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityArBinding
import com.example.smarttourismrgb.databinding.ActivityArBinding.inflate
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.Camera
import com.google.ar.sceneform.Scene
import com.google.ar.sceneform.math.Quaternion
import com.google.maps.android.PolyUtil
import main.MainApp
import models.PlacemarkModel
import org.json.JSONObject
import timber.log.Timber
//import models.Place
//import models.getPositionVector
import timber.log.Timber.*
import kotlin.math.*

class ArActivity : AppCompatActivity(), SensorEventListener {

    //private val TAG = "ArActivity"

    //private lateinit var placesService: PlacesService
    private lateinit var arFragment: PlacesArFragment
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var binding: ActivityArBinding

    // Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Sensor
    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var anchorNode: AnchorNode? = null
    //private var markers: MutableList<Marker> = emptyList<Marker>().toMutableList()
    //private var places: List<Place>? = null commented out cause of the unused place model
    //private var currentLocation: Location? = null
    private lateinit var map: GoogleMap
    //var location = Locationsave()
    private lateinit var lastLocation: Location
    //for the Sensor
    private var azimuth: Int = 0
    private var rotationV: Sensor? = null
    private var accelerometer: Sensor? = null
    private var magnetometer: Sensor? = null
    private var rMat = FloatArray(9)
    private var orientation = FloatArray(3)
    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false
    private var isArPlaced = false
    private lateinit var camera: Camera
    private lateinit var scene: Scene

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isSupportedDevice()) {
            return
        }


        binding = inflate(layoutInflater)
        setContentView(binding.root)


        arFragment = supportFragmentManager.findFragmentById(R.id.ar_fragment) as PlacesArFragment
        mapFragment = supportFragmentManager.findFragmentById(R.id.maps_fragment) as SupportMapFragment

        //sensorManager = getSystemService()!!
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        startSensor()

        //placesService = PlacesService.create()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //scene = arFragment.arSceneView.scene!!
        //camera = scene.camera


        setUpAr()
        setUpMaps()
    }

    override fun onResume() {
        super.onResume()
        startSensor()

    }

    override fun onPause() {
        super.onPause()
        stopSensor()
    }

    private fun setUpAr() {
            arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            val anchor = hitResult.createAnchor()
            anchorNode = AnchorNode(anchor)
            anchorNode?.setParent(arFragment.arSceneView.scene)
                i("Fehlersuche: On Screen tapped") // at the moment it will place them when tapped
                Toast.makeText(this@ArActivity, "Tapped, Compass: $azimuth", Toast.LENGTH_LONG).show()

                setPlacemarks(anchorNode!!)

        }
    }




    private fun showInfoWindow(place: PlacemarkModel) {
        // Show in AR

        val matchingPlaceNode = anchorNode?.children?.filter {
            it is PlaceNode
        }?.first {
            val otherPlace = (it as PlaceNode).placemark ?: return@first false //coming from placeNode
            return@first otherPlace == place
        } as? PlaceNode
        matchingPlaceNode?.showInfoWindow()

        // Show as marker
        /**
        val matchingMarker = app.placemarks.findAll().firstOrNull {
            val placeTag = (it.tag as? PlacemarkModel) ?: return@firstOrNull false
            return@firstOrNull placeTag == place
        }  //commented out cause of the unused place model
        matchingMarker?.showInfoWindow()
        */
    }


    private fun setUpMaps() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MapActivity.LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mapFragment.getMapAsync { googleMap ->
            googleMap.isMyLocationEnabled = true

            googleMap.setOnMarkerClickListener { marker ->


                marker.showInfoWindow()
                return@setOnMarkerClickListener true
            }
            map = googleMap

            getCurrentLocation()
            setPlacemarks(anchorNode)
        }

    }




    private fun setPlacemarks(anchorNode: AnchorNode?) {

        val app: MainApp = application as MainApp

        val markerlist = app.placemarks.findAll()
        for (point in markerlist) {
            map.addMarker(MarkerOptions().position(LatLng(point.lat, point.lng)))
        }

        markerlist.forEach {
            val options = MarkerOptions()
                .title(it.title)
                .snippet(it.address)
                .position(LatLng(it.lat, it.lng))
            map.addMarker(options)
        }


        // Till here place the placemarks onto the map section
        // Now place the placemark loactions onto the AR sight





        for (marker in markerlist) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->


                val markerLatLng = LatLng(marker.lat, marker.lng)


                val currentLocation = LatLng(lastLocation.latitude, lastLocation.longitude)



                val placeNode = PlaceNode(this, marker)
                placeNode.setParent(anchorNode)
                i("Fehlersuche: 1) setMarker ${marker.address} to $currentLocation (now comes getPositionVektor)")
                i("Fehlersuche: 1.1 currentLocation: $currentLocation, currentLatLng: $markerLatLng")
                //placeNode.localPosition = marker.getPositionVector(orientationAngles[0], currentLocation.latLng)//currentLocation.latLng)



                placeNode.worldPosition = Vector3(getPositionen(currentLocation, markerLatLng))
                i("ARCore: PlaceNode.worldposition ${placeNode.worldPosition}")

                val cameraPosition = arFragment.arSceneView.scene.camera.worldPosition
                i("ARCore: CameraPosition $cameraPosition")

                val direction = Vector3.subtract(cameraPosition, placeNode.worldPosition)
                i("ARCore: direction $direction")

                val lookRotation = Quaternion.lookRotation(direction, Vector3.up())
                i("ARCore: lookRotation $lookRotation")

                placeNode.worldRotation = lookRotation
                i("ARCore: placeNode.worldrotation ${placeNode.worldRotation}")

                //placeNode.localPosition = getPositionen(currentLocation, currentLatLng)

                placeNode.setOnTapListener { _, _ ->
                    showInfoWindow(marker)

                }

            }.addOnFailureListener {
                i("Location not found")
            }
        }
    }

    fun getPositionen(currentLatLng: LatLng, markerlatLng: LatLng): Vector3 {

        //val placeLatLng = this.geometry.latLng
        //i("inside getPositionVector, $placeLatLng")
        Timber.plant(Timber.DebugTree())
        i("MyLat/Lng $currentLatLng")
        i("TargetLat/Lng $markerlatLng")

        val lat1 = currentLatLng.latitude / 180 * PI
        val lng1 = currentLatLng.longitude / 180 * PI
        val lat2 = markerlatLng.latitude / 180 * PI
        val lng2 = markerlatLng.longitude / 180 * PI

        i("Lat 1: $lat1 Lat2: $lat2 Lng1: $lng1 $lng2")

        val deltaOmega = ln(tan((lat2 / 2) + (PI / 4)) / tan((lat1 / 2) + (PI / 4)))
        val deltaLongitude = (lng1 - lng2)

        val zuruck = atan2(deltaLongitude, deltaOmega)

        val degree = (360 - (zuruck * 180 / PI))
        val distant = 3.0

        val y = 0
        val x = distant * cos(PI * degree / 180)
        val z = 1 * distant * sin(PI * degree / 180)

        i("ARCore_MyLocation $currentLatLng")
        i("ARCore_TargetLocation $markerlatLng")
        i("ARCore_COMPASS $azimuth")
        i("ARCore_Degree $degree")
        i("ARCore_X $x")
        i("ARCore_Y $y")
        i("ARCore_Z $z")

        return Vector3(x.toFloat(), y.toFloat(), z.toFloat())
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                MapActivity.LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
                i("Map Current Locaton"+currentLatLng.toString())
            }
        }.addOnFailureListener{
            i("Location not found")
        }
    }



    private fun isSupportedDevice(): Boolean { //checks the device OpenGL ES Version
        val activityManager = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val openGlVersionString = activityManager.deviceConfigurationInfo.glEsVersion
        if (openGlVersionString.toDouble() < 3.0) {
            Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            finish()
            return false
        }
        return true
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values)
            azimuth = (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0].toDouble()) + 360).toInt() % 360
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
            lastAccelerometerSet = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
            lastMagnetometerSet = true
        }

        if (lastAccelerometerSet && lastMagnetometerSet) {
            SensorManager.getRotationMatrix(rMat, null, lastAccelerometer, lastMagnetometer)
            SensorManager.getOrientation(rMat, orientation)
            azimuth = (Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0].toDouble()) + 360).toInt() % 360
        }
    }

    private fun startSensor() {
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI)
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI)
        } else {
            rotationV = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            sensorManager.registerListener(this, rotationV, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun stopSensor() {
        sensorManager.unregisterListener(this, accelerometer)
        sensorManager.unregisterListener(this, magnetometer)
    }


}

val Location.latLng: LatLng
    get() = LatLng(this.latitude, this.longitude)

