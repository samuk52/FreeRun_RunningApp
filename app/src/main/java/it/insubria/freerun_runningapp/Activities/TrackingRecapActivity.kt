package it.insubria.freerun_runningapp.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.TextView
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.R
import java.lang.IndexOutOfBoundsException

//TODO disegnare il tracciato eseguito dall'utente durante la corsa sulla mappa
class TrackingRecapActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var tvDistance: TextView
    private lateinit var tvCalories: TextView
    private lateinit var tvAvgPace: TextView

    private lateinit var googleMap: GoogleMap
    private lateinit var databaseManager: DatabaseManager
    private lateinit var locations: ArrayList<LatLng>

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
        drawPolyline()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking_recap)

        databaseManager = DatabaseManager()

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
            // apro il dialog per la conferma dell'eliminazione
            showDeleteTrackingDialog()
        }

        // gestisco il pulsante che salva la corsa
        findViewById<Button>(R.id.saveTrackingButton).setOnClickListener {
            saveTracking()
        }

        // aggiotno l'intefaccia utente
        updateUI()

    }

    // metodo che salva la corsa nel database
    private fun saveTracking(){
        // TODO salvare nel database la corsa.
        databaseManager.addNewActivity(tvTime.text.toString(), tvDistance.text.toString(), tvAvgPace.text.toString(), tvCalories.text.toString(), locations)
        openHomeActivity()
    }

    // metodo che aggiorna le componenti dell'interfaccia utente
    private fun updateUI(){
        // recupero i vari dati
        val time = intent.getStringExtra("time") ?: "00:00:00" // nel caso in cui il valore recupero dall'intent è null, assegno alla variabile time il formato di default -> "00:00:00"
        val distance = intent.getFloatExtra("distance", 0f)
        val avgPace = intent.getFloatExtra("avgPace", 0f)
        val calories = intent.getIntExtra("calories", 0)
        locations = intent.getSerializableExtra("locations", kotlin.collections.ArrayList::class.java) as ArrayList<LatLng>

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

    // metodo che disegna il percorso eseguito dall'utente durante la corsa.
    private fun drawPolyline(){
        // disegno il percorso
        val polyline = googleMap.addPolyline(PolylineOptions()
            .addAll(locations))
        polyline.color = getColor(R.color.orange)
        polyline.jointType = JointType.ROUND

        // aggiungo il marker che indica l'inzio della corsa
        googleMap.addMarker(MarkerOptions()
            .position(locations[0])
            .icon(createCircleIcon(getColor(R.color.green))))

        // aggiungo il marker che indica la fine della corsa.
        googleMap.addMarker(MarkerOptions()
            .position(locations[locations.lastIndex])
            .icon(createCircleIcon(getColor(R.color.red))))

        // calcolo i limiti della polyline, essi mi servono per zoomare sul percorso effettauto
        // dall'utente, per calcolare i limiti utilizzo un oggetto LatLngBounds.Builder()
        // al quale aggiungo i punti della polyline e lui tramite il metodo build ci restituisce
        // i limiti
        val builder = LatLngBounds.builder()
        //itero i punti della polyline e gli aggingo al builder
        for(point in polyline.points){
            builder.include(point)
        }
        // recupero i limiti della polyline
        val bounds = builder.build()

        // sporto la telecamera di google maps sui limiti
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
    }

    // funzione che crea i circhi di inizio e fine corsa che verranno visualizzati sulla mappa
    private fun createCircleIcon(color: Int): BitmapDescriptor{
        val diameter = 30
        val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    // metodo che mostra un dialog che chiede all'utente se vuole terminare l'attività
    private fun showDeleteTrackingDialog(){
        // recupero la view
        val view = LayoutInflater.from(this).inflate(R.layout.alert_activity_dialog_layout, null)
        // imposto il messaggio di allerta
        val message = view.findViewById<TextView>(R.id.alertActivityDialogText)
        message.text = resources.getString(R.string.DeleteActivityMessage)
        // creo il dialog
        val endActivityDialog = MaterialAlertDialogBuilder(this).setView(view).show()
        // gestisco quando viene premuto il pulsante di conferma
        view.findViewById<Button>(R.id.positiveButton).setOnClickListener {
            openHomeActivity()
        }
        // gestisco quando viene premuto il pulsante di negazione
        view.findViewById<Button>(R.id.negativeButton).setOnClickListener {
            endActivityDialog.cancel()
        }
    }

    // metodo che apre la home activity
    private fun openHomeActivity(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}