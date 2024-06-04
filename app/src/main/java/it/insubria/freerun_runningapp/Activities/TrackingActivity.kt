package it.insubria.freerun_runningapp.Activities

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Services.TrackingService
import it.insubria.freerun_runningapp.Utilities.GuiUtilities
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

//TODO 1.recuperare le textView
// 2. sistemare forse il layout
class TrackingActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvKilometers: TextView
    private lateinit var tvCalories: TextView
    private lateinit var pauseButtonView: View
    private lateinit var restartButtonView: View
    private lateinit var startPauseTrackingLayout: LinearLayout

    private var trackingService: TrackingService? = null
    private var trackingStarted = false
    private var bound = false // variabile che inidica se c'è qulche componente legato al servizio

    private lateinit var runDataUpdater: ExecutorService
    private lateinit var guiUtilities: GuiUtilities

    private lateinit var time: String
    private var km = 0f
    private var calories = 0

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

        guiUtilities = GuiUtilities(this)

        // recupero le varie TextView (tempo, chilometri e calorie)
        tvTime = findViewById(R.id.textViewTime)
        tvKilometers = findViewById(R.id.textViewKilometers)
        tvCalories = findViewById(R.id.textViewCalories)

        // carico il layout del pulsante per mettere in pausa l'attività
        pauseButtonView = LayoutInflater.from(this).inflate(R.layout.pause_traking_button_layout, null)
        // carico il layout del pulsante per riprendere l'attività, e del pulsante per terminare l'attività
        restartButtonView = LayoutInflater.from(this).inflate(R.layout.restart_tracking_button_layout, null)

        // recupero il linear layout che si occupa di contenere le due view definite sopra
        startPauseTrackingLayout = findViewById(R.id.startPauseTrackingLayout)
        startPauseTrackingLayout.addView(pauseButtonView)

        // gestisco il pulsante che mette in pausa l'attività
        pauseButtonView.findViewById<Button>(R.id.pauseTrackingButton).setOnClickListener {
            pauseTacking()
        }

        // gestisco il pulsante che riprende l'attività
        restartButtonView.findViewById<Button>(R.id.restartTrackingButton).setOnClickListener {
            restartTracking()
        }

        // gestisco il pulsante che termina l'attività
        restartButtonView.findViewById<Button>(R.id.stopTrackingButton).setOnClickListener {
            guiUtilities.showAlertDialog(resources.getString(R.string.EndActivtyMessage)){
                stopTrackingService()
            }
        }

        handleOnBackPressed()

        // richiesta dei permessi
        requestNotificationPermission()
        requestActivityRecognitionPermission()

        // start del traking service
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

    // gestisco quando viene premuto il pulsante "indietro" di android
    private fun handleOnBackPressed(){
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                // quando viene premuto non succede niente
            }
        })
    }

    private fun startTrackingService(){
        trackingStarted = true
        val trackingServiceIntent = Intent(this, TrackingService::class.java)
        bindService(trackingServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        startUpdatingRunData() // parte l'updater incaricato di aggiornare i dati della corsa
    }

    private fun stopTrackingService(){
        guiUtilities.openTrackingRecapActivity(time, km, calories, trackingService!!.getAvgPace(), serializeLatLngList(trackingService!!.getLocations()))
        if(bound) {
            unbindService(serviceConnection)
        }
    }

    private fun pauseTacking(){
        trackingService!!.pauseTracking()
        stopUpdatingRunData() // fermo l'updater incaricato di aggiornare i dati della corsa
        trackingStarted = false //TODO forse rimuovere in quanto inutile
        startPauseTrackingLayout.removeView(pauseButtonView)
        startPauseTrackingLayout.addView(restartButtonView)
    }

    private fun restartTracking(){
        trackingService!!.startTracking()
        startUpdatingRunData() // parte l'updater incaricato di aggiornare i dati della corsa
        trackingStarted = true //TODO forse rimuovere in quanto inutile
        startPauseTrackingLayout.removeView(restartButtonView)
        startPauseTrackingLayout.addView(pauseButtonView)
    }

    // funzione che fa partire la Coroutine incaricata di recuperare i dati dal service e di aggiornare la UI
    private fun startUpdatingRunData(){
        runDataUpdater = Executors.newSingleThreadExecutor()
        runDataUpdater.execute {
            //DEBUG
            println("RUN DATA UPDATER STARTED")
            while (true){
                try {
                    if(trackingService != null){
                        // recupero i dati dal service
                        time = trackingService!!.getTime()
                        km = trackingService!!.getKilometres()
                        calories = trackingService!!.getCalories(km)

                        // aggiorno l'interfaccia utente
                        Handler(Looper.getMainLooper()).post {
                            updateUI(time, km, calories)
                        }

                        Thread.sleep(1000)
                    }
                }catch (e: InterruptedException){
                    break
                }
            }
            //DEBUG
            println("RUN DATA UPDATER STOPPED")
        }

    }

    // funzione che ferma la Coroutine incaricata di recuperare i dati dal service e di aggiornare la UI
    private fun stopUpdatingRunData(){
        runDataUpdater.shutdownNow() //fermo il thread
    }

    // metodo che aggiorna le componenti dell'interfaccia utente
    private fun updateUI(time: String, km: Float, calories: Int) {
        tvTime.text = time
        tvKilometers.text = String.format("%.2f", km)
        tvCalories.text = "$calories"
    }

    // metodo che presa in input una lista di oggetti LatLng, la serializza in una lista di stringhe
    // questa operazione è necessaria in quando il metodo dell'intent che permette di recuperare
    // un oggetto serializzato richiede che il device abbiamo come sdk minimo il 33.
    private fun serializeLatLngList(listToSerialize: ArrayList<LatLng>): ArrayList<String>{
        val list = arrayListOf<String>()
        for(item in listToSerialize){
            list.add("${item.latitude},${item.longitude}")
        }
        return list
    }

    // metodo che richiede i permessi per le notifiche
    private fun requestNotificationPermission(){
        // i permessi delle notifiche, devono essere richiesti solo se la versione android del device
        // è superiore ad ANDROID TIRAMISU
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }

    // metodo che richiede i permessi per l'attività fisica
    private fun requestActivityRecognitionPermission(){
        if (Build.VERSION.SDK_INT >= 29) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                1
            )
        }
    }
}