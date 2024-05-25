package it.insubria.freerun_runningapp.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.TrackingRunComponents.LocationProvider
import it.insubria.freerun_runningapp.TrackingRunComponents.StopWatch

class TrackingService: Service(){

    private val trackingBinder = TrackingBinder(this)
    private lateinit var notificationManager: NotificationManager
    private lateinit var notification: Notification
    private lateinit var stopWatch: StopWatch
    private lateinit var locationProvider: LocationProvider

    class TrackingBinder(private val trackingService: TrackingService): Binder(){
        fun getService(): TrackingService{
            return trackingService
        }
    }

    override fun onCreate() {
        super.onCreate()
        stopWatch = StopWatch()
        locationProvider = LocationProvider(this)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onBind(intent: Intent?): IBinder {
        makeForeground()
        startTracking()
        // TODO inserire i vari metodi che devono essere avviati quando viene lanciato il servizio
        return trackingBinder
    }

    override fun unbindService(conn: ServiceConnection) {
        super.unbindService(conn)
        stopTracking()
    }

    // metodo che fa partire il monitoraggio della corsa.
    fun startTracking(){
        stopWatch.start() // avvio il cronometro
        locationProvider.startLocationUpdates()
    }

    // metodo che ferma il monitoraggio della corsa.
    fun stopTracking(){
        stopWatch.stop() // fermo il cronometro
        locationProvider.stopLocationUpdates() // TODO forse rimuovere da qui, e metterlo separato nel metodo unbind.
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