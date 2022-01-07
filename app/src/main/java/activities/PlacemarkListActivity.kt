package activities

import adapters.PlacemarkAdapter
import adapters.PlacemarkListener
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smarttourismrgb.R
import com.example.smarttourismrgb.databinding.ActivityPlacemarkListBinding
import com.example.smarttourismrgb.databinding.CardPlacemarkBinding
import main.MainApp
import models.PlacemarkModel

class PlacemarkListActivity : AppCompatActivity(), PlacemarkListener {

    lateinit var app: MainApp
    private lateinit var binding: ActivityPlacemarkListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlacemarkListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = title
        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
            //Back Button to StartActivity
        }



        app = application as MainApp

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = PlacemarkAdapter(app.placemarks.findAll(),this)
    }




    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_placemarklist, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_add -> {
                val launcherIntent = Intent(this, PlacemarkActivity::class.java)
                //startActivity(launcherIntent) // see comment underneath at onPlacemarkClick
                refresh.launch(launcherIntent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPlacemarkClick(placemark: PlacemarkModel) {
        val launcherIntent = Intent(this, PlacemarkActivity::class.java)
        launcherIntent.putExtra("placemark_edit", placemark) //through parcelable the clicked placemark goes to Placemarkactivity
        //startActivity(launcherIntent) //should be startActivityForResult, but is deprecated. Used startActivity till I fixed it with getResult underneath
        refresh.launch(launcherIntent)
    }

    private val refresh = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        {if(it.resultCode == Activity.RESULT_OK) {
            binding.recyclerView.adapter?.notifyDataSetChanged()} //updates the view if code result is OK
           //val value = it.data?.getStringExtra("input")} //got the code from a MongoDB Articel about the deprecated onActivityResult
            //effort was not needed, changed in the labs as well.
        }

    //override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
       // binding.recyclerView.adapter?.notifyDataSetChanged()
        //super.onActivityResult(requestCode, resultCode, data) }
}


