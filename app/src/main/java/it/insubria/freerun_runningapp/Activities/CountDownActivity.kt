package it.insubria.freerun_runningapp.Activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.insubria.freerun_runningapp.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.properties.Delegates

class CountDownActivity : AppCompatActivity() {

    private lateinit var executor: ExecutorService
    private lateinit var countDownText: TextView

    private var countDown = 3
    private var countDownStopped = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)

        countDownText = findViewById(R.id.countDownText)

        // avvio il countDown
        startCountDown()

        val playPauseBtn = findViewById<Button>(R.id.playPauseBtn)
        val endActivityBtn = findViewById<Button>(R.id.endActivityBtn)

        playPauseBtn.setOnClickListener {
            if(!countDownStopped){ // se il countDown è attivo, lo fermo
                playPauseBtn.setBackgroundResource(R.drawable.play) // modifico l'immagine del pulsante
                endActivityBtn.visibility = Button.VISIBLE // rendo il pulsante per terminare l'attività visibile
                stopCountDown() //fermo il countDown
                countDownStopped = true
            }else{ // se il countDown è fermo, lo riattivo
                playPauseBtn.setBackgroundResource(R.drawable.pause) // modifico l'immagine del pulsante
                endActivityBtn.visibility = Button.INVISIBLE // rendo il pulsante per terminare l'attività invisibile
                startCountDown() // riattivo il countdown
                countDownStopped = false
            }
        }

        endActivityBtn.setOnClickListener {
            showEndActivityDialog()
        }
    }

    // metodo che avvia il countDown
    private fun startCountDown(){
        executor = Executors.newSingleThreadExecutor()
        val handler = Handler(mainLooper)
        executor.execute {
            while (countDown > 0){
                try {
                    handler.post {
                        countDownText.text = "$countDown"
                    }
                    Thread.sleep(1000)
                    countDown -= 1
                }catch (e: InterruptedException) {
                    break // in caso di interrupt exeption, chiudo
                }
            }
            if(countDown == 0) {
                // TODO avviare activity che monitora la corsa
                openTrackingActivity()
            }
        }
    }

    // metodo che interrope il countDown, per interromperlo spegne/disattiva l'ecexutor (thread) che si occupa
    // di eseguire il countdown
    private fun stopCountDown(){
        executor.shutdownNow()
    }

    // metodo che mostra un dialog che chiede all'utente se vuole terminare l'attività
    private fun showEndActivityDialog(){
        // recupero la view
        val view = LayoutInflater.from(this).inflate(R.layout.end_activity_dialog_layout, null)
        // creo il dialog
        val endActivityDialog = MaterialAlertDialogBuilder(this).setView(view).show()
        // gestisco quando viene premuto il pulsante che termina l'attività
        view.findViewById<Button>(R.id.endActivityDialognButton).setOnClickListener {
            openHomeActivity()
        }
        // gestisco quando viene premuto il pulsante che chiude il dialog
        view.findViewById<Button>(R.id.closeEndActivityDialogButton).setOnClickListener {
            endActivityDialog.cancel()
        }
    }

    // metodo che apre la home activity
    private fun openHomeActivity(){
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

    // metodo che apre la tracking activity
    private fun openTrackingActivity(){
        val intent = Intent(this, TrackingActivity::class.java)
        startActivity(intent)
    }

}