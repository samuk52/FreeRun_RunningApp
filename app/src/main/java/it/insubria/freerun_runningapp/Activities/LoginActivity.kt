package it.insubria.freerun_runningapp.Activities

import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import it.insubria.freerun_runningapp.R

// TODO forse aggiungere textView per il recupero della password, il quale avverrà tramite l'API di firebase authentication
// TODO modificare metodo showErrorLoginMessage, il quale deve mostare o un banner o un Toast con il layout definito sta mattina
class LoginActivity : AppCompatActivity() {

    private lateinit var authenticationManager: AuthenticationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authenticationManager = AuthenticationManager()

        // recupero le textField presenti nel form di login
        val emailTextField = findViewById<TextInputLayout>(R.id.emailLogin)
        val passwordTextField = findViewById<TextInputLayout>(R.id.passwordLogin)

        // gestisco il pulsante di chiusura del form di login
        findViewById<Button>(R.id.closeLogInButton).setOnClickListener{
            openMainActivity()
        }

        // gestisco il pulsante di log in
        findViewById<Button>(R.id.logInButton).setOnClickListener {
            logIn(
                emailTextField.editText?.text.toString(),
                passwordTextField.editText?.text.toString()
            )
        }
    }

    // funzione per effettuare il login
    private fun logIn(email: String, password: String){
        /*
        chiamo il metodo dell'authentication manager per effettuare il login, esso restituisce un
        Task<AuthResult>? e lo utilizzo per chiamare un listener che verifichi se il login è andata a buon fine o meno,
        oppure restituisce null sei uno o entrambi i parametri passati (email e password) sono vuoti.
        */
        val authResult = authenticationManager.logIn(email, password)
        if(authResult != null){
            authResult.addOnCompleteListener { task ->
                if (task.isSuccessful){
                    // TODO apire activity home
                    // DEBUG todo rimuovere
                    Toast.makeText(this, "Login successfully", Toast.LENGTH_LONG).show()
                }else{
                    showErrorLoginMessage()
                }
            }
        }else{
            showErrorLoginMessage()
        }
    }

    // metodo che apre la mainActivity
    private fun openMainActivity(){
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
    }

    // funzione che viene invocata dall'it.insubria.freerun.managers.AuthenticationManager se il log in non è andato a buon fine
    // essa viene passata come argomento alla funzione login dell'authentication manager.
    private fun successLogin(){
        // TODO implemetare, in particolare aprire le activity che chiedono il peso e l'altezza all'utente per il calcolo delle calorie
        // DEBUG
        Log.d("Login Activity", "Login successfully")
    }

    // funzione che viene invocata dall'it.insubria.freerun.managers.AuthenticationManager se il login non è andato a buon fine
    // essa viene passata come argomento alla funzione login dell'authentication manager.
    private fun showErrorLoginMessage(){
        // recupero la roorView
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        // set della view contenete il layout di errore
        val errorBanner = layoutInflater.inflate(R.layout.error_authentication_layout, rootView, false)
        // recupero le due textView, titolo e messaggio, della view
        val errorTitle = errorBanner.findViewById<TextView>(R.id.errorTitleText)
        val errorMessage = errorBanner.findViewById<TextView>(R.id.errorMessageText)
        // modifico il titolo e il messaggio della error view
        errorTitle.text = resources.getString(R.string.ErrorLogInTitle)
        errorMessage.text = resources.getString(R.string.ErrorLogInMessage)
        // aggiungo la view di errore
        rootView.addView(errorBanner)
        // avvio un thread (hanlder) che si occuperà di eliminare la view di error dopo 3 secondi dalla sua visualizzazione
        Handler(Looper.getMainLooper()).postDelayed({
            rootView.removeView(errorBanner)
        }, 3000)
    }

}