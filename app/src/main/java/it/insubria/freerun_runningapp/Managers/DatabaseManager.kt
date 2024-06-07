package it.insubria.freerun_runningapp.Managers

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.firestore

// TODO creare metodo per eliminare la corsa dal database
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
    fun addNewRun(date: String, time: String, distance: String, avgPace: String, calories: String, locations: ArrayList<LatLng>){
        val run = mapOf(
            "date" to date,
            "time" to time,
            "distance" to distance,
            "avgPace" to avgPace,
            "calories" to calories,
            "locations" to locations
        )
        db.collection("users").document(authManager.getCurrentUser()!!.uid).collection("activities").add(run)
    }

    // metodo che aggiorna il nome dell'utente nel database
    fun updateName(name: String) {
        db.collection("users").document(authManager.getCurrentUser()!!.uid)
            .update(
                mapOf(
                    "name" to name
                )
            )
    }

    // metodo che aggiorna il genere dell'utente nel database
    fun updateGender(gender: String){
        db.collection("users").document(authManager.getCurrentUser()!!.uid)
            .update(
                mapOf(
                    "gender" to gender
                )
            )
    }

    // metodo che aggiorna il peso dell'utente nel database.
    fun updateWeight(weight: Float){
        db.collection("users").document(authManager.getCurrentUser()!!.uid)
            .update(
                mapOf(
                    "weight" to weight
                )
            )
    }

    // funzione che restituisce le informazioni dell'utente loggato
    fun getUserInfo(): Task<DocumentSnapshot>{
        //DEBUG todo remove
        println("User id -> ${authManager.getCurrentUser()!!.uid}")
        return db.collection("users").document(authManager.getCurrentUser()!!.uid)
            .get()
    }

    // recupero tutte le corse svolte dell'utente
    fun getUserRuns(): Task<QuerySnapshot> {
        return db.collection("users").document(authManager.getCurrentUser()!!.uid)
            .collection("activities").get()
    }

    fun removeUserRun(documentRunId: String): Task<Void> {
        return db.collection("users").document(authManager.getCurrentUser()!!.uid)
            .collection("activities")
            .document(documentRunId)
            .delete()
    }

}