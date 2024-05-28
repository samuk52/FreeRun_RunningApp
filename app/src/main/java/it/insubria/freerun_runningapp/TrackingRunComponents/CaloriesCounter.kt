package it.insubria.freerun_runningapp.TrackingRunComponents

import it.insubria.freerun_runningapp.Managers.DatabaseManager

class CaloriesCounter {

    // TODO recuperare peso dell'utente dal database
    private val databaseManager = DatabaseManager()
    private var weight = 0f

    init {
        databaseManager.getUserInfo().addOnSuccessListener { document ->
            if(document.getDouble("weight") != null) {
                weight = document.getDouble("weight")!!.toFloat()
                println("WEIGHT -> $weight")
            }
        }
    }

    // funzione che calcola le calorie consumato dall'utente
    fun getCalories(km: Float): Int{
        return (weight * km * 0.9).toInt()
    }

}