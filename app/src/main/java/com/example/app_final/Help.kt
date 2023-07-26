package com.example.app_final

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView

class Help : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        data class Item(
            val title: String,
            val desc: String
        )

        val descriptionOne = "You can change your location in the settings activity. Choose from a list of locations and return to the main activity to see your changes applied."
        val descriptionTwo = "You can customize your desired forecast location, your name, and the metric you'd like your forecasts displayed in (either F or C) in the Settings activity."
        val descriptionThree = "All data in this application is provided by the OpenWeatherMap API free tier."
        val descriptionFour = "This application gives users a weather forecast for their selected location formatted to their liking. First, a user enters their name and chooses whether to give their permission" +
                " to track their location. After that, you can "

        val itemArr = arrayOf(
            Item("What does this app do?", descriptionFour),
            Item("How do I change my location?", descriptionOne),
            Item("What can I customize?", descriptionTwo),
            Item("Where do you get this data?", descriptionThree)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemArr.map { it.title })
        val descTextView = findViewById<TextView>(R.id.descTextView)
        val titleListView = findViewById<ListView>(R.id.titleListView)
        titleListView.adapter = adapter

        descTextView.text = itemArr[0].desc

        titleListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            if(position < itemArr.size) {
                descTextView.text = itemArr[position].desc
            }
        }

        val settingsBtn = findViewById<Button>(R.id.settingsBtn)
        val forecastBtn = findViewById<Button>(R.id.forecastBtn)
        forecastBtn.setOnClickListener{ startActivity(Intent(this, WeatherForecast::class.java)) }
        settingsBtn.setOnClickListener{ startActivity(Intent(this, Settings::class.java)) }
    }
}