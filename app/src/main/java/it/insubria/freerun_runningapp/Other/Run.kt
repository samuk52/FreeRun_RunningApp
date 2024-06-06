package it.insubria.freerun_runningapp.Other

import com.google.android.gms.maps.model.LatLng

class Run(private val id: String,
          private val date: String,
          private val distance: String,
          private val time: String,
          private val calories: String,
          private val avgPace: String,
          private val locations: ArrayList<LatLng>) {

    fun getId(): String{
        return id
    }

    fun getDate(): String{
        return date
    }

    fun getDistance(): String{
        return distance
    }

    fun getTime(): String{
        return time
    }

    fun getCalories(): String{
        return calories
    }

    fun getAvgPace(): String{
        return avgPace
    }

    fun getLocations(): ArrayList<LatLng>{
        return locations
    }

    override fun toString(): String {
        return "$id, $date, $distance, $time, $calories, $avgPace, Locations: $locations"
    }

}