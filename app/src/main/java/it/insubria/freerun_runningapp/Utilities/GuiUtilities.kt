package it.insubria.freerun_runningapp.Utilities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.ContentInfoCompat.Flags
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.insubria.freerun_runningapp.Activities.CountDownActivity
import it.insubria.freerun_runningapp.Activities.HomeActivity
import it.insubria.freerun_runningapp.Activities.LoginActivity
import it.insubria.freerun_runningapp.Activities.MainActivity
import it.insubria.freerun_runningapp.Activities.SignUpActivity
import it.insubria.freerun_runningapp.Activities.TrackingActivity
import it.insubria.freerun_runningapp.Activities.TrackingRecapActivity
import it.insubria.freerun_runningapp.Fragments.ActivitiesFragment
import it.insubria.freerun_runningapp.Fragments.ActivityRecognitionPermissionFragment
import it.insubria.freerun_runningapp.Fragments.ActivitySensorNotDetectedFragment
import it.insubria.freerun_runningapp.Fragments.EditProfileFragment
import it.insubria.freerun_runningapp.Fragments.LocationPermissionFragment
import it.insubria.freerun_runningapp.Fragments.NoActivitiesFoundFragment
import it.insubria.freerun_runningapp.Fragments.NotificationPermissionFragment
import it.insubria.freerun_runningapp.Fragments.ProfileFragment
import it.insubria.freerun_runningapp.Fragments.ProgressDialogFragment
import it.insubria.freerun_runningapp.Fragments.RunFragment
import it.insubria.freerun_runningapp.Fragments.ShowRunDetailsFragment
import it.insubria.freerun_runningapp.R

// TODO creare metodi per apertura frammenti e metodi per apertura dei vari dialog e alert
class GuiUtilities(private val context: Context) {

    // -- METODI PER APRIRE LE ACTIVITY -- //
    fun openMainActivity(requestPermission: Boolean){
        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.putExtra("requestPermission", requestPermission)
        context.startActivity(intent)
    }

    // il parametro del metodo indica se l'activity va ricreata o meno
    fun openHomeActivity(create: Boolean){
        val intent = Intent(context, HomeActivity::class.java)
        if(create) { // se deve essere ricreata
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }else{
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // impostando il seguente FLAG se l'activity è già in cima allo stack,
            // (e dovrebbe esserlo in quanto sopra è stato dichiarato il FLAG CLER TOP
            // il quale va a rimuovere dal backStack tutte le activity al di sopra) essa
            // non verrà ricreata ma verrà semplicemente riportata in primo piano
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        context.startActivity(intent)
    }

    fun openSignUpActivity(requestPermission: Boolean){
        val intent = Intent(context, SignUpActivity::class.java)
        intent.putExtra("requestPermission", requestPermission)
        context.startActivity(intent)
    }

    fun openLoginActivity(requestPermission: Boolean){
        val intent = Intent(context, LoginActivity::class.java)
        intent.putExtra("requestPermission", requestPermission)
        context.startActivity(intent)
    }

    fun openCountDownActivity(){
        val intent = Intent(context, CountDownActivity::class.java)
        context.startActivity(intent)
    }

    fun openTrackingActivity(){
        val intent = Intent(context, TrackingActivity::class.java)
        context.startActivity(intent)
    }

    fun openTrackingRecapActivity(time: String, distance: Float, calories: Int, avgPace: Float, locations: ArrayList<String>){
        val intent = Intent(context, TrackingRecapActivity::class.java)
        // passo all'activity i vari dati
        intent.putExtra("time", time)
        intent.putExtra("distance", distance)
        intent.putExtra("avgPace", avgPace)
        intent.putExtra("calories", calories)
        intent.putStringArrayListExtra("locations", locations)
        context.startActivity(intent)
    }

    fun openAppSettings(){
        val settingsIntent = Intent(
            android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        )
        context.startActivity(settingsIntent)
    }

    // -- METODI PER APRIRE I FRAGMENT -- //

    fun openRunFragment(fragmentManager: FragmentManager){
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace<RunFragment>(R.id.fragmentContainerView)
        }
    }

    fun openProfileFragment(fragmentManager: FragmentManager){
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace<ProfileFragment>(R.id.fragmentContainerView)
        }
    }

    fun openEditProfileFragment(fragmentManager: FragmentManager, name: String, weight: String, gender: String){
        fragmentManager.commit {
            setReorderingAllowed(true)
            val editProfileFragment = EditProfileFragment.newInstance(name, weight, gender)
            replace(R.id.fragmentContainerView, editProfileFragment)
        }

    }

    fun openActivitiesFragment(fragmentManager: FragmentManager){
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace<ActivitiesFragment>(R.id.fragmentContainerView)
        }
    }

    fun openShowRunDetailsFragment(fragmentManager: FragmentManager, time: String, distance: String, calories: String, avgPace: String, locations: ArrayList<String>){
        fragmentManager.commit {
            setReorderingAllowed(true)
            val showRunDetailsFragment = ShowRunDetailsFragment.newInstance(time, distance, calories, avgPace, locations)
            replace(R.id.fragmentContainerView, showRunDetailsFragment)
        }
    }

    fun openNoActivitiesFoundFragment(fragmentManager: FragmentManager){
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace<NoActivitiesFoundFragment>(R.id.fragmentContainerView)
        }
    }

    // -- METODI PER APRIRE I DIALOG e BANNER -- //
    fun showAlertDialog(message: String, positiveButtonMethod: () -> Unit){
        println("---- showAlertDialog ----")
        // recupero la view
        val view = LayoutInflater.from(context).inflate(R.layout.alert_activity_dialog_layout, null)
        // imposto il messaggio di allerta
        val tvMessage = view.findViewById<TextView>(R.id.alertActivityDialogText)
        tvMessage.text = message
        // creo il dialog
        val dialog = MaterialAlertDialogBuilder(context).setView(view).show()
        // gestisco quando viene premuto il pulsante di conferma
        view.findViewById<Button>(R.id.positiveButton).setOnClickListener {
            positiveButtonMethod()
            dialog.cancel()
        }
        // gestisco quando viene premuto il pulsante di negazione
        view.findViewById<Button>(R.id.negativeButton).setOnClickListener {
            dialog.cancel()
        }
    }

    // metodo uguale al precedente ma in più si passa il metodo che deve essere eseguito nel caso
    // venga premuto il pulsante "negativo" presente nel dialog.
    fun showAlertDialog(message: String, negativeButtonMethod: () -> Unit, positiveButtonMethod: () -> Unit){
        println("---- showAlertDialog ----")
        // recupero la view
        val view = LayoutInflater.from(context).inflate(R.layout.alert_activity_dialog_layout, null)
        // imposto il messaggio di allerta
        val tvMessage = view.findViewById<TextView>(R.id.alertActivityDialogText)
        tvMessage.text = message
        // creo il dialog
        val dialog = MaterialAlertDialogBuilder(context).setView(view).show()
        dialog.setCancelable(false) // impedisco il dialog venga chiuso quando si preme al di fuori di esso
        // gestisco quando viene premuto il pulsante di conferma
        view.findViewById<Button>(R.id.positiveButton).setOnClickListener {
            positiveButtonMethod()
            dialog.cancel()
        }
        // gestisco quando viene premuto il pulsante di negazione
        view.findViewById<Button>(R.id.negativeButton).setOnClickListener {
            negativeButtonMethod()
            dialog.cancel()
        }
    }

    fun showInformationDialog(title: String, message: String){
        val infoView = LayoutInflater.from(context).inflate(R.layout.information_dialog_layout, null)

        val tvTitle = infoView.findViewById<TextView>(R.id.infoDialogTitleText)
        val tvMessage = infoView.findViewById<TextView>(R.id.infoDialogMessageText)
        val closeDialogBtn = infoView.findViewById<Button>(R.id.closeInfoDialogButton)

        tvTitle.text = title
        tvMessage.text = message

        val infoDialog = MaterialAlertDialogBuilder(context)
            .setView(infoView)
            .show()

        closeDialogBtn.setOnClickListener {
            infoDialog.cancel()
        }
    }

    fun showErrorBanner(rootView: ViewGroup, title: String, message: String){
        // set della view contenete il layout di errore
        val errorBanner = LayoutInflater.from(context).inflate(R.layout.error_authentication_layout, rootView, false)
        // recupero le due textView, titolo e messaggio, della view
        val tvTitle = errorBanner.findViewById<TextView>(R.id.errorTitleText)
        val tvMessage = errorBanner.findViewById<TextView>(R.id.errorMessageText)
        // modifico il titolo e il messaggio della error view
        tvTitle.text = title
        tvMessage.text = message
        // aggiungo la view di errore
        rootView.addView(errorBanner)
        // avvio un thread (hanlder) che si occuperà di eliminare la view di error dopo 3 secondi dalla sua visualizzazione
        Handler(Looper.getMainLooper()).postDelayed({
            rootView.removeView(errorBanner)
        }, 3000)
    }

    // metodo che apre il fragment per la richiesta dei permessi di posizione
    fun showLocationPermissionFragment(fragmentManager: FragmentManager){
        val locationPermissionFragment = LocationPermissionFragment()
        locationPermissionFragment.show(fragmentManager, "location_permission_fragment")
    }

    // metodo che apre il fragment per la richiesta dei permessi delle notifiche
    fun showNotificationPermissionFragment(fragmentManager: FragmentManager){
        val notificationPermissionFragment = NotificationPermissionFragment()
        notificationPermissionFragment.show(fragmentManager, "notification_permission_fragment")
    }

    // metodo che apre il fragment per la richiesta dei permessi per il rilevamento dell'attività
    fun showActivityRecognitionPermissionFragment(fragmentManager: FragmentManager){
        val activityRecognitionPermissionFragment = ActivityRecognitionPermissionFragment()
        activityRecognitionPermissionFragment.show(fragmentManager, "activity_recognition_permission_fragment")
    }

    // metodo che apre il frammento che avvisa l'utente che il suo dispositivo non possiede il sensore che traccia i passi.
    fun showActivitySensorNotDetectedFragment(fragmentManager: FragmentManager){
        val activitySensorNotDetectedFragment = ActivitySensorNotDetectedFragment()
        activitySensorNotDetectedFragment.show(fragmentManager, "activity_sensor_not_detected_fragment")
    }

    companion object{

        private val progressDialogFragment = ProgressDialogFragment()

        // metodo che apre il progressDialogFragment
        fun showProgressDialogFragment(fragmentManager: FragmentManager){
            progressDialogFragment.show(fragmentManager, "progress_fragment")
        }

        // metodo che chiude il progressDialogFragment
        fun closeProgressDialogFragment(){
            progressDialogFragment.dismiss()
        }
    }
}