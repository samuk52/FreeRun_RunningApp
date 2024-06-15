package it.insubria.freerun_runningapp.Fragments

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

class ActivityRecognitionPermissionFragment : DialogFragment() {

    private lateinit var guiUtilities: GuiUtilities

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        // indipendentemente dal fatto che i permessi sono stati concessi o meno, apro la homeActivity.
        guiUtilities.openHomeActivity(true)
        this.dismiss()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        guiUtilities = GuiUtilities(requireActivity());
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activity_recognition_permission, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.requestActivityRecognitionPermissionButton).setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }

    }

    override fun onStart() {
        super.onStart()
        dialog?.setCancelable(false);
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

}