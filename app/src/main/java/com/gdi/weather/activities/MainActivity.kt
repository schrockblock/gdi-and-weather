package com.gdi.weather.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.android.volley.RequestQueue
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.gdi.weather.R
import com.gdi.weather.models.WeatherCondition
import com.google.gson.Gson
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    var fetchButton: Button? = null
    var woeidEditText: EditText? = null
    var tempTextView: TextView? = null

    var queue: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        queue = Volley.newRequestQueue(this)

        fetchButton = findViewById(R.id.btn_fetch)
        woeidEditText = findViewById(R.id.et_place)
        tempTextView = findViewById(R.id.tv_temp)

        fetchButton?.setOnClickListener {
            fetchWeather()
        }
    }

    fun fetchWeather() {
        val woeid = woeidEditText?.text
        val url = "https://query.yahooapis.com/v1/public/yql?q=" +
                "select%20item.condition%20from%20weather.forecast%20where%20woeid%20=%20" +
                woeid + "&format=json"
        val request = JsonObjectRequest(url, null, { response: JSONObject? ->
            val conditionsObject = response?.getNestedObject("query", "results",
                    "channel", "item", "condition")
            val condition: WeatherCondition? =
                    Gson().fromJson(conditionsObject.toString(), WeatherCondition::class.java)
            tempTextView?.text = condition?.temp
        }, { error: VolleyError? ->
            error?.printStackTrace()
        })
        queue?.add(request)
    }
}

fun JSONObject.getNestedObject(vararg keys: String): JSONObject {
    if (keys.count() == 0) {
        return this
    } else {
        val json = this.getJSONObject(keys.first())
        val nextKeys = keys.drop(1).toTypedArray()
        return json.getNestedObject(*nextKeys)
    }
}
