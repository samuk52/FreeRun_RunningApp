package it.insubria.freerun_runningapp.TrackingRunComponents

import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.Other.User

class CaloriesCounter {

    private val user = User.getInstance()
    private var weight = 0f

    init {
        weight = user!!.getWeight()
        // DEBUG
        println("WEIGHT -> $weight")
    }

    // funzione che calcola le calorie consumato dall'utente
    fun getCalories(km: Float): Int{
        return (weight * km * 0.9).toInt()
    }

}