package it.insubria.freerun_runningapp.Activities

import android.content.Intent
import android.graphics.Outline
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import it.insubria.freerun_runningapp.R
import java.lang.IndexOutOfBoundsException
import java.lang.reflect.Array

//TODO disegnare il tracciato eseguito dall'utente durante la corsa sulla mappa
class TrackingRecapActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvAvgPace: TextView

    private lateinit var googleMap: GoogleMap

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
        googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_recap)

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
            //TODO aprire HOME activity
        }

        // gestisco il pulsante che salva la corsa
        findViewById<Button>(R.id.saveTrackingButton).setOnClickListener {
            // TODO salvare la corsa nel database e aprire home activity
        }

        updateUI()

    }

    // metodo che aggiorna le componenti dell'interfaccia utente
    // TODO disegnare sulla mappa il percorso dell'utente utilizzando le posizioni nella lista locations
    //  penso che bisogna avviare un thread.
    private fun updateUI(){
        // recupero i vari dati
        val time = intent.getStringExtra("time")
        val distance = intent.getFloatExtra("distance", 0f)
        val avgPace = intent.getFloatExtra("avgPace", 0f)
        val calories = intent.getIntExtra("calories", 0)
        val locations = intent.getSerializableExtra("locations", ArrayList::class.java)

        // aggiorno le componenti dell'interfaccio utente
        tvTime.text = time
        tvDistance.text = "${String.format("%.2f", distance)} KM"
        tvAvgPace.text = getFormattedAvgPace(avgPace)
        tvCalories.text = "$calories"

    }

    // metodo che restituisce il passo medio formattato
    private fun getFormattedAvgPace(avgPace: Float): String{
        try {
            val avgPaceToFormat = String.format("%.2f", avgPace).split(".")
            return "${avgPaceToFormat[0]}'${avgPaceToFormat[1]}\"/KM"
        }catch (e: IndexOutOfBoundsException){
            return "0'00\"/KM"
        }
    }
}