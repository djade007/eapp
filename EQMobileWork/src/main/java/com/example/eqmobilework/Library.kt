package com.example.eqmobilework

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.eqmobilework.data.LocationEvent
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import java.util.*

const val TAG = "EQLIB"


class Library {

    // Queue to store log requests and reprocess if any fails
    // This logic can be moved to a permanent storage like SQLite for the final version
    val requests: Queue<LocationEvent> = LinkedList()

    private val baseUrl = "https://httpbin.org/post"

    fun setup(): Boolean {
        return true
    }

    fun log(event: LocationEvent, process: Boolean = true) {
        requests.add(event)

        if (process)
            process()
    }

    fun log(context: Context) {
        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Location permission not granted")
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener {
            val event = if (it == null) {
                // user doesn't have a last know location yet
                // fallback to (0,0)
                LocationEvent(0F, 0F, ext = "empty")
            } else {
                LocationEvent(
                    lat = it.latitude.toFloat(),
                    lon = it.longitude.toFloat(),
                    ext = "empty"
                )
            }

            Log.i(TAG, "Started posting")
            log(event)
        }
    }

    fun process(callback: (() -> Unit)? = null) {
        // return, no requests available to process
        if (requests.isEmpty()) return

        val location = requests.peek()!!

        post(location).responseString { _, _, result ->

            result.success {
                Log.i(TAG, "Posted successfully")
                Log.i(TAG, it)

                // remove from queue if posting was successful
                requests.remove()

                // continue processing the rest of the queue
                process()
            }

            result.failure {
                Log.e(TAG, "Failed to post")
                Log.e(TAG, it.response.statusCode.toString())
            }

            if (callback != null) callback()
        }
    }

    private fun post(location: LocationEvent): Request {
        return Fuel.post(baseUrl).body(Gson().toJson(location))
    }
}
