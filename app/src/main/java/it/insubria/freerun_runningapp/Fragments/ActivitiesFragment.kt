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
import it.insubria.freerun_runningapp.Utilities.DataUtilities
import it.insubria.freerun_runningapp.Utilities.GuiUtilities
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.Objects

class ActivitiesFragment : Fragment(), RecyclerViewClickInterface {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var adapter: CustomAdapter
    private lateinit var guiUtilities: GuiUtilities
    private lateinit var dataUtilities: DataUtilities

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
            val run = adapter.getRun(position)
            guiUtilities.showAlertDialog(
                resources.getString(R.string.DeleteRunMessage),
                {
                    adapter.restoreRun(position)
                },
                {
                    // rimuovo la corsa dal database
                    databaseManager.removeUserRun(run.getId()).addOnSuccessListener {
                        // se la corsa è stata eliminata correttamente dal database, la elimino anche dalla recylerView
                        adapter.removeRun(position)
                    }
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
        dataUtilities = DataUtilities()
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
        val run = adapter.getRun(position)
        guiUtilities.openShowRunDetailsFragment(
            parentFragmentManager,
            run.getTime(),
            run.getDistance(),
            run.getCalories(),
            run.getAvgPace(),
            dataUtilities.serializeLatLngList(run.getLocations())
        )
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
                val locations = arrayListOf<LatLng>()
                // all'interno di firebase la lista di LatLng viene deserializzata in una lista
                // di hashMap string-double. per questo motivo vado a ri-serializzare la lista
                for(location in document.get("locations") as List<HashMap<String, Double>>){
                    val lat = location["latitude"] ?: 0.0
                    val lng = location["longitude"] ?: 0.0
                    locations.add(LatLng(lat, lng))
                }

                val run = Run(id, date, distance, time, calories, avgPace, locations)
                adapter.addRun(run)
                //DEBUG
                println("run -> $run")
            }
        }
    }
}