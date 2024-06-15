package it.insubria.freerun_runningapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

class MainActivity : AppCompatActivity() {

    private lateinit var authManager: AuthenticationManager
    private lateinit var guiUtilities: GuiUtilities

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        authManager = AuthenticationManager()
        guiUtilities = GuiUtilities(this)

        // recupero l'informazione aggiuntiva passata all'activity, la quale verrà propagata alle activity
        // login e signUp per determinare se devono o meno aprire i fragment per la richiesta dei permessi.
        val requestPermission = intent.getBooleanExtra("requestPermission", true)

        // se l'utente ha già effettuato l'accesso apro l'home activity
        if(authManager.getCurrentUser() != null){
            guiUtilities.openHomeActivity(true)
        }

        // gestisco il pulsante per la registrazione.
        findViewById<Button>(R.id.welcomeSignUpButton).setOnClickListener {
            guiUtilities.openSignUpActivity(requestPermission)
        }

        // gestisco il pulsante per il login
        findViewById<Button>(R.id.welcomeLogInButton).setOnClickListener {
            guiUtilities.openLoginActivity(requestPermission)
        }
    }
}