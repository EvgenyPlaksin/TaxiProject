package com.example.taxiproject

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    var arrayList: ArrayList<String>? = null
    var dialog: Dialog? = null
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap
    lateinit var lastLocation: Location
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    companion object{
        private const val LOCATION_REQUESR_CODE = 1
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        val textview: TextView? = findViewById(R.id.testView)
        val languages = resources.getStringArray(R.array.Languages)
        arrayList = ArrayList()
        arrayList!!.add("DSA Self Paced")
        arrayList!!.add("Complete Interview Prep")
        arrayList!!.add("Amazon SDE Test Series")
        arrayList!!.add("Compiler Design")
        arrayList!!.add("Git & Github")
        arrayList!!.add("Python foundation")
        arrayList!!.add("Operating systems")
        arrayList!!.add("Theory of Computation")

        // --------------------------- Spinner --------------------------------------
        textview?.setOnClickListener(View.OnClickListener {
            // Initialize dialog
            dialog = Dialog(this@MainActivity)

            // set custom dialog
            dialog!!.setContentView(R.layout.dialog_searchable_spinner)

            // set custom height and width
            dialog!!.window!!.setLayout(650, 800)

            // set transparent background
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // show dialog
            dialog!!.show()

            // Initialize and assign variable
            val editText = dialog!!.findViewById<EditText>(R.id.edit_text)
            val listView = dialog!!.findViewById<ListView>(R.id.list_view)

            // Initialize array adapter
            val adapter =
                ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, arrayList!!)

            // set adapter
            listView.adapter = adapter
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable) {}
            })
            listView.onItemClickListener =
                AdapterView.OnItemClickListener { parent, view, position, id -> // when item selected from list
                    // set selected item on textView
                    textview.setText(adapter.getItem(position))

                    // Dismiss dialog
                    dialog!!.dismiss()
                }
        })
        // --------------------------- Date and Time --------------------------------------
        val textView = findViewById<TextView?>(R.id.dateTv)
        val textViewTime = findViewById<TextView?>(R.id.tvTime)
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        textView?.setOnClickListener {

            val dpd = DatePickerDialog(
                this,
                DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                    // Display Selected date in TextView
                    textView.setText("" + dayOfMonth + ", " + month + ", " + year)
                },
                year,
                month,
                day
            )
            dpd.show()

        }
        textViewTime?.setOnClickListener {
            val timesetListener =
                TimePickerDialog.OnTimeSetListener { TimePicker: TimePicker, hour: Int, minute: Int ->
                    c.set(Calendar.HOUR_OF_DAY, hour)
                    c.set(Calendar.MINUTE, minute)
                    textViewTime.text = java.text.SimpleDateFormat("HH:mm").format(c.time)
                }
            TimePickerDialog(
                this,
                AlertDialog.THEME_HOLO_DARK,
                timesetListener,
                c.get(Calendar.HOUR_OF_DAY),
                c.get(Calendar.MINUTE),
                true
            ).show()
        }

        //----------------------------------Maps----------------------------
        // у меня так и не вышло сделать местоположение. Почему-то не видит разрешений на геолокацию
        val mapFragment = (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?)
        mapFragment?.getMapAsync(this)

    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.setOnMarkerClickListener(this)
        setupMap()
    }

    @SuppressLint("MissingPermission")
    private fun setupMap() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUESR_CODE)
                return
            }
        googleMap.isMyLocationEnabled = true
        fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
            if(location != null){
                lastLocation = location
                val currentLatLong = LatLng(location.latitude, location.longitude)
                placeMarkerOnMap(currentLatLong)
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }

    }

    private fun placeMarkerOnMap(currentLatLong: LatLng) {
        val markerOptions = MarkerOptions().position(currentLatLong)
        markerOptions.title("$currentLatLong")
        googleMap.addMarker(markerOptions)
    }

    override fun onMarkerClick(p0: Marker) = false
}
