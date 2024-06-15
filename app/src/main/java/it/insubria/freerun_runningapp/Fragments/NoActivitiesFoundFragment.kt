package it.insubria.freerun_runningapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

class NoActivitiesFoundFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_activities_found, container, false)
    }

}