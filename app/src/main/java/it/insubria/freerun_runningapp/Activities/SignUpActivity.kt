package it.insubria.freerun_runningapp.Activities

import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputLayout
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.R
import org.w3c.dom.Text

// TODO modificare metodo showErrorSignUpMessage, il quale deve mostare o un banner o un Toast con il layout definito sta mattina
class SignUpActivity : AppCompatActivity() {

    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var databaseManager: DatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        authenticationManager = AuthenticationManager()
        databaseManager = DatabaseManager()

        // recupero il le tre textField presenti nel form di registrazione
        val nameTextField = findViewById<TextInputLayout>(R.id.nameTextField)
        val emailTextField = findViewById<TextInputLayout>(R.id.emailSignUp)
        val passwordTextField = findViewById<TextInputLayout>(R.id.passwordSignUp)
        val weightText = findViewById<TextView>(R.id.weightText)

        val weightSlider = findViewById<Slider>(R.id.weightSlider)
        weightSlider.setLabelFormatter{ value ->
            "$value Kg"
        }
        weightSlider.addOnChangeListener { slider, value, fromUser ->
            weightText.text = "${value} Kg"
        }

        // gestisco il pulsante di chiusura del form di registrazione
        findViewById<Button>(R.id.closeSignUpButton).setOnClickListener{
            openMainActivity()
        }

        // gestisco quando viene premuta la textView per le informazioni della password
        findViewById<TextView>(R.id.textPasswordInfo).setOnClickListener {
            showPasswordInformation()
        }

        // gestisco quando viene premuta la textView del peso.
        findViewById<TextView>(R.id.weightTextInfo).setOnClickListener {
            showWeightInformation()
        }

        // gestisco il pulsante di registrazione
        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            signUp(
                emailTextField.editText?.text.toString(),
                passwordTextField.editText?.text.toString(),
                nameTextField.editText?.text.toString(),
                weightSlider.value
            )
        }
    }

    // funzione per effettuare la registrazione
    private fun signUp(email: String, password: String, name: String, weight: Float){
        /*
        chiamo il metodo dell'authentication manager per effettuare la registrazione, esso restituisce un
        Task<AuthResult>? e lo utilizzo per chiamare un listener che verifichi se la registrazione è andata a buon fine o meno,
        oppure restituisce null sei uno o entrambi i parametri passati (email e password) sono vuoti.
         */
        val authResult = authenticationManager.createAccount(email, password)
        if(authResult != null){
            authResult.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    // se la registrazione è andata a buon fine, aggiungo il numero utente al database.
                    databaseManager.addNewUserToDB(name, weight).addOnSuccessListener {
                        // DEBUG todo rimuove
                        Log.d("SignUpActivity", "added user document for $email")
                    }
                    // TODO activiy per richiedere i vari permessi.
                }else{
                    showErrorSignUpMessage()
                }
            }
        }else{
            showErrorSignUpMessage()
        }
    }

    // funzione che apre la main activity
    private fun openMainActivity(){
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        startActivity(mainActivityIntent)
    }

    // funzione che viene invocata dall'it.insubria.freerun.managers.AuthenticationManager se la registrazione è andata a buon fine
    // essa viene passata come argomento alla funzione createAccount dell'authentication manager.
    private fun successSignUp(){
        // TODO implemetare, in particolare aprire le activity che chiedono il peso e l'altezza all'utente per il calcolo delle calorie
        // DEBUG
        Log.d("SignUp Activity", "Sign Up successfully")
    }

    // funzione che viene invocata dall'it.insubria.freerun.managers.AuthenticationManager se la registrazione non è andata a buon fine
    // essa viene passata come argomento alla funzione createAccount dell'authentication manager.
    private fun showErrorSignUpMessage(){
        // recupero la rootView
        val rootView = findViewById<ViewGroup>(android.R.id.content)
        // set della view contenete il layout di errore
        val errorBanner = layoutInflater.inflate(R.layout.error_authentication_layout, rootView, false)
        // recupero le due textView, titolo e messaggio, della view
        val errorTitle = errorBanner.findViewById<TextView>(R.id.errorTitleText)
        val errorMessage = errorBanner.findViewById<TextView>(R.id.errorMessageText)
        // modifico il titolo e il messaggio della error view
        errorTitle.text = resources.getString(R.string.ErrorSignUpTitle)
        errorMessage.text = resources.getString(R.string.ErrorSignUpMessage)
        // aggiungo la view di errore
        rootView.addView(errorBanner)
        // avvio un thread (hanlder) che si occuperà di eliminare la view di error dopo 3 secondi dalla sua visualizzazione
        Handler(Looper.getMainLooper()).postDelayed({
            rootView.removeView(errorBanner)
        }, 3000)
    }

    // funzione che apre un dialog nel quale vengono spiegati i requisiti di sicurezza (informazioni) che deve avere la password
    private fun showPasswordInformation(){

        val infoView = LayoutInflater.from(this).inflate(R.layout.information_dialog_layout, null)

        val titleText = infoView.findViewById<TextView>(R.id.infoDialogTitleText)
        val messageText = infoView.findViewById<TextView>(R.id.infoDialogMessageText)
        val closeDialogBtn = infoView.findViewById<Button>(R.id.closeInfoDialogButton)

        titleText.text = resources.getString(R.string.PasswordInfoText)
        messageText.text = resources.getString(R.string.PasswordInformationMessage)

        val infoDialog = MaterialAlertDialogBuilder(this)
            .setView(infoView)
            .show()

        closeDialogBtn.setOnClickListener {
            infoDialog.cancel()
        }

    }

    // funzione che apre un dialog che spiega il perchè viene richiesto il peso.
    private fun showWeightInformation(){

        val infoView = LayoutInflater.from(this).inflate(R.layout.information_dialog_layout, null)

        val titleText = infoView.findViewById<TextView>(R.id.infoDialogTitleText)
        val messageText = infoView.findViewById<TextView>(R.id.infoDialogMessageText)
        val closeDialogBtn = infoView.findViewById<Button>(R.id.closeInfoDialogButton)

        titleText.text = resources.getString(R.string.WeightInfoTitle)
        messageText.text = resources.getString(R.string.WeightInfoMessage)

        val infoDialog = MaterialAlertDialogBuilder(this)
            .setView(infoView)
            .show()

        closeDialogBtn.setOnClickListener {
            infoDialog.cancel()
        }

    }

}