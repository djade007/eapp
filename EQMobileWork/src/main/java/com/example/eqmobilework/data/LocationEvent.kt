package com.example.eqmobilework.data

data class LocationEvent(
    val lat: Float,
    val lon: Float,
    val time: Long = System.currentTimeMillis() / 1000L,
    val ext: String,
)
