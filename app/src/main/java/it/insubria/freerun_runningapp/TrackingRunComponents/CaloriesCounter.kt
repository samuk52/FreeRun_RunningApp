package it.insubria.freerun_runningapp.TrackingRunComponents

class CaloriesCounter {

    // TODO recuperare peso dell'utente dal database
    val weight: Float = 70.0f // peso per debug

    fun getCalories(km: Float): Int{
        return (weight * km * 0.9).toInt()
    }

}