package it.insubria.freerun_runningapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import it.insubria.freerun_runningapp.Interfaces.RecyclerViewClickInterface
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.Other.Run
import it.insubria.freerun_runningapp.R

// TODO
//  1. implementare il listener per il click sugli elementi della recylerView
//  2. lo swap sugli elementi della recyclerView per la rimozione delle corse
//  3. metodi per la rimozione della corsa
class CustomAdapter(private val recyclerViewClickInterface: RecyclerViewClickInterface): RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    private val runs = arrayListOf<Run>()

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val distance: TextView
        val date: TextView

        init {
            distance = view.findViewById(R.id.distanceText)
            date = view.findViewById(R.id.dateRunText)

            view.setOnClickListener {
                recyclerViewClickInterface.onClick(layoutPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_custom_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.distance.text = runs[position].getDistance()
        holder.date.text = runs[position].getDate()
    }

    override fun getItemCount(): Int {
        return runs.size
    }

    // metodo che restitusice la corsa alla posizone specificata
    fun getRun(position: Int): Run{
        return runs[position]
    }

    // metodo che per aggiungere una corsa
    fun addRun(run: Run){
        runs.add(run)
        notifyItemInserted(runs.lastIndex)
    }

    // metodo per rimuovere la corsa alla posizione specificata
    fun removeRun(position: Int){
        runs.removeAt(position)
        notifyItemRemoved(position)
    }

    // metodo che ripristina il "ViewHolder" alla posizione specificata
    // viene invocato quando l'utente preme "NO" sul dialog che compare per confermare
    // la cancellazione della corsa.
    fun restoreRun(position: Int){
        notifyItemChanged(position)
    }

    fun runsIsEmpty(): Boolean{
        return runs.isEmpty()
    }

}