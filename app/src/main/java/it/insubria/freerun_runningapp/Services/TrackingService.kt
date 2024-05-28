package it.insubria.freerun_runningapp.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.maps.model.LatLng
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.TrackingRunComponents.CaloriesCounter
import it.insubria.freerun_runningapp.TrackingRunComponents.LocationProvider
import it.insubria.freerun_runningapp.TrackingRunComponents.StepCounter
import it.insubria.freerun_runningapp.TrackingRunComponents.StopWatch

class TrackingService: Service(){

    private val trackingBinder = TrackingBinder(this)
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification
    private lateinit var stopWatch: StopWatch
    private lateinit var locationProvider: LocationProvider
    private lateinit var stepCounter: StepCounter
    private lateinit var caloriesCounter: CaloriesCounter

    class TrackingBinder(private val trackingService: TrackingService): Binder(){
        fun getService(): TrackingService{
            return trackingService
        }
    }

    override fun onCreate() {
        super.onCreate()
        stopWatch = StopWatch()
        locationProvider = LocationProvider(this)
        stepCounter = StepCounter(this)
        caloriesCounter = CaloriesCounter()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent?): IBinder {
        makeForeground()
        startTracking()
        // TODO inserire i vari metodi che devono essere avviati quando viene lanciato il servizio
        return trackingBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTracking()
    }

    // metodo che fa partire il monitoraggio della corsa.
    fun startTracking(){
        stopWatch.start() // avvio il cronometro
        locationProvider.startLocationUpdates()
        // verifico se è presente il sensore che conta i passi
        if(stepCounter.isStepSensorPresent()) {
            stepCounter.start() // se è presente lo avvio
        }
    }

    // metodo che mette in pausa il monitoraggio della corsa.
    fun pauseTracking(){
        stopWatch.stop() // fermo il cronometro
        locationProvider.stopLocationUpdates() // TODO forse rimuovere da qui, e metterlo separato nel metodo unbind.
        // verifico se è presente il sensore che conta i passi
        if(stepCounter.isStepSensorPresent()) {
            stepCounter.pause()
        }
    }

    // metodo che termina il monitoraggio della corsa.
    fun stopTracking(){
        stopWatch.stop() // fermo il cronometro
        locationProvider.stopLocationUpdates() // TODO forse rimuovere da qui, e metterlo separato nel metodo unbind.
        // verifico se è presente il sensore che conta i passi
        if(stepCounter.isStepSensorPresent()) {
            stepCounter.stop()
        }
    }

    // funzione che restituisce il tempo del cronometro.
    fun getTime(): String{
        return stopWatch.getFormattedStopWatchTime()
    }

    // funzione che restituisce i chilometri percorsi.
    fun getKilometres(): Float {
        // se è presenta il sensore che conta i passi, restituisco la distanza calcolata da esso
        // altrimenti restituisco la distanza calcolata con le posizioni rilevate
        if (stepCounter.isStepSensorPresent()) {
            return stepCounter.getDistanceInKm()
        } else{
            return locationProvider.getDistanceInKm()
        }
    }

    // funzione che restituisce il passo medio
    fun getAvgPace(): Float{
        val minutes = stopWatch.getStopWatchTimeInMinutes()
        if(stepCounter.isStepSensorPresent()){
            return stepCounter.getAvgPace(minutes)
        }else{
            return locationProvider.getAvgPace(minutes)
        }
    }

    // funzione che restituisce le posizioni rilevate durante la corsa.
    fun getLocations(): ArrayList<LatLng>{
        return locationProvider.getLocations()
    }

    // funzione che ritorna le calorie consumate durante la corsa.
    fun getCalories(km: Float): Int{
        return caloriesCounter.getCalories(km)
    }

    private fun makeForeground(){
        createNotificationChannel()
        showNotification()
        startForeground(SERVICE_ID, notification) // metodo che fa partire il servizio in foregorund

    }

    // metodo che crea il notification channel, il canale è necessario crearlo se la versione di android
    // che si sta utilizzando è superiore o uguale ad ANDROID OREO
    private fun createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Channel_Tracking_service",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    // metodo che mostra la notifica
    private fun showNotification(){
        notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Traking service") // todo modificare
            .setContentText("Free run tracking your activity") // todo modificare
            .setSmallIcon(R.drawable.run)
            .setOnlyAlertOnce(true) // la notifica emette un suono solo la prima volta.
            .build()
        notificationManager.notify(NOTIFY_ID, notification) // mostro la notifica
    }

    companion object{
        private const val CHANNEL_ID = "Tracking_service"
        private const val SERVICE_ID = 1
        private const val NOTIFY_ID = 1
    }

}