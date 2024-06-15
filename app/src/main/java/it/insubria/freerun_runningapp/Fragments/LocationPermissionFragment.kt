package it.insubria.freerun_runningapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

class LocationPermissionFragment : DialogFragment() {

    private lateinit var guiUtilities: GuiUtilities

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        // indipendentemente da se concessi o meno, apro il successivo fragment per i permessi.
        // TODO aprire fragment successivo,
        println("--- DENTRO ---")
        guiUtilities.showNotificationPermissionFragment(requireActivity().supportFragmentManager)
        this.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guiUtilities = GuiUtilities(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.requestLocationPermissionButton).setOnClickListener {
            // richiedo i permessi per la posizione
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }

    }

    override fun onStart() {
        super.onStart()
        // imposto il dialog come non cancellabile
        dialog?.setCancelable(false)
        // imposto l'altezza e la larghezza del fragment, mettendola a tutto schermo.
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }
}