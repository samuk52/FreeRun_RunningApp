package it.insubria.freerun_runningapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.insubria.freerun_runningapp.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationBar.itemActiveIndicatorColor = getColorStateList(R.color.white)
        bottomNavigationBar.selectedItemId = R.id.item_run

    }
}