package it.insubria.freerun_runningapp.Utilities

import com.google.android.gms.maps.model.LatLng
import java.lang.IndexOutOfBoundsException

class DataUtilities {

    // metodo che presa in input una lista di oggetti LatLng, la serializza in una lista di stringhe
    // questa operazione è necessaria in quando il metodo dell'intent che permette di recuperare
    // un oggetto serializzato richiede che il device abbiamo come sdk minimo il 33.
    fun serializeLatLngList(listToSerialize: ArrayList<LatLng>): ArrayList<String>{
        val list = arrayListOf<String>()
        for(item in listToSerialize){
            list.add("${item.latitude},${item.longitude}")
        }
        return list
    }

    // metodo che preso in input una lista di stringhe la deserializza in una lista
    // di oggetti LatLng, questo metodo è necessario in quando il metodo dell'intent
    // getSerializableExtra richiede che il device abbiamo come sdk minimo il 33, mentre
    // io voglio che l'app funzioni anche con sdk minori.
    fun deserializeLatLngList(listToDeserialize: ArrayList<String>): ArrayList<LatLng>{
        val list = arrayListOf<LatLng>()
        for(item in listToDeserialize){
            val latLngArray = item.split(",")
            list.add(LatLng(latLngArray[0].toDouble(), latLngArray[1].toDouble()))
        }
        return list
    }

    // metodo che restituisce il passo medio formattato
    fun getFormattedAvgPace(avgPace: Float): String{
        try {
            val avgPaceToFormat = String.format("%.2f", avgPace).split(".")
            return "${avgPaceToFormat[0]}'${avgPaceToFormat[1]}\""
        }catch (e: IndexOutOfBoundsException){
            return "_'__\""
        }
    }

}