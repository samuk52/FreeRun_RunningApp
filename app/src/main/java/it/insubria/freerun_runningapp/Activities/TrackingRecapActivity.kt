package it.insubria.freerun_runningapp.Activities

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.DataUtilities
import it.insubria.freerun_runningapp.Utilities.GuiUtilities
import it.insubria.freerun_runningapp.Utilities.MapUtilities
import java.lang.IndexOutOfBoundsException
import java.text.SimpleDateFormat
import java.util.Date

//TODO disegnare il tracciato eseguito dall'utente durante la corsa sulla mappa
class TrackingRecapActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvAvgPace: TextView

    private lateinit var googleMap: GoogleMap
    private lateinit var databaseManager: DatabaseManager
    private lateinit var locations: ArrayList<LatLng>

    private lateinit var guiUtilities: GuiUtilities
    private lateinit var mapUtilities: MapUtilities
    private lateinit var dataUtilities: DataUtilities

    private val callback = OnMapReadyCallback{googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        this.googleMap = googleMap
        // faccio in modo che quando premo sui marker non compaiano nella mappa i pulsanti
        // predefiniti (come quello che apre googleMaps)
        googleMap.setOnMarkerClickListener {
            true
        }
        googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        mapUtilities.drawPolyline(googleMap, locations)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_recap)

        databaseManager = DatabaseManager()
        guiUtilities = GuiUtilities(this)
        mapUtilities = MapUtilities(this)
        dataUtilities = DataUtilities()

        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapRecap) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // rendo i bordi della mappa arrotondati
        val mapContainer = findViewById<View>(R.id.mapRecap)
        mapContainer.outlineProvider = object : ViewOutlineProvider(){
            override fun getOutline(view: View, outline: Outline) {
                val radius = resources.getDimensionPixelSize(R.dimen.map_corner_radius)
                outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
            }
        }
        mapContainer.clipToOutline = true

        tvTime = findViewById(R.id.recapTimeText)
        tvDistance = findViewById(R.id.recapDistanceText)
        tvCalories = findViewById(R.id.recapCaloriesText)
        tvAvgPace = findViewById(R.id.avgPaceText)

        // gestisco il pulsante che elimina la corsa effettuata
        findViewById<Button>(R.id.deleteTrackingButton).setOnClickListener {
            guiUtilities.showAlertDialog(resources.getString(R.string.DeleteActivityMessage)){
                guiUtilities.openHomeActivity()
            }
        }

        // gestisco il pulsante che salva la corsa
        findViewById<Button>(R.id.saveTrackingButton).setOnClickListener {
            saveTracking()
        }

        handleOnBackPressed()

        // aggiotno l'intefaccia utente
        updateUI()

    }

    // gestisco quando viene premuto il pulsante "indietro" di android
    private fun handleOnBackPressed(){
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                // quando viene premuto non succede niente
            }
        })
    }

    // metodo che salva la corsa nel database
    private fun saveTracking(){
        val currentDate = SimpleDateFormat("dd/MM/yy").format(Date()) // recupero la data odierna
        databaseManager.addNewRun(currentDate, tvTime.text.toString(), tvDistance.text.toString(), tvAvgPace.text.toString(), tvCalories.text.toString(), locations)
        guiUtilities.openHomeActivity()
    }

    // metodo che aggiorna le componenti dell'interfaccia utente
    private fun updateUI(){
        // recupero i vari dati
        val time = intent.getStringExtra("time") ?: "00:00:00" // nel caso in cui il valore recupero dall'intent è null, assegno alla variabile time il formato di default -> "00:00:00"
        val distance = intent.getFloatExtra("distance", 0f)
        val avgPace = intent.getFloatExtra("avgPace", 0f)
        val calories = intent.getIntExtra("calories", 0)
        // di seguito il metodo getStringArrayListExtra mi restituisce una arrayList di stringhe in particolare
        // quella inviato dall'activity TrackingActivity, per ottenere una lista di oggetti LatLng, vado a deserializzare
        // la lista con il metodo di sotto creato
        locations = dataUtilities.deserializeLatLngList(intent.getStringArrayListExtra("locations") as ArrayList<String>)

        // aggiorno le componenti dell'interfaccio utente
        tvTime.text = time
        tvDistance.text = String.format("%.2f", distance)
        tvAvgPace.text = dataUtilities.getFormattedAvgPace(avgPace)
        tvCalories.text = "$calories"

    }
}