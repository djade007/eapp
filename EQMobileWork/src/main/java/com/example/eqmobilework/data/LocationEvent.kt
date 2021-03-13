package com.example.eqmobilework.data

data class LocationEvent(
    val lat: Float,
    val lon: Float,
    val time: Long = System.currentTimeMillis(),
    val ext: String,
)
