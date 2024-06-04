package it.insubria.freerun_runningapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

class MainActivity : AppCompatActivity() {

    private lateinit var guiUtilities: GuiUtilities

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        guiUtilities = GuiUtilities(this)

        // gestisco il pulsante per la registrazione.
        findViewById<Button>(R.id.welcomeSignUpButton).setOnClickListener {
            guiUtilities.openSignUpActivity()
        }

        // gestisco il pulsante per il login
        findViewById<Button>(R.id.welcomeLogInButton).setOnClickListener {
            // DEBUG todo tenere per debug su homeactivity poi aprire correttamente loginActivity
            guiUtilities.openHomeActivity()
            // startActivity(Intent(this, HomeActivity::class.java))
        }
    }
}