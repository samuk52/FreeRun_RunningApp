package it.insubria.freerun_runningapp.Fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.TrackingRunComponents.StepCounter
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

class NotificationPermissionFragment : DialogFragment() {

    private lateinit var guiUtilities: GuiUtilities
    private lateinit var stepCounter: StepCounter

    private val permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        // se il sensore è presente allora mostro il fragment per chiederne i permessi
        if(stepCounter.isStepSensorPresent()){
            guiUtilities.showActivityRecognitionPermissionFragment(requireActivity().supportFragmentManager)
        }else{ // altrimenti informo l'utente che il sensore non è disponibile sul suo dispositivo.
            guiUtilities.showActivitySensorNotDetectedFragment(requireActivity().supportFragmentManager);
        }

        this.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guiUtilities = GuiUtilities(requireActivity())
        stepCounter = StepCounter(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.notificationPermissionButton).setOnClickListener {
            permissionRequestLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}