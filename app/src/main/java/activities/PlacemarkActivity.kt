package activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityPlacemarkBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import helpers.showImagePicker
import main.MainApp
import models.Location
import models.PlacemarkModel
import timber.log.Timber.i

class PlacemarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlacemarkBinding
    var placemark = PlacemarkModel()
    lateinit var app : MainApp
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent> //embedding image choosing activity
    private lateinit var mapIntentLauncher: ActivityResultLauncher<Intent> //embedding google maps activity
    private var edit = false // flag for a placemark, will be changed when it goes in the edit aka hasExtra if

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        invalidateOptionsMenu()

        binding = ActivityPlacemarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)



        app = application as MainApp
        i("Placemark Activity started...")

        if (intent.hasExtra("placemark_edit")) { //block for editing a Placemark, comes from PlacemarkListActivity
            edit = true
            placemark = intent.extras?.getParcelable("placemark_edit")!!
            binding.btnAdd.setText(R.string.save_placemark)
            binding.placemarkTitle.setText(placemark.title)
            binding.description.setText(placemark.description)
            Picasso.get()
                .load(placemark.image)
                .into(binding.placemarkImage)
            if(placemark.image != Uri.EMPTY) {
                binding.chooseImage.setText(R.string.change_placemark_image) //use other string when there was a picture. Therefore change button to change
            }

        }

        binding.btnAdd.setOnClickListener() {
            placemark.title = binding.placemarkTitle.text.toString()
            placemark.description = binding.description.text.toString()
            if (placemark.title.isEmpty()) {
                Snackbar.make(it, R.string.enter_placemark_title, Snackbar.LENGTH_LONG)
                    .show()

            } else {
                if (edit) { //depending on the flag, do a update or create a new placemark
                    app.placemarks.update(placemark.copy())
                } else {
                    app.placemarks.create(placemark.copy())
                }
            }
            setResult(RESULT_OK)
            finish()
        }

        binding.chooseImage.setOnClickListener {
            showImagePicker(imageIntentLauncher)

        }
        registerImagePickerCallback()

        binding.placemarkLocation.setOnClickListener{
            val location = Location(49.01, 12.10, 15f)
            if(placemark.zoom != 0f) {
                location.lat =  placemark.lat
                location.lng = placemark.lng
                location.zoom = placemark.zoom
            }
            val launcherIntent = Intent(this, MapActivity::class.java)
                .putExtra("location", location)
            mapIntentLauncher.launch(launcherIntent)
        }
        registerMapCallback()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_placemark, menu)

        if(edit == true){
            menu.findItem(R.id.item_delete).isVisible = true
            //val menuItem = menu.findItem(R.id.item_delete) does it as well
            //menuItem.setVisible(true)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //cancel the thing
        when (item.itemId){
            R.id.item_cancel -> {
                finish()
            }
        }
        when(item.itemId){
            R.id.item_delete -> {
                app.placemarks.delete(placemark)
                finish()
            }
        }


        return super.onOptionsItemSelected(item)
    }


    private fun registerImagePickerCallback() {
        imageIntentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            when(result.resultCode){
                RESULT_OK -> {
                    if (result.data != null) {
                        i("Got Image ${result.data!!.data}")
                        placemark.image = result.data!!.data!!
                        Picasso.get()
                            .load(placemark.image)
                            .into(binding.placemarkImage)
                        binding.chooseImage.setText(R.string.change_placemark_image)
                    } // end of if
                }
                RESULT_CANCELED -> { } else -> { }
            }
        }
    }

    private fun registerMapCallback() {
        mapIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->
                when (result.resultCode) {
                    RESULT_OK -> {
                        if (result.data != null) {
                            i("Got Location ${result.data.toString()}")
                            val location = result.data!!.extras?.getParcelable<Location>("location")!!
                            i("Location == $location")
                            placemark.lat = location.lat
                            placemark.lng = location.lng
                            placemark.zoom = location.zoom
                        } // end of if
                    }
                    RESULT_CANCELED -> {}
                    else -> {}
                }
            }
    }

}