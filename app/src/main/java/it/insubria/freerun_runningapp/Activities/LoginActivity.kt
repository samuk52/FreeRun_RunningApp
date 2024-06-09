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
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

// TODO forse aggiungere textView per il recupero della password, il quale avverrà tramite l'API di firebase authentication
// TODO modificare metodo showErrorLoginMessage, il quale deve mostare o un banner o un Toast con il layout definito sta mattina
class LoginActivity : AppCompatActivity() {

    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var guiUtilities: GuiUtilities

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authenticationManager = AuthenticationManager()
        guiUtilities = GuiUtilities(this)

        // recupero le textField presenti nel form di login
        val emailTextField = findViewById<TextInputLayout>(R.id.emailLogin)
        val passwordTextField = findViewById<TextInputLayout>(R.id.passwordLogin)

        // gestisco il pulsante di chiusura del form di login
        findViewById<Button>(R.id.closeLogInButton).setOnClickListener{
            guiUtilities.openMainActivity()
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
                    successLogin()
                }else{
                    guiUtilities.showErrorBanner(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.ErrorLogInTitle),
                        resources.getString(R.string.ErrorLogInMessage)
                    )
                }
            }
        }else{
            guiUtilities.showErrorBanner(
                findViewById(android.R.id.content),
                resources.getString(R.string.ErrorLogInTitle),
                resources.getString(R.string.ErrorLogInMessage)
            )
        }
    }

    // metodo che viene eseguito nel caso in cui il login è avvenuto con successo
    private fun successLogin(){
        // se il login è avvenuto correttamente apro la home activity
        guiUtilities.openHomeActivity()
    }

}