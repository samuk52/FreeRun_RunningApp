package it.insubria.freerun_runningapp.TrackingRunComponents

import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

// TODO: aggiungere lista in cui memorizzare le posizioni rilevate, la quale verrà memorizzata nel db fairbase
class LocationProvider(private val context: Context) {

    private val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var currentLocation: Location? = null


    init {
        createLocationRequest()
        createLocationCallback()
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
                    currentLocation = location
                    // DEBUG TODO remove
                    println("${location.longitude} - ${location.latitude}")
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
        // DEBUG
        println("-- STOP LOCATION UPDATES --")
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}