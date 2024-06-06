package it.insubria.freerun_runningapp.Fragments

import android.graphics.Canvas
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import it.insubria.freerun_runningapp.Adapter.CustomAdapter
import it.insubria.freerun_runningapp.Interfaces.RecyclerViewClickInterface
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.Other.Run
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator

class ActivitiesFragment : Fragment(), RecyclerViewClickInterface {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var adapter: CustomAdapter
    private lateinit var guiUtilities: GuiUtilities

    // callback che rileva eventi di swap verso sinistra sullo specifico viewHolder della recylerView
    private val swipeCallback: ItemTouchHelper.SimpleCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            // non ci interessa
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val position = viewHolder.layoutPosition// recupero la posizione dell'adapter position nella recylerView
            guiUtilities.showAlertDialog(
                resources.getString(R.string.DeleteRunMessage),
                {
                    adapter.restoreRun(position)
                },
                {
                    adapter.removeRun(position)
                    // TODO chiamare metodo del databaseManager che rimuove la corsa dal database.
                }
            )
        }

        // modifica lo stile dello swap
        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(requireActivity().getColor(R.color.red))
                .addSwipeLeftLabel(resources.getString(R.string.DeleteText))
                .setSwipeLeftLabelColor(requireActivity().getColor(R.color.white))
                .addSwipeLeftActionIcon(R.drawable.delete_icon_white)
                .create()
                .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseManager = DatabaseManager()
        guiUtilities = GuiUtilities(requireActivity())
        adapter = CustomAdapter(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_activities, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.adapter = adapter

        getUserRun()

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView) // attacco il touchHelper alla recylerView

    }

    // metodo che gestisce quando viene effettuato il click su un elemento della recylerView
    override fun onClick(position: Int) {
        // TODO aprire fragment per mostrare i dati della corsa
    }

    // metodo che recupera dal database le corse effettuate dall'utente
    private fun getUserRun(){
        databaseManager.getUserRuns().addOnSuccessListener { snapshot ->
            for (document in snapshot.documents) {
                val id = document.id
                val date = document.getString("date") ?: "--/--/--"
                val time = document.getString("time") ?: "--/--/--"
                val distance = document.getString("distance") ?: "--"
                val avgPace = document.getString("avgPace") ?: "_'__\""
                val calories = document.getString("calories") ?: "--"
                val locations = document.get("locations") as ArrayList<LatLng>
                val run = Run(id, date, distance, time, calories, avgPace, locations)
                adapter.addRun(run)
                //DEBUG
                println("run -> $run")
            }
        }
    }
}