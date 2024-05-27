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
import androidx.core.app.ActivityCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Services.TrackingService
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
            showEndActivityDialog()
        }

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

    private fun startTrackingService(){
        trackingStarted = true
        val trackingServiceIntent = Intent(this, TrackingService::class.java)
        bindService(trackingServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        startUpdatingRunData() // parte l'updater incaricato di aggiornare i dati della corsa
    }

    private fun stopTrackingService(){
        if(bound) {
            unbindService(serviceConnection)
        }
        //DEBUG poi rimuovere e aprire l'activity corretta
        startActivity(Intent(this, HomeActivity::class.java))
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
                        val time = trackingService!!.getTime()
                        val km = trackingService!!.getKilometres()
                        // val calories = 0 // TODO recuperare le calorie correttamente
                        val calories = trackingService!!.getCalories(km)

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

    // metodo che mostra un dialog che chiede all'utente se vuole terminare l'attività
    private fun showEndActivityDialog(){
        // recupero la view
        val view = LayoutInflater.from(this).inflate(R.layout.end_activity_dialog_layout, null)
        // creo il dialog
        val endActivityDialog = MaterialAlertDialogBuilder(this).setView(view).show()
        // gestisco quando viene premuto il pulsante che termina l'attività
        view.findViewById<Button>(R.id.endActivityDialognButton).setOnClickListener {
            stopTrackingService()
        }
        // gestisco quando viene premuto il pulsante che chiude il dialog
        view.findViewById<Button>(R.id.closeEndActivityDialogButton).setOnClickListener {
            endActivityDialog.cancel()
        }
    }

    // metodo che richiede i permessi per le notifiche
    private fun requestNotificationPermission(){
        // i permessi delle notifiche, devono essere richiesti solo se la versione android del device
        // è superiore ad ANDROID TIRAMISU
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }

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