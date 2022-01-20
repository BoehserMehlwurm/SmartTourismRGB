package activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityInfoBinding
import timber.log.Timber
import timber.log.Timber.i
import main.MainApp
import models.PlacemarkModel


class InfoActivity : AppCompatActivity() {

        private lateinit var binding: ActivityInfoBinding
        lateinit var app : MainApp

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            binding = ActivityInfoBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.toolbar.title = title
            setSupportActionBar(binding.toolbar)


            app = application as MainApp

            Timber.i("Info Page visited...")

            supportActionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowHomeEnabled(true)
                //Back Button to StartActivity
            }


            var test = app.placemarks.findAll().get(0).description

            /**
             * Testing of addressing variables over other files


            var placemarker = app.placemarks.findAll().get(0).address

            val tv_dynamic = TextView(this)
            tv_dynamic.textSize = 30f
            tv_dynamic.text = placemarker

            // add TextView to LinearLayout
            binding.root.addView(tv_dynamic)
             */

        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_info, menu)
            return super.onCreateOptionsMenu(menu)
        }



    }