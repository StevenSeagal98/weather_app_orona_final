package com.example.app_final

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.os.AsyncTask
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import android.content.Context
import android.content.Intent
import android.provider.Settings.Global
import android.util.TypedValue
import android.widget.Button
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class WeatherForecast : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather_forecast)

        val settingsBtn = findViewById<Button>(R.id.settings_btn)
        val helpBtn = findViewById<Button>(R.id.help_btn)

        settingsBtn.setOnClickListener{
            startActivity(Intent(this, Settings::class.java))
        }

        helpBtn.setOnClickListener{
            startActivity(Intent(this, Help::class.java))
        }

        val globalName = GlobalVariables.name
        val globalLat = GlobalVariables.userLat
        val globalLong = GlobalVariables.userLong
        val globalLocationPref = GlobalVariables.userPreferredLocation
        val globalUnits = GlobalVariables.userMetricPreference

        val welcomeText = findViewById<TextView>(R.id.forecastWelcome)
        //welcomeText.text = "Welcome $globalName"

        val apiStr = "ae79f44407a13edcb7b72adfd53b9334"
        var qString = "https://api.openweathermap.org/data/2.5/forecast?appid=$apiStr&units="
        //add unit
        qString += if(globalUnits.isNullOrEmpty()) "imperial" else globalUnits
        //add location or direct to settings activity
        if(globalLocationPref == "My Location") {
            if(globalLat !== "" && globalLong !== "") {
                qString += "&lat=$globalLat&lon=$globalLong"
            } else {
                qString += "&q=chicago"
            }
        } else {
            qString += "&q=${globalLocationPref}"
        }

        Log.i("Qstring: ", qString)

        welcomeText.text = "Welcome $globalName"

        val weatherApiClient = WeatherForecastApiClient(qString)
        val forecastDays = 3

        val weatherCallback = object : WeatherForecastApiClient.WeatherForecastCallback {
            override fun onSuccess(weatherForecast: JSONObject) {
                fun dpToPx(dp: Int, context: Context): Int {
                    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
                }
                fun capitalizeWords(input: String): String {
                    return input.split(" ").joinToString(" ") { it.capitalize() }
                }

                val cardViewPadding = dpToPx(20, this@WeatherForecast)
                val cardViewMargin = dpToPx(20, this@WeatherForecast)
                val cardViewElevation = dpToPx(4, this@WeatherForecast)
                val cardViewRadius = dpToPx(8, this@WeatherForecast)

                val container = findViewById<LinearLayout>(R.id.container)

                val jsonArr = JSONArray(weatherForecast.getString("list"))

                val cityObj = JSONObject(weatherForecast.getString("city"))
                val city = cityObj.getString("name")
                val sunriseTimestamp = cityObj.getString("sunrise")
                val sunsetTimestamp = cityObj.getString("sunset")

                var dateStr: String = ""
                fun extractDatePart(inputDateTime: String): String {
                    return inputDateTime.split(" ")[0]
                }
                //jsonArr.length()
                val totalCards: Int = GlobalVariables.numDays
                var cardCount: Int = 0
                for(i in 0 until jsonArr.length()) {
                    //Use date str as unique identifier to render cards
                    val entryObj = jsonArr.getJSONObject(i)
                    val weather = entryObj.getString("weather")
                    val time = entryObj.getString("dt_txt")
                    val ex: String = extractDatePart(time)

                    if(dateStr != ex) {
                        if(cardCount < totalCards) {
                            val main = entryObj.getString("main")
                            val mainObj = JSONObject(main)
                            val weatherArr = JSONArray(weather)
                            val weatherObj = weatherArr.getJSONObject(0)
                            val temp = mainObj.getString("temp")

                            //Weather
                            val clouds = capitalizeWords(weatherObj.getString("description"))

                            val linearLayout = LinearLayout(this@WeatherForecast)
                            linearLayout.orientation = LinearLayout.VERTICAL

                            val cardView = CardView(this@WeatherForecast)
                            cardView.cardElevation = cardViewElevation.toFloat()
                            cardView.radius = cardViewRadius.toFloat()

                            val layoutParams = LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                            )
                            layoutParams.setMargins(10,10,10,cardViewMargin)
                            cardView.setPadding(cardViewPadding, cardViewPadding, cardViewPadding, cardViewPadding)
                            cardView.layoutParams = layoutParams

                            val textView = TextView(this@WeatherForecast)
                            val cloudsTextView = TextView(this@WeatherForecast)
                            val timeTextView = TextView(this@WeatherForecast)
                            val sunsetTextView = TextView(this@WeatherForecast)
                            val sunriseTextView = TextView(this@WeatherForecast)

                            fun formatDateFromUnix(timeStamp: String): String? {
                                val instant = Instant.ofEpochSecond(timeStamp.toLong())
                                val zoneId = ZoneId.systemDefault()
                                val localDateTime = LocalDateTime.ofInstant(instant, zoneId)
                                val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
                                return localDateTime.format(formatter)
                            }

                            val formatSunrise = formatDateFromUnix(sunriseTimestamp)
                            val formatSunset = formatDateFromUnix(sunsetTimestamp)

                            sunriseTextView.text = "Sunrise: $formatSunrise"
                            sunsetTextView.text = "Sunset: $formatSunset"
                            val unitText: String = if(GlobalVariables.userMetricPreference == "metric") "C" else "F"
                            textView.text = "Temp: $temp $unitText"
                            cloudsTextView.text = clouds
                            timeTextView.text = "$city: $time"

                            val textViewArr = arrayOf(textView, cloudsTextView, timeTextView, sunriseTextView, sunsetTextView)
                            textViewArr.forEach {textView ->
                                textView.layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                                textView.setPadding(cardViewPadding, cardViewPadding,cardViewPadding,cardViewPadding)
                                linearLayout.addView(textView)
                            }
                            cardView.addView(linearLayout)
                            container.addView(cardView)
                            dateStr = extractDatePart(time)
                            Log.i("Setting dateStr: ", dateStr)
                            cardCount++
                        }
                    }
                }
            }

            override fun onError(errorMessage: String) {
                Log.i("Couldn't get it: ", errorMessage)
            }
        }
        weatherApiClient.getWeatherForecast(forecastDays, weatherCallback)
    }
}

class WeatherForecastApiClient(private val qString: String) {
    fun getWeatherForecast(forecastDays: Int, callback: WeatherForecastCallback) {
        val url = qString
        Log.i("Querying for: ", qString)
        val apiRequestTask = object: AsyncTask<String, Void, JSONObject?>() {
            override fun doInBackground(vararg urls: String): JSONObject? {
                val urlString = urls[0]
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"

                val responseCode = connection.responseCode
                if(responseCode == HttpURLConnection.HTTP_OK) {
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = StringBuilder()
                    var line: String? = reader.readLine()
                    while(line != null) {
                        response.append(line)
                        line = reader.readLine()
                    }
                    reader.close()
                    val jsonResponse = response.toString()
                    return JSONObject(jsonResponse)
                } else {
                    callback.onError("Error: $responseCode")
                }
                return null
            }

            override fun onPostExecute(result: JSONObject?) {
                if(result != null) {
                    val filteredForecast = filterForecastByDays(result, forecastDays)
                    callback.onSuccess(filteredForecast)
                }
            }
        }
        apiRequestTask.execute(url)
    }

    private fun filterForecastByDays(weatherForecast: JSONObject, forecastDays: Int): JSONObject {
        val list = weatherForecast.getJSONArray("list")
        val filteredList = mutableListOf<JSONObject>()

        val currentTime = System.currentTimeMillis() / 1000
        val targetTime = currentTime + forecastDays * 24 * 60 * 60

        for (i in 0 until list.length()) {
            val forecastItem = list.getJSONObject(i)
            val timestamp = forecastItem.getLong("dt")
            if (timestamp <= targetTime) {
                filteredList.add(forecastItem)
            }
        }
        weatherForecast.put("list", filteredList)
        return weatherForecast
    }

    interface WeatherForecastCallback {
        fun onSuccess(weatherForecast: JSONObject)
        fun onError(errorMessage: String)
    }
}