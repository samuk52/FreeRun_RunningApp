package it.insubria.freerun_runningapp.TrackingRunComponents

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

// TODO: aggiungere lista in cui memorizzare le posizioni rilevate, la quale verrà memorizzata nel db fairbase
class LocationProvider(private val context: Context) {

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var previousLocation: Location? = null
    private var totalDistanceInMt = 0f
    private lateinit var locations: ArrayList<LatLng>

    init {
        createLocationRequest()
        createLocationCallback()
        locations = ArrayList()
    }

    // metodo che crea la location request, la location request contiene i requisti per gli aggiornamenti
    // della posizione, ad es. si specifica la l'accuratezza della posizione, ogni quanto deve essere rilevata
    // (in questo caso ogni 5000 ms (5 secondi) etc...)
    private fun createLocationRequest() {
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000).apply {
            setMinUpdateIntervalMillis(3000)
            setMaxUpdateDelayMillis(10000)
        }.build()
    }

    // metodo che crea la location callback, essa viene chiamata dal fusedLocationProviderClient
    // quando ha una nuova location, in altre parole la location callback serve per recuperare
    // l'ultima posizione rilevata.
    private fun createLocationCallback(){
        locationCallback = object: LocationCallback(){
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                /*
                utilizzo il for per recuperare la posizione perchè Il LocationResult può contenere
                più oggetti Location. Questo accade perché il sistema di localizzazione potrebbe
                raccogliere più aggiornamenti di posizione prima di consegnarli all'applicazione,
                specialmente se la tua applicazione non era in primo piano
                o se ci sono stati ritardi nella consegna degli aggiornamenti.
                 */
                for(location in locationResult.locations){
                    if(previousLocation != null){
                        totalDistanceInMt += previousLocation!!.distanceTo(location) // aggiorno la distanza percorsa

                        // se la posizione rilevata è diversa dalla precedente posizione rilevata allora la aggiungo alla lista delle posizioni rilevate
                        if(!isLocationEqual(previousLocation!!, location)){
                            val latLng = LatLng(location.latitude, location.longitude)
                            locations.add(latLng) // aggiungo la location alla lista della location rilevate
                        }
                    }

                    // serve per memorizzare nella lista locations la prima posizione rilevata
                    if(previousLocation == null){
                        val latLng = LatLng(location.latitude, location.longitude)
                        locations.add(latLng)
                    }

                    previousLocation = location
                }
            }
        }
    }

    // metodo che starta gli aggiornamenti della posizione
    fun startLocationUpdates() {
        println("-- START LOCATION UPDATES --")
        try {
            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // DEBUG todo remove
            println("Permessi localizzazione non conessi")
        }
    }

    // metodo che termina gli aggiornamenti della posizione
    fun stopLocationUpdates(){
        println("-- STOP LOCATION UPDATES --")
        previousLocation = null
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

    // metodo che ritorna se le due posizioni passate come parametro sono uguali
    private fun isLocationEqual(previousLocation: Location, currentLocation: Location): Boolean{
        return (previousLocation.latitude == currentLocation.latitude) && (previousLocation.longitude == currentLocation.longitude)
    }

    // metodo che ritorna i chilometri percorsi dall'utente
    fun getDistanceInKm(): Float{
        return totalDistanceInMt / 1000
    }

}