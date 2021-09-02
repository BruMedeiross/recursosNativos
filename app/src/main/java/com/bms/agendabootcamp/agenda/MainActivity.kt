package com.bms.agendabootcamp.agenda

import android.content.Intent
import android.os.Bundle
import android.provider.CalendarContract
import android.provider.CalendarContract.Events.*
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bms.agendabootcamp.Camera.CameraActivity
import com.bms.agendabootcamp.Maps.MapsActivity
import com.bms.agendabootcamp.R
import com.bms.agendabootcamp.contacts.ContactActivity
import com.bms.agendabootcamp.photo.PhotoActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnSetEvent = findViewById<Button>(R.id.set_event)

        btnSetEvent.setOnClickListener {
            val intent = Intent(Intent.ACTION_INSERT)
                .setData(CONTENT_URI)
                .putExtra(TITLE, "bootcamp")
                .putExtra(EVENT_LOCATION, "on line")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis()+(60*60*1000))
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis()+(60*60*1000))
                //seta dados previos na agenda
                // titulo, local, hora de inicio e fim

            startActivity(intent)
        }



    }


    //inflando layout do menu :
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    //quando um dos itens for selecionado no menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.item_menu -> {
                val intent = Intent(this, ContactActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item_menu2 -> {
                Toast.makeText(this, "Voce já esta aqui", Toast.LENGTH_SHORT).show()
                true
            }
            
            R.id.item_menu3 -> {
                val intent = Intent(this, PhotoActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item_menu4 -> {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item_menu5 -> {
                val intent = Intent(this, MapsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item_menu6 -> {
                val intent = Intent(this, PhotoActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}