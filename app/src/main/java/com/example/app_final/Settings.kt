package com.example.app_final

import android.content.Intent
import android.os.Bundle
import android.provider.Settings.Global
import android.text.InputFilter
import android.text.Spanned
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class Settings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        fun showToast(msg: String) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }

        val forecastBtn = findViewById<Button>(R.id.forecastBtnSettings)
        val helpBtn = findViewById<Button>(R.id.helpBtn)
        helpBtn.setOnClickListener{ startActivity(Intent(this, Help::class.java)) }
        forecastBtn.setOnClickListener{
            var intent = Intent(this, WeatherForecast::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        val nameBtn = findViewById<Button>(R.id.settingsNameBtn)
        val cBtn = findViewById<RadioButton>(R.id.cBtn)
        val fBtn = findViewById<RadioButton>(R.id.fBtn)

        val daysBtn = findViewById<Button>(R.id.numDaysBtn)
        val daysInput = findViewById<EditText>(R.id.numDaysInput)
        class NumericInputFilter : InputFilter {
            override fun filter(
                source: CharSequence?,
                start: Int,
                end: Int,
                dest: Spanned?,
                dstart: Int,
                dend: Int
            ): CharSequence? {
                val regex = "[1-4]+".toRegex()
                return if (source?.matches(regex) == true) source else ""
            }
        }
        daysInput.filters = arrayOf(NumericInputFilter())

        daysBtn.setOnClickListener{
            val x = daysInput.text.toString().toInt()
            GlobalVariables.numDays = x
            showToast("Number of days for forecast changed successfully to $x")
        }

        nameBtn.setOnClickListener{
            val name = findViewById<EditText>(R.id.nameInputSettings).text.toString()
            GlobalVariables.name = name
            showToast("Name changed successfully to $name")

        }
        cBtn.setOnClickListener{
            GlobalVariables.userMetricPreference = "metric"
            showToast("Unit set to metric (C) successfully")
        }
        fBtn.setOnClickListener{
            GlobalVariables.userMetricPreference = "imperial"
            showToast("Unit set to imperial (F) successfully")
        }

        val spinner: Spinner = findViewById(R.id.spinner)
        val dropdownValues = arrayOf("My Location", "Chicago", "Los Angeles", "Paris", "New York", "London")

        val indexOfCurrent = dropdownValues.indexOf(GlobalVariables.userPreferredLocation)
        if(indexOfCurrent > -1) {
            spinner.setSelection(indexOfCurrent)
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dropdownValues)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, position: Int, id: Long) {
                val selectedValue = parent.getItemAtPosition(position) as String
                GlobalVariables.userPreferredLocation = selectedValue
                showToast("Preferred Location set to $selectedValue")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle the case when nothing is selected (optional)
                spinner.setSelection(dropdownValues.indexOf(GlobalVariables.userPreferredLocation))
            }
        }
        val nameInput = findViewById<EditText>(R.id.nameInputSettings)
        val numDaysInput = findViewById<EditText>(R.id.numDaysInput)
        val metricInput = findViewById<RadioButton>(R.id.fBtn)
        val fahrInput = findViewById<RadioButton>(R.id.cBtn)

        nameInput.setText(GlobalVariables.name)
        numDaysInput.setText(GlobalVariables.numDays.toString())
        if(GlobalVariables.userMetricPreference == "imperial") {
            fahrInput.isChecked = true
        } else {
            metricInput.isChecked = true
        }
    }
}