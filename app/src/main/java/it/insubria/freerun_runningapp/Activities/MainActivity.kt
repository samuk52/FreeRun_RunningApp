package it.insubria.freerun_runningapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import it.insubria.freerun_runningapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // gestisco il pulsante per la registrazione.
        findViewById<Button>(R.id.welcomeSignUpButton).setOnClickListener {
            openSignUpActivity()
        }

        // gestisco il pulsante per il login
        findViewById<Button>(R.id.welcomeLogInButton).setOnClickListener {
            // DEBUG todo tenere per debug su homeactivity
            // startActivity(Intent(this, HomeActivity::class.java))
            openLogInActivity()
        }
    }

    // metodo che apre l'activity per la registrazione dell'utente
    private fun openSignUpActivity(){
        val signUpIntent = Intent(this, SignUpActivity::class.java)
        startActivity(signUpIntent)
    }

    // metodo che apre l'activity per il log in dell'utente
    private fun openLogInActivity(){
        val logInIntent = Intent(this, LoginActivity::class.java)
        startActivity(logInIntent)
    }
}