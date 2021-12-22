
package bd.ac.ru.cse

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.squareup.picasso.Picasso
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.math.log

class MainActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")

    var City: String =""
    private val client = OkHttpClient()

    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btn_weather = findViewById(R.id.weather_btn) as Button
        val btn_location = findViewById(R.id.location_btn) as Button

        // set on-click listener
        btn_location.setOnClickListener {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
            getlocation()
        }

        // set on-click listener

        btn_weather.setOnClickListener {

            val editTextHello = findViewById(R.id.city_text) as EditText
            City = editTextHello.text.toString()


            if (City.isNullOrEmpty()){
                print("please give city name")
                Toast.makeText(this, "Please enter a City name.", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(this,City,Toast.LENGTH_SHORT).show()
                getWeather(City)
            }

        }

    }



    private fun getlocation() {

        val location_status: Boolean= locationEnabled()
        if(location_status == false){
            Toast.makeText(applicationContext, "Please Eneable Location", Toast.LENGTH_LONG).show()
            return
        }
        try {
            val task =  fusedLocationProviderClient.lastLocation

            if (ActivityCompat.shouldShowRequestPermissionRationale(this@MainActivity,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            } else {
                ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 1)
            }

            task.addOnSuccessListener {
                if (it != null) {
                    Toast.makeText(
                        applicationContext,
                        "${it.latitude} ${it.longitude}",
                        Toast.LENGTH_LONG
                    ).show()
                }
                val lat: Double = it.latitude
                val lon: Double = it.longitude
                findViewById<TextView>(R.id.latitude_text).text = it.latitude.toString()
                findViewById<TextView>(R.id.longitude_text).text = it.longitude.toString()


                val geocoder = Geocoder(this)
                val list = geocoder.getFromLocation(lat, lon, 1)

                val city_name = list[0].locality
                val adress = findViewById(R.id.city_text) as EditText
                adress.setText(city_name)


                City = list[0].locality

            }
        }catch (exception: Exception) {
            Toast.makeText(applicationContext, "Please Eneable Location", Toast.LENGTH_LONG).show()
        }
    }

    private fun locationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        return gpsStatus
    }



    private fun getWeather(cityName : String)
    {
        var url = "https://api.openweathermap.org/data/2.5/weather?q=${cityName}&units=metric&appid=5bf963ea904e482e1e5f466a65347b06"
        //var url = api.openweathermap.org/data/2.5/find?lat={lat}&lon={lon}&cnt={cnt}&appid={API key}
        val request = Request.Builder()
            .url(url)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}
            override fun onResponse(call: Call, response: Response) = try {
                var jsonString = response.body?.string()

                val obj = JSONObject(jsonString)

                val jsonArray: JSONArray = obj.getJSONArray("weather")
                val weatherJsonObject : JSONObject = jsonArray.getJSONObject(0)
                val description : String = weatherJsonObject.getString("description")
                val weatherIcon : String = weatherJsonObject.getString("icon")

                val main_data: JSONObject = JSONObject(jsonString).getJSONObject("main")
                val temp = main_data.getString("temp")
                val humidity = main_data.getString("humidity")

                var str = "Description : $description\nTemprature : $temp °C\n Humidity : $humidity\n"

                val text_weather = findViewById(R.id.Show_weather) as TextView
                text_weather.setText(str)

                runOnUiThread{
                    val weatherImage : ImageView = findViewById<ImageView>(R.id.weather_icon)
                    Picasso.get().load("https://openweathermap.org/img/wn/${weatherIcon}@2x.png").into(weatherImage)
                    weatherImage.visibility = View.VISIBLE
                }

            }catch (exception: Exception) {
                Toast.makeText(applicationContext, "Please Enter Correct City", Toast.LENGTH_LONG).show()
            }
        })
    }

    fun exit_app(view: android.view.View) {
        System.exit(0)
    }


}
