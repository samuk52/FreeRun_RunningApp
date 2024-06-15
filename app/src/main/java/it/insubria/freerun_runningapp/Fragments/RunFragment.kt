package it.insubria.freerun_runningapp.Fragments

import android.content.Intent
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import it.insubria.freerun_runningapp.Activities.CountDownActivity
import it.insubria.freerun_runningapp.Activities.MainActivity
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities
import java.lang.NullPointerException

class RunFragment : Fragment(){

    private lateinit var googleMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var guiUtilities: GuiUtilities

    private val callback = OnMapReadyCallback {googleMap ->
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
        this.googleMap.mapType = GoogleMap.MAP_TYPE_TERRAIN
        getCurrentPosition(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_run, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        guiUtilities = GuiUtilities(requireActivity())

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // gestisco il pulsante che avvia la corsa.
        view.findViewById<Button>(R.id.startRunButton).setOnClickListener {
            guiUtilities.openCountDownActivity()
        }

    }

    // TODO vedere se ripristinare ramo else
    private fun getCurrentPosition(fromMapReadyCallback: Boolean){
        // verifico se l'utente ha dato i permessi per la localizzazione, in caso contrario
        // invio la richiesta per i permessi
        if(ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            // quando isMyLocationEnabled è abilitato e la posizione è disponibile, il
            // my-location-layer continua a disegnare un'indicazione della posizione
            // e della direzione corrente dell'utente, in altre parole disegna sulla mappa
            // il pallino che indica la posizione corrente dell'utente.
            googleMap.isMyLocationEnabled = true
            // sposto la camera di googleMaps sulla posizione corrente
            fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                try {
                    if(task.isSuccessful){
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(task.result.latitude, task.result.longitude), 15f))
                    }
                }catch (e: NullPointerException){
                    // il recupero della posizione potrebbe restituisce una posizione nulla per un qualche motivo
                    // se così succede, viene sollegata una NullPointerExcpetion quando vado a recuperare la
                    // latitidine e la longitudine della posizione nel metodo moveCamera di googleMap, per questo
                    // motivo gestisco l'eccezzione, facendo in modo che se viene sollevata cerco di recuperare di
                    // nuovo la posizione
                    getCurrentPosition(false)
                }
            }
        }
    }
}