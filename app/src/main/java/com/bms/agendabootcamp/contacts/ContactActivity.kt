package com.bms.agendabootcamp.contacts

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bms.agendabootcamp.Camera.CameraActivity
import com.bms.agendabootcamp.Maps.MapsActivity
import com.bms.agendabootcamp.R
import com.bms.agendabootcamp.agenda.MainActivity
import com.bms.agendabootcamp.photo.PhotoActivity


class ContactActivity : AppCompatActivity() {

    val REQUEST_CONTACT = 1
    val LINEAR_LAYOUT_VERTICAL = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)

        //condição de permissão de acesso a contatos
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            //se nao, pedir permissao
            != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CONTACT)
        } else {//se permitido, insira os cotantos
            setContacts()
        }
    }

    //resultado da permissao > REQUEST_CONTACT
    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray)
    {
        if (requestCode == REQUEST_CONTACT)
            setContacts()
    }

    private fun setContacts() {
        //lista de contatos
        val contactList: ArrayList<Contact> = ArrayList()
                                            //conteiner dos dados comuns dos contatos do telefone
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            "display_name"
        )

        if (cursor != null) {
            //se o cursor não for vazio, adicione a lista Contact (string name, string phone)
            while (cursor.moveToNext()) {
                contactList.add(Contact(
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                        cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

                ))
            }
            cursor.close()
        }
        val adapter = ContactsAdapter(contactList)
        val contactRecyclerView = findViewById<RecyclerView>(R.id.contacts_rv)

        contactRecyclerView.layoutManager = LinearLayoutManager(
                this,
                LINEAR_LAYOUT_VERTICAL,
                false
        )

        contactRecyclerView.adapter = adapter
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
                Toast.makeText(this, "Voce já esta aqui", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.item_menu2 -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
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