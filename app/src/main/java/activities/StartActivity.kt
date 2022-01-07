package activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.example.smarttourismrgb.R
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