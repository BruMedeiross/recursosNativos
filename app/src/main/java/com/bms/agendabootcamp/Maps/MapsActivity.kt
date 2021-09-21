package com.bms.agendabootcamp.Maps

import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.bms.agendabootcamp.Camera.CameraActivity
import com.bms.agendabootcamp.R
import com.bms.agendabootcamp.agenda.MainActivity
import com.bms.agendabootcamp.contacts.ContactActivity
import com.bms.agendabootcamp.photo.PhotoActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MapsActivity : AppCompatActivity(),
    OnMapReadyCallback,
    //marker ao ser clicado
    GoogleMap.OnMarkerClickListener {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location

    //callback e requerimento e estado da localizacao
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //criação da localizacao do user
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback(){
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)

                lastLocation = p0.lastLocation
                placeMarkerOnMap(LatLng(lastLocation.latitude, lastLocation.longitude))
            }
        }
        createLocationRequest()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Add a marker in Sydney and move the camera
        val myPlace = LatLng(-23.5489, -46.6388)
        map.addMarker(MarkerOptions().position(myPlace).title("Aqui é São Paulo - Marco Zero"))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(myPlace, 12.0f))


        map.getUiSettings().setZoomControlsEnabled(true)//zoom: botão + -
        map.getUiSettings().setMyLocationButtonEnabled(true)
        map.setOnMarkerClickListener(this) //mostra a opcao de abrir o google maps

        setUpMap()
    }

    private fun setUpMap() {
        //solicita a permissao de localização atual do usuario
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        map.isMyLocationEnabled = true


        //TIPO DE MAPA
        map.mapType = GoogleMap.MAP_TYPE_HYBRID

        //obtendo a localizacao atual
        fusedLocationClient.lastLocation.addOnSuccessListener(this){ location ->

            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLng)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    private fun placeMarkerOnMap(location : LatLng) {
        val markerOptions = MarkerOptions().position(location)

        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(
            BitmapFactory.decodeResource(resources, R.mipmap.ic_user_location)
        ))

        //marker com endereço completo
        val titleStr = getAddress(location)
        markerOptions.title(titleStr)

        // opção - texto simples
        // markerOptions.title("voce esta aqui")

        map.addMarker(markerOptions)
        //texto simples
        // map.addMarker(markerOptions.title("você esta aqui"))

    }

    private fun getAddress (latLng: LatLng) : String{
        val geocoder: Geocoder
        val addresses: List<Address>

        geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)

        val address = addresses[0].getAddressLine(0)
        val city = addresses[0].locality
        val state = addresses[0].adminArea
        val country = addresses[0].countryName
        val postalCode = addresses[0].postalCode
        return address
    }

    private fun startLocationUpdates() {
        //solicita a permissao de localização atual do usuario
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /*Looping de atualizacao*/)
    }

    private fun createLocationRequest(){

        locationRequest = LocationRequest()

        //velocidade, velocidade +rapida e prioridade do recebeimento de dados(ver teste)
        locationRequest.interval = 10000
        locationRequest.fastestInterval=5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)

        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener{
            locationUpdateState = true
            startLocationUpdates()
            //se as atualizacoes tiverem sucesso inicia as atualizacoes periodicas
        }

        task.addOnFailureListener{ e->

            if (e is ResolvableApiException){
                try{
                    e.startResolutionForResult(this@MapsActivity,
                    REQUEST_CHECK_SETTINGS)

                }catch (sendEx: IntentSender.SendIntentException){

                }
            }
        }
    }

    //para e retorma a atualização de acordo com o stado do app -respectivamente onPause onResume
    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    public override fun onResume() {
        super.onResume()
        if (!locationUpdateState){
            startLocationUpdates()
        }
    }



    override fun onMarkerClick(p0: Marker?) = false

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
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item_menu3 -> {
                val intent = Intent(this, PhotoActivity::class.java)
                true
            }
            R.id.item_menu4 -> {
                val intent = Intent(this, CameraActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.item_menu5 -> {
                Toast.makeText(this, "Voce já esta aqui", Toast.LENGTH_SHORT).show()
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


