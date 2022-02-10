package activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityStartBinding
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.squareup.picasso.Picasso
import main.MainApp
import models.Locationsave
import models.PlacemarkModel
import models.UriParser
import timber.log.Timber
import timber.log.Timber.i
import java.io.File
import java.lang.reflect.Type
import java.util.ArrayList

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    lateinit var app: MainApp
    var placemark = PlacemarkModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        Timber.plant(Timber.DebugTree())
        i("Smart Tourism App started...")



        binding.startApplication.setOnClickListener() {
            i("Start Button Pressed")
            val intent = Intent(this, PlacemarkListActivity::class.java)
            startActivity(intent)

        }

        binding.arexperience.setOnClickListener(){
            i("AR Experience pressed")
            val intent = Intent(this, ArActivity::class.java)
            startActivity(intent)
        }



        binding.seeplacemarksmap.setOnClickListener(){
            i("Show Placemarks pressed")

            val launcherIntent = Intent(this, MapActivity::class.java)
                startActivity(launcherIntent)



        }



    binding.initlist.setOnClickListener(){

        Toast.makeText(this, "List filled", Toast.LENGTH_LONG).show()

        app = application as MainApp
        i("Init Button pressed")
            placemark.title = "Init Landmark"
            placemark.description = "Test"
            placemark.lat = 49.00554404625103
            placemark.lng = 12.10002925246954

        app.placemarks.create(placemark.copy())

        // first try, should add a Init List of places. Other option is the Places Google API
            setResult(RESULT_OK)

        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { //inflate menu toolbar (kind of)
        menuInflater.inflate(R.menu.menu_start, menu) //reference to the xml in the menu folder
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //go to new activity
        when (item.itemId) {
            R.id.item_info -> { //reference to the ID of the Info Button declared in the menu_start
                val launcherIntent = Intent(this, InfoActivity::class.java)
                startActivity(launcherIntent)
            }
        }
            return super.onOptionsItemSelected(item)
        }


}


