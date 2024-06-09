package it.insubria.freerun_runningapp.Fragments

import android.graphics.Outline
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapFragment
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.DataUtilities
import it.insubria.freerun_runningapp.Utilities.GuiUtilities
import it.insubria.freerun_runningapp.Utilities.MapUtilities
import org.w3c.dom.Text

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_TIME = "time"
private const val ARG_DISTANCE = "distance"
private const val ARG_CALORIES = "calories"
private const val ARG_PACE = "avgPace"
private const val ARG_LOCATIONS = "locations"

class ShowRunDetailsFragment : Fragment() {
    private var time: String? = null
    private var distance: String? = null
    private var calories: String? = null
    private var avgPace: String? = null
    private var locations: ArrayList<LatLng>? = null

    private lateinit var googleMap: GoogleMap

    private lateinit var dataUtilities: DataUtilities
    private lateinit var mapUtilities: MapUtilities
    private lateinit var guiUtilities: GuiUtilities

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
        mapUtilities.drawPolyline(googleMap, locations!!)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataUtilities = DataUtilities()
        mapUtilities = MapUtilities(requireActivity())
        guiUtilities = GuiUtilities(requireActivity())

        arguments?.let {
            time = it.getString(ARG_TIME)
            distance = it.getString(ARG_DISTANCE)
            calories = it.getString(ARG_CALORIES)
            avgPace = it.getString(ARG_PACE)
            locations= dataUtilities.deserializeLatLngList(it.getStringArrayList(ARG_LOCATIONS)!!)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_run_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment = childFragmentManager.findFragmentById(R.id.mapRunDetails) as SupportMapFragment?
        mapFragment!!.getMapAsync(callback)

        // rendo i bordi della mappa arrotondati
        val mapContainer = view.findViewById<View>(R.id.mapRunDetails)
        mapContainer.outlineProvider = object : ViewOutlineProvider(){
            override fun getOutline(view: View, outline: Outline) {
                val radius = resources.getDimensionPixelSize(R.dimen.map_corner_radius)
                outline.setRoundRect(0, 0, view.width, view.height, radius.toFloat())
            }
        }
        mapContainer.clipToOutline = true

        // gestisco quando viene premuta la textView per ritornare all'activites
        view.findViewById<TextView>(R.id.returnToActivitiesButton).setOnClickListener {
            guiUtilities.openActivitiesFragment(parentFragmentManager)
        }

        updateUI(view)

    }

    // metodo che aggiorna il testo delle view presenti
    private fun updateUI(view: View){
        val tvTime = view.findViewById<TextView>(R.id.timeRunDetailsText)
        val tvDistance = view.findViewById<TextView>(R.id.distanceRunDetailsText)
        val tvCalories = view.findViewById<TextView>(R.id.caloriesRunDetailsText)
        val tvAvgPace = view.findViewById<TextView>(R.id.avgPaveRunDetailsText)

        tvTime.text = time
        tvDistance.text = distance
        tvCalories.text = calories
        tvAvgPace.text = avgPace
    }

    companion object {
        // Use factory method to create a new instance of
        // this fragment using the provided parameters.
        @JvmStatic
        fun newInstance(time: String, distance: String, calories: String, avgPace: String, locations: ArrayList<String>) =
            ShowRunDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_TIME, time)
                    putString(ARG_DISTANCE, distance)
                    putString(ARG_CALORIES, calories)
                    putString(ARG_PACE, avgPace)
                    putStringArrayList(ARG_LOCATIONS, locations)
                }
            }
    }
}