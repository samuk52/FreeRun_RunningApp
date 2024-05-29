package it.insubria.freerun_runningapp.Managers

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.firestore

class DatabaseManager {

    private val db = Firebase.firestore
    private val authManager = AuthenticationManager()


    // funzione che aggiunge un nuovo utente al database.
    fun addNewUserToDB(name: String, weight: Float, gender: String): Task<Void> {
        // TODO vedere se fare classe user.
        val user = mapOf(
            "name" to name,
            "weight" to weight,
            "gender" to gender
        )
        return db.collection("users").document(authManager.getCurrentUser()!!.uid).set(user)
    }

    // metodo che aggiunge nel database una nuova corsa (activity)
    fun addNewActivity(time: String, distance: String, avgPace: String, calories: String, locations: ArrayList<LatLng>){
        val activity = mapOf(
            "time" to time,
            "distance" to distance,
            "avgPace" to avgPace,
            "calories" to calories,
            "locations" to locations
        )
        db.collection("users").document(authManager.getCurrentUser()!!.uid).collection("activities").add(activity)
    }

    // funzione che restituisce le informazioni dell'utente loggato
    fun getUserInfo(): Task<DocumentSnapshot>{
        //DEBUG todo remove
        println("User id -> ${authManager.getCurrentUser()!!.uid}")
        return db.collection("users").document(authManager.getCurrentUser()!!.uid)
            .get()
    }

}