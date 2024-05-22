package it.insubria.freerun_runningapp.Managers

import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.firestore

class DatabaseManager {

    private val db = Firebase.firestore

    // funzione che aggiunge un nuovo utente al database.
    fun addNewUserToDB(name: String, weight: Float): Task<DocumentReference> {
        // TODO vedere se fare classe user.
        val user = mapOf("name" to name, "weight" to weight)
        return db.collection("users")
            .add(user)
    }

}