package activities

import adapters.PlacemarkAdapter
import adapters.PlacemarkListener
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.smarttourismrgb.databinding.ActivityRouteListBinding
import com.google.android.gms.maps.model.LatLng

import main.MainApp
import models.Locationsave
import models.PlacemarkModel
import models.Routing
import timber.log.Timber.i


class RouteListActivity: AppCompatActivity(), PlacemarkListener {
    lateinit var app: MainApp
    private lateinit var binding: ActivityRouteListBinding
    private lateinit var maprouteIntentLauncher: ActivityResultLauncher<Intent>
    private var startinglatlng: LatLng? = null
    private var destinationlatlng: LatLng? = null
    var placemark = PlacemarkModel()
    var logtag = "ROUTELIST"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRouteListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        i("$logtag visited...")

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            //Back Button to StartActivity
        }

        app = application as MainApp



        binding.startrouteBtn.setOnClickListener {
            //val tag = "startBtn"
            val location = Locationsave(49.01, 12.10, 15f)
            /*
            if (placemark.zoom != 0f) {
                location.lat = placemark.lat
                location.lng = placemark.lng
                location.zoom = placemark.zoom
                location.address = placemark.address
            }*/

            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("startroute", location)
            maprouteIntentLauncher.launch(launcherIntent)
            i("$logtag start Test")


        }

        binding.destinationrouteBtn.setOnClickListener {
            //val tag = "destinationBtn"

            val location = Locationsave(49.01, 12.10, 15f)

            /*
            if (placemark.zoom != 0f) {
                location.lat = placemark.lat
                location.lng = placemark.lng
                location.zoom = placemark.zoom
                location.address = placemark.address
                location.tag = 1
            }*/


                val launcherIntent = Intent(this, MapActivity::class.java)
                    .putExtra("endroute", location)
                maprouteIntentLauncher.launch(launcherIntent)
            i("$logtag Tapped Start Location Route")


        }


        //noch einbauen, dass der Button erst funktioniert wenn die var gef??llt sind }


        binding.beginrouteBtn.setOnClickListener() {

            val routingAR = Routing(routestartlatlng = startinglatlng, routedestinationlatlng = destinationlatlng)

            i("AR Experience pressed")
            val intent = Intent(this, ArActivity::class.java)
                .putExtra("routingAR", routingAR)
            startActivity(intent)
        }


        registerMapCallback() //tag
        loadPlacemarks()

    }


    private fun loadPlacemarks() {
        showPlacemarks(app.placemarks.findAll())
    }

    fun showPlacemarks(placemarks: List<PlacemarkModel>) {
        binding.recyclerView.adapter = PlacemarkAdapter(placemarks, this)
        binding.recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun onPlacemarkClick(placemark: PlacemarkModel) {
        TODO("Not yet implemented")
    }

    private fun registerMapCallback() {
        maprouteIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {

                            if(result.data!!.extras?.getParcelable<Locationsave>("startroute")!!.tag==1){
                                val location = result.data!!.extras?.getParcelable<Locationsave>("startroute")!!
                            i("Location == $location")
                            placemark.lat = location.lat
                            placemark.lng = location.lng
                            placemark.zoom = location.zoom
                            placemark.address = location.address

                            startinglatlng = LatLng(placemark.lat, placemark.lng)
                            binding.startinfo.text = placemark.address
                            }else{ //better with tag==2, but for the moment IO
                            val location = result.data!!.extras?.getParcelable<Locationsave>("endroute")!!
                            i("Location == $location")
                            placemark.lat = location.lat
                            placemark.lng = location.lng
                            placemark.zoom = location.zoom
                            placemark.address = location.address

                            destinationlatlng = LatLng(placemark.lat,placemark.lng)
                            binding.destinationinfo.text = placemark.address

                            binding.beginrouteBtn.visibility = View.VISIBLE //let see Start Button after Destination was filled in
                            }


                        } // end of if
                    }
                    RESULT_CANCELED -> {}
                    else -> {}
                }
            }
    }
}


