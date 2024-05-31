package it.insubria.freerun_runningapp.Managers

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AuthenticationManager {

    private val EMAIL_REGEX = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}\$"
    private val PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(?!.* ).{8,16}\$"

    private val auth = Firebase.auth

    // metodo per la creazione dell'account
    fun createAccount(email: String, password: String): Task<AuthResult>? {
        if (email.isBlank() || password.isBlank() || !email.matches(Regex(EMAIL_REGEX)) || !password.matches(Regex(PASSWORD_REGEX))) {
            return null
        } else {
            return auth.createUserWithEmailAndPassword(email, password)
        }
    }

    // metodo per il login
    fun logIn(email: String, password: String): Task<AuthResult>?{
        if(email.isBlank() || password.isBlank()){
            return null
        }else {
            return auth.signInWithEmailAndPassword(email, password)
        }
    }

    // metodo che effettua il signOut
    fun signOut(){
        auth.signOut()
    }

    // metodo che restituisce l'utente corrente
    fun getCurrentUser(): FirebaseUser?{
        return auth.currentUser
    }

}