package it.insubria.freerun_runningapp.Utilities

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import it.insubria.freerun_runningapp.R

class MapUtilities(private val context: Context) {

    // metodo che disegna il percorso eseguito dall'utente durante la corsa.
    fun drawPolyline(googleMap: GoogleMap, locations: ArrayList<LatLng>){
        // disegno il percorso
        val polyline = googleMap.addPolyline(
            PolylineOptions()
            .addAll(locations))
        polyline.color = context.getColor(R.color.orange)
        polyline.jointType = JointType.ROUND

        // visulizzo i marker di inizio e fine corsa solo se è stata rilaveta almento una posizione
        // senza questo controllo, nel caso in cui non è stata rilevata nessuna posizione
        // viene sollevata una IndexOutBoundException dovuta all'accesso nella lista nella
        // righe 156 e 174
        if (locations.isNotEmpty()) {
            // aggiungo il marker che indica l'inzio della corsa
            googleMap.addMarker(
                MarkerOptions()
                    .position(locations[0])
                    .icon(createCircleIcon(context.getColor(R.color.green)))
            )

            // aggiungo il marker che indica la fine della corsa.
            googleMap.addMarker(
                MarkerOptions()
                    .position(locations[locations.lastIndex])
                    .icon(createCircleIcon(context.getColor(R.color.red)))
            )

            // calcolo i limiti della polyline, essi mi servono per zoomare sul percorso effettauto
            // dall'utente, per calcolare i limiti utilizzo un oggetto LatLngBounds.Builder()
            // al quale aggiungo i punti della polyline e lui tramite il metodo build ci restituisce
            // i limiti
            val builder = LatLngBounds.builder()
            //itero i punti della polyline e gli aggingo al builder
            for (point in polyline.points) {
                builder.include(point)
            }
            // recupero i limiti della polyline
            val bounds = builder.build()

            // sporto la telecamera di google maps sui limiti
            googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50))
        }
    }

    // funzione che crea i circhi di inizio e fine corsa che verranno visualizzati sulla mappa
    private fun createCircleIcon(color: Int): BitmapDescriptor {
        val diameter = 30
        val bitmap = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint()
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

}