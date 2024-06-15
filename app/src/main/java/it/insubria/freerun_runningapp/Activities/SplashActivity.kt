package it.insubria.freerun_runningapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

class SplashActivity : AppCompatActivity() {

    private lateinit var authManager: AuthenticationManager
    private lateinit var guiUtilities: GuiUtilities
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        authManager = AuthenticationManager()
        guiUtilities = GuiUtilities(this)

        // classe che apsetta 3 secondi e poi apre, in base a se l'utente è già loggato o meno
        // la mainActivity o la HomeActivity
        Handler(Looper.getMainLooper()).postDelayed({
            // se l'utente ha già effettuato l'accesso apro l'home activity
            if(authManager.getCurrentUser() != null){
                guiUtilities.openHomeActivity(true)
            }else{
                guiUtilities.openMainActivity(true)
            } }, 3000)

    }
}