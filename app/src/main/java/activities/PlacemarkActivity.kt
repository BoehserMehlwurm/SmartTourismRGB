package activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityPlacemarkBinding
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import helpers.showImagePicker
import main.MainApp
import models.PlacemarkModel
import timber.log.Timber
import timber.log.Timber.i

class PlacemarkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlacemarkBinding
    var placemark = PlacemarkModel()
    lateinit var app : MainApp
    private lateinit var imageIntentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPlacemarkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        var edit = false // flag for a placemark, will be changed when it goes in the edit aka hasExtra if

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
            // showImagePicker(imageIntentLauncher)
        }
        registerImagePickerCallback()


    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_placemark, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { //cancel the thing
        when (item.itemId){
            R.id.item_cancel -> {
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
                        i("Got Result ${result.data!!.data}")
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

}