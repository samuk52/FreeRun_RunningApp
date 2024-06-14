package it.insubria.freerun_runningapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import it.insubria.freerun_runningapp.R

// fragment dialog che mostra la barra di caricamento
class ProgressDialogFragment : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false)
    }

    override fun onStart() {
        super.onStart()
        // imposto il dialog non cancellabile e full-screen
        dialog?.setCancelable(false)
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        // rendo lo sfondo del dialog fragment trasparente.
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

}