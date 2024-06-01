package it.insubria.freerun_runningapp.Fragments

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import it.insubria.freerun_runningapp.Activities.MainActivity
import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.R

// TODO 1.aggiornare le varie componenti dell'interfaccia utente, selezionare l'icon corretta in base a se l'utente è una domma o un uomo
//      quindi recuperare queste informazioni dal database
//  2. creare dialog o bottom sheet per aggiornamento dei dati (peso, genere e nome)
//  3. abilitare o meno gli switch dei vari permrssi in base a se quei permessi sono stati dati dall'utente
//  4. aprire activity impostazioni che permettano di modificare le autorizzazioni quando viene premuto uno switch dei setting
//
//
class ProfileFragment : Fragment() {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var authManager: AuthenticationManager

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvWeight: TextView
    private lateinit var tvGender: TextView

    private lateinit var avatarImageView: ImageView

    private lateinit var notificationSwitch: MaterialSwitch
    private lateinit var locationSwitch: MaterialSwitch
    private lateinit var physicalActivitySwitch: MaterialSwitch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseManager = DatabaseManager()
        authManager = AuthenticationManager()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvName = view.findViewById(R.id.nameProfileText)
        tvEmail = view.findViewById(R.id.emailProfileText)
        tvWeight = view.findViewById(R.id.weightProfileText)
        tvGender = view.findViewById(R.id.genderProfileText)

        avatarImageView = view.findViewById(R.id.avatarImageView)

        notificationSwitch = view.findViewById(R.id.notificationSwitch)
        locationSwitch = view.findViewById(R.id.locationSwitch)
        physicalActivitySwitch = view.findViewById(R.id.physicalActivitySwitch)

        // gestisco quando viene premuto il notification switch
        notificationSwitch.setOnClickListener{
            openAppSettings()
        }
        // gestisco quando viene premuto il location switch
        locationSwitch.setOnClickListener{
            openAppSettings()
        }
        // gestisco quando viene premuto il physicalActivitySwitch switch
        physicalActivitySwitch.setOnClickListener{
            openAppSettings()
        }

        // gestisco quando viene premuto il pulsante per modificare i dati del profilo
        view.findViewById<Button>(R.id.editProfileButton).setOnClickListener {
            // TODO implementare: aprire fragment che permette di modificare i dati
            openEditProfileFragment()
        }
        // gestisco quando viene premuto il pulsante per modificare i dati del profilo
        view.findViewById<Button>(R.id.logOutButton).setOnClickListener{
            // TODO apire info dialog che per confermare l'uscita dall'applicazione
            showConfirmLogOutDialog()
        }

        updateUI()

    }

    override fun onResume() {
        super.onResume()
        // aggiorno gli switch quando il FragmentProfile riprende il focus dopo aver aperto le impostazioni
        updatedSwitch()
    }

    // metodo che si occupa di aggiornare le componenti dell'interfaccia utente
    private fun updateUI(){
        databaseManager.getUserInfo().addOnSuccessListener { document ->
            tvName.text = document.get("name") as String
            tvWeight.text = (document.get("weight") as Double).toFloat().toString()
            tvEmail.text = authManager.getCurrentUser()?.email
            when(document.get("gender") as String){
                "Man" -> {
                    tvGender.text = resources.getString(R.string.ManText)
                    avatarImageView.setImageResource(R.drawable.runner_man) // modifico l'avatar
                }
                "Woman" -> {
                    tvGender.text = resources.getString(R.string.WomanText)
                    avatarImageView.setImageResource(R.drawable.runner_woman) // modifico l'avatar
                }
            }
        }

        updatedSwitch()

    }

    // metodo che aggiorna il "contenuto" dello switch
    private fun updatedSwitch(){
        notificationSwitch.isChecked = isNotificationPermissionGranted()
        locationSwitch.isChecked = isLocationPermissionGranted()
        physicalActivitySwitch.isChecked = isActivityPermissionGranted()
    }

    // metodo che verifica se i permessi per le notifiche sono stati concessi
    private fun isNotificationPermissionGranted(): Boolean{
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED)
    }

    // metodo che verifica se i permessi per la posizione sono stati concessi
    private fun isLocationPermissionGranted(): Boolean{
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
    }

    // metodo che verifica se i permessi per l'attività fisica sono stati concessi
    private fun isActivityPermissionGranted(): Boolean{
        return (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_GRANTED)
    }

    // metodo che apre l'activity delle impostazioni dell'applicazione
    private fun openAppSettings(){
        val settingsIntent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", requireActivity().packageName, null)
        )
        startActivity(settingsIntent)
    }

    // metodo che apre il frammento per la modifica del profilo
    private fun openEditProfileFragment(){
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            val editProfileFragment = EditProfileFragment.newInstance(
                tvName.text.toString(),
                tvWeight.text.toString(),
                tvGender.text.toString()
            )
            replace(R.id.fragmentContainerView, editProfileFragment)
        }
    }

    // metodo che mostra un dialog che chiede all'utente se vuole terminare l'attività
    private fun showConfirmLogOutDialog(){
        // recupero la view
        val view = LayoutInflater.from(requireActivity()).inflate(R.layout.alert_activity_dialog_layout, null)
        // imposto il messaggio di allerta
        val message = view.findViewById<TextView>(R.id.alertActivityDialogText)
        message.text = resources.getString(R.string.LogOutMessage)
        // creo il dialog
        val dialog = MaterialAlertDialogBuilder(requireActivity()).setView(view).show()
        // gestisco quando viene premuto il pulsante di conferma
        view.findViewById<Button>(R.id.positiveButton).setOnClickListener {
            // se l'utente conferma il logOut, eseguo l'apposito metodo
            logOut()
        }
        // gestisco quando viene premuto il pulsante di negazione
        view.findViewById<Button>(R.id.negativeButton).setOnClickListener {
            dialog.cancel()
        }
    }

    // metodo che effettua il log out dall'applicazione
    private fun logOut(){
        authManager.signOut()
        //DEBUG
        startActivity(Intent(requireActivity(), MainActivity::class.java))
    }
}