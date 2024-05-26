package it.insubria.freerun_runningapp.Activities

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Services.TrackingService

//TODO 1.recuperare le textView
// 2. sistemare forse il layout
class TrackingActivity : AppCompatActivity() {

    private var trackingService: TrackingService? = null
    private var trackingStarted = false
    private var bound = false // variabile che inidica se c'è qulche componente legato al servizio

    // stabilizzo la connessione con il servizio
    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val trackingBinder = service as TrackingService.TrackingBinder // recupero il binder del servizio
            trackingService = trackingBinder.getService() // dal binder ottengo il servizio
            bound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            bound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)

        // carico il layout del pulsante per mettere in pausa l'attività
        val pauseButtonView = LayoutInflater.from(this).inflate(R.layout.pause_traking_button_layout, null)
        // carico il layout del pulsante per riprendere l'attività, e del pulsante per terminare l'attività
        val restartButtonView = LayoutInflater.from(this).inflate(R.layout.restart_tracking_button_layout, null)

        // recupero il linear layout che si occupa di contenere le due view definite sopra
        val startPauseTrackinglayout = findViewById<LinearLayout>(R.id.startPauseTrackingLayout)
        startPauseTrackinglayout.addView(pauseButtonView)

        // gestisco il pulsante che mette in pausa l'attività
        pauseButtonView.findViewById<Button>(R.id.pauseTrackingButton).setOnClickListener {
            trackingService!!.stopTracking()
            trackingStarted = false //TODO forse rimuovere in quanto inutile
            startPauseTrackinglayout.removeView(pauseButtonView)
            startPauseTrackinglayout.addView(restartButtonView)
        }

        // gestisco il pulsante che riprende l'attività
        restartButtonView.findViewById<Button>(R.id.restartTrackingButton).setOnClickListener {
            trackingService!!.startTracking()
            trackingStarted = true //TODO forse rimuovere in quanto inutile
            startPauseTrackinglayout.removeView(restartButtonView)
            startPauseTrackinglayout.addView(pauseButtonView)
        }

        // gestisco il pulsante che mette in termina l'attività
        restartButtonView.findViewById<Button>(R.id.stopTrackingButton).setOnClickListener {
            // TODO aprire dialog per confermare il termine dell'attività
        }

        requestNotificationPermission()
        startTrackingService()

    }

    override fun onDestroy() {
        super.onDestroy()
        // se qualcuno è connesso al service nel momento della distruzione dell'activity,
        // allora faccio l'unbind del servizio, altrimenti no
        if(bound) {
            unbindService(serviceConnection)
        }
    }

    private fun startTrackingService(){
        trackingStarted = true
        val trackingServiceIntent = Intent(this, TrackingService::class.java)
        bindService(trackingServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    // metodo che richiede i permessi per le notifiche
    private fun requestNotificationPermission(){
        // i permessi delle notifiche, devono essere richiesti sono la versione android del device
        // è superiore ad ANDROID TIRAMISU
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }
}