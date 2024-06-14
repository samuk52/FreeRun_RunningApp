package it.insubria.freerun_runningapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CountDownActivity : AppCompatActivity() {

    private lateinit var executor: ExecutorService
    private lateinit var countDownText: TextView
    private lateinit var guiUtilities: GuiUtilities

    private var countDown = 3
    private var countDownStopped = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_count_down)

        guiUtilities = GuiUtilities(this)

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
            guiUtilities.showAlertDialog(resources.getString(R.string.EndActivtyMessage)){
                guiUtilities.openHomeActivity(false)
            }
        }

        handleOnBackPressed()

    }

    // gestisco quando viene premuto il pulsante "indietro" di android
    private fun handleOnBackPressed(){
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                // quando viene premuto non succede niente
            }
        })
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
                guiUtilities.openTrackingActivity()
            }
        }
    }

    // metodo che interrope il countDown, per interromperlo spegne/disattiva l'ecexutor (thread) che si occupa
    // di eseguire il countdown
    private fun stopCountDown(){
        executor.shutdownNow()
    }

}