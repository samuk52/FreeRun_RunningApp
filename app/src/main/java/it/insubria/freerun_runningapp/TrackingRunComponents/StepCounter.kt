package it.insubria.freerun_runningapp.TrackingRunComponents

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

class StepCounter(private val context: Context): SensorEventListener {

    //TODO recuperare il genere dell'utente dal database

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    private var totalSteps = 0
    private var trackingStarted = false

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
    fun getDistanceInKm(): Float{
        // TODO modificare, in quanto dipende dal genere, infatti se uomo moltiplichiamo i passi * 0.78
        //  se donna moltiplichiamo i passi * 0.70, quindi recuperare genere utente
        val distanceInMt = totalSteps * 0.78
        val distanceInKm = distanceInMt / 1000
        return distanceInKm.toFloat()
    }

}