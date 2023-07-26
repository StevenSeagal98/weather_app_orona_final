package com.example.app_final

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.location.LocationListener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity(), LocationListener {

    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        val nameSubmitButton = findViewById<Button>(R.id.nameSubmitButton)
        checkLocationPermission()
        nameSubmitButton.setOnClickListener{
            val nameInputStr: Editable = findViewById<EditText>(R.id.nameInput).text
            var msg: String = "Please enter your name before submitting"

            if(nameInputStr.length > 0) {
                try {
                    val name = nameInputStr.toString()
                    GlobalVariables.name = name
                    msg = "Name Successfully Stored 🎉"
                    val intent = Intent(this, WeatherForecast::class.java)
                    startActivity(intent)
                } catch(e: UnsupportedOperationException) {
                    Log.i("Exception: ", e.toString())
                    msg = "Error storing name to shared preferences"
                }
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
    }

    private fun startLocationUpdates() {
        try {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000,
                10f,
                this
            )
        } catch(ex: SecurityException) {
            Log.i("Err: ", ex.toString())
        }
    }

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    private fun checkLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            startLocationUpdates()
        }
    }

    override fun onLocationChanged(location: Location) {
        GlobalVariables.userLat = location.latitude.toString()
        GlobalVariables.userLong = location.longitude.toString()
    }
}