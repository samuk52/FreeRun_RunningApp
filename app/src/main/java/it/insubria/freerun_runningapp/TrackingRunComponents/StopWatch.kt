package it.insubria.freerun_runningapp.TrackingRunComponents

import com.google.android.gms.location.FusedLocationProviderClient
import java.sql.Time
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class StopWatch {

    private lateinit var executor: ExecutorService
    private var stopWatchTime = 0L
    private var oldStopWatchTime = 0L
    private var started = false

    // metodo che fa partire il cronometro
    fun start(){
        started = true
        val startTime = System.currentTimeMillis()
        executor = Executors.newSingleThreadExecutor()
        executor.execute {
            while (started){
                val currentTime = System.currentTimeMillis()
                stopWatchTime = currentTime - startTime + oldStopWatchTime
                // DEBUG
                println(getFormattedStopWatchTime())
                Thread.sleep(1000)
            }
            // DEBUG todo remove
            println("StopWatch stopped")
        }

    }

    // metodo che mette in ferma il cronometro
    fun stop(){
        oldStopWatchTime = stopWatchTime
        started = false
    }

    // metodo che restituisco il tempo del cronometro
    fun getFormattedStopWatchTime(): String{
        val hours = TimeUnit.MILLISECONDS.toHours(stopWatchTime) % 24
        val minutes = TimeUnit.MILLISECONDS.toMinutes(stopWatchTime) % 60
        val seconds = TimeUnit.MILLISECONDS.toSeconds(stopWatchTime) % 60
        return "${if(hours < 10) "0" else ""}$hours:${if(minutes < 10) "0" else ""}$minutes:${if (seconds < 10) "0" else ""}$seconds"
    }

    // funzione che restituisce il tempo del cronometro in minuti, per il calcolo del passo medio.
    fun getStopWatchTimeInMinutes(): Long{
        return TimeUnit.MILLISECONDS.toMinutes(stopWatchTime)
    }

}