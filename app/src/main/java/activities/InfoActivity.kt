package activities

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityInfoBinding
import timber.log.Timber
import timber.log.Timber.i
import main.MainApp


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
        }

        override fun onCreateOptionsMenu(menu: Menu): Boolean {
            menuInflater.inflate(R.menu.menu_info, menu)
            return super.onCreateOptionsMenu(menu)
        }



    }