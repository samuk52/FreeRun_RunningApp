package it.insubria.freerun_runningapp.Activities

import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

// TODO modificare metodo showErrorSignUpMessage, il quale deve mostare o un banner o un Toast con il layout definito sta mattina
class SignUpActivity : AppCompatActivity() {

    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var databaseManager: DatabaseManager
    private lateinit var guiUtilities: GuiUtilities

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        authenticationManager = AuthenticationManager()
        databaseManager = DatabaseManager()
        guiUtilities = GuiUtilities(this)

        // recupero il le tre textField presenti nel form di registrazione
        val nameTextField = findViewById<TextInputLayout>(R.id.nameTextField)
        val emailTextField = findViewById<TextInputLayout>(R.id.emailSignUp)
        val passwordTextField = findViewById<TextInputLayout>(R.id.passwordSignUp)

        val integerWeightPicker = findViewById<NumberPicker>(R.id.integerWeightPicker)
        integerWeightPicker.minValue = 0
        integerWeightPicker.maxValue = 300
        integerWeightPicker.value = 50

        val decimalWeightPicker = findViewById<NumberPicker>(R.id.decimalWeightPicker)
        decimalWeightPicker.minValue = 0
        decimalWeightPicker.maxValue = 9

        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)

        // gestisco il pulsante di chiusura del form di registrazione
        findViewById<Button>(R.id.closeSignUpButton).setOnClickListener{
            // chiudo l'activity con il metodo finish() in modo tale da poter
            // tornare nella precedente activity del backstack.
            finish()
        }

        // gestisco quando viene premuta la textView per le informazioni della password
        findViewById<TextView>(R.id.textPasswordInfo).setOnClickListener {
            guiUtilities.showInformationDialog(
                resources.getString(R.string.PasswordInfoText),
                resources.getString(R.string.PasswordInformationMessage)
            )
        }

        // gestisco quando viene premuta la textView del peso.
        findViewById<TextView>(R.id.weightTextInfo).setOnClickListener {
            guiUtilities.showInformationDialog(
                resources.getString(R.string.WeightInfoTitle),
                resources.getString(R.string.WeightInfoMessage)
            )
        }

        // gestisco il pulsante di registrazione
        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            // apro il fragment che mostra la progress bar del caricamento
            GuiUtilities.showProgressDialogFragment(supportFragmentManager)

            // recupero quale radioButton è stato premuto
            val genderRadioButton = findViewById<RadioButton>(radioGroup.checkedRadioButtonId) // recupero il radio button selezionato
            var gender = ""
            // il codice sotto è necessario in quando il testo dei RadioButton dipendono dalla lingua
            // impostata nel dispositivo, di conseguenza non posso salvare direttamente il testo dei RadioButton
            // in quanto ci sarebbe disomogeneità nei valori.
            when(genderRadioButton.id){
                R.id.manRadioButton -> { gender = "Man" }
                R.id.womanRadioButton -> { gender = "Woman"}
            }
            signUp(
                emailTextField.editText?.text.toString(),
                passwordTextField.editText?.text.toString(),
                nameTextField.editText?.text.toString(),
                integerWeightPicker.value + (decimalWeightPicker.value / 10.0f),
                gender
            )
        }
    }

    // funzione per effettuare la registrazione
    private fun signUp(email: String, password: String, name: String, weight: Float, gender: String){
        /*
        chiamo il metodo dell'authentication manager per effettuare la registrazione, esso restituisce un
        Task<AuthResult>? e lo utilizzo per chiamare un listener che verifichi se la registrazione è andata a buon fine o meno,
        oppure restituisce null sei uno o entrambi i parametri passati (email e password) sono vuoti.
         */
        //DEBUT
        println("--GENDER -> $gender")
        val authResult = authenticationManager.createAccount(email, password)
        if(authResult != null){
            authResult.addOnCompleteListener { task ->
                if(task.isSuccessful){
                    // se la registrazione è andata a buon fine, aggiungo il numero utente al database.
                    databaseManager.addNewUserToDB(name, weight, gender).addOnSuccessListener {
                        successSignUp()
                    }
                }else{
                    GuiUtilities.closeProgressDialogFragment()
                    guiUtilities.showErrorBanner(
                        findViewById(android.R.id.content),
                        resources.getString(R.string.ErrorSignUpTitle),
                        resources.getString(R.string.ErrorSignUpMessage)
                    )
                }
            }
        }else{
            GuiUtilities.closeProgressDialogFragment()
            guiUtilities.showErrorBanner(
                findViewById(android.R.id.content),
                resources.getString(R.string.ErrorSignUpTitle),
                resources.getString(R.string.ErrorSignUpMessage)
            )
        }
    }

    // metodo che viene eseguito nel caso in cui il login è avvenuto con successo
    private fun successSignUp(){
        // se la registrazione è avvenuta correttamente apro la home activity
        // guiUtilities.openHomeActivity(true)
        // apro il fragment per la richiesta dei permessi di posizione
        val openFragmentPermission = intent.getBooleanExtra("requestPermission", false)
        if(openFragmentPermission) {
            guiUtilities.showLocationPermissionFragment(supportFragmentManager)
        }else{ // se non devo aprire il frammento per la richiesta dei permessi, apro la home activity.
            guiUtilities.openHomeActivity(true)
        }
    }

}