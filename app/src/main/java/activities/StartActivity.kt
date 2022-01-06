package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.smarttourismrgb.databinding.ActivityStartBinding
import main.MainApp
import timber.log.Timber
import timber.log.Timber.i

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding
    lateinit var app: MainApp


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarAdd.title = title
        setSupportActionBar(binding.toolbarAdd)

        Timber.plant(Timber.DebugTree())

        i("Smart Tourism App started...")

        //actionBar!!.title = "PlacemarkList Activity"

        binding.startApplication.setOnClickListener() {
            i("Start Button Pressed")
            val intent = Intent(this, PlacemarkListActivity::class.java)
            startActivity(intent)

        }
    }
}