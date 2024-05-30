package it.insubria.freerun_runningapp.TrackingRunComponents

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import it.insubria.freerun_runningapp.Managers.DatabaseManager

class StepCounter(private val context: Context): SensorEventListener {

    //TODO recuperare il genere dell'utente dal database
    private val databaseManager = DatabaseManager()
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private var totalSteps = 0
    private var trackingStarted = false
    private var avgStepLength = 0f // lunghezza passo medio

    init {
        databaseManager.getUserInfo().addOnSuccessListener { document ->
            if(document.get("gender") != null) {
                val gender = document.get("gender") as String
                if(gender == "Man"){
                    avgStepLength = 0.76f
                }else if (gender == "Woman"){
                    avgStepLength = 0.67f
                }
                //DEBUG
                println("GENDER -> $gender, value -> $avgStepLength")
            }
        }
    }

    // metodo che viene chiamato, ogni qual volta viene rilevato un evento emesso dal sensore passato
    // al listener
    override fun onSensorChanged(event: SensorEvent?) {
        // i passi devono aggiunti solo quando il tracking è attivo
        if(trackingStarted) {
            if (event!!.sensor == stepSensor) {
                totalSteps += event.values[0].toInt()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    // metodo che starta lo stepCounter
    fun start(){
        sensorManager.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_NORMAL)
        trackingStarted = true
    }

    // metodo che mette in pausa lo stepCounter
    fun pause(){
        trackingStarted = false
    }

    // metodo che ferma lo stepCounter
    fun stop(){
        //DEBUG
        println("--- step counter stopper ---")
        sensorManager.unregisterListener(this)
    }

    // metodo che restituisce se è presente il step detector sensor
    fun isStepSensorPresent(): Boolean{
        return stepSensor != null
    }

    // metodo che restituisce la distanza percorsa in chilometri
    fun getDistanceInKm(): Float {
        val distanceInMt = totalSteps * avgStepLength
        return distanceInMt / 1000
    }

    // funzione che ritorna il passo medio
    fun getAvgPace(minute: Long): Float{
        val distanceInMt = totalSteps * avgStepLength
        val distanceInKm = distanceInMt / 1000
        return (minute / distanceInKm)
    }

}