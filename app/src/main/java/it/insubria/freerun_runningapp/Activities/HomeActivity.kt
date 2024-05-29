package it.insubria.freerun_runningapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.insubria.freerun_runningapp.Fragments.ActivitiesFragment
import it.insubria.freerun_runningapp.Fragments.ProfileFragment
import it.insubria.freerun_runningapp.Fragments.RunFragment
import it.insubria.freerun_runningapp.R

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationBar.itemActiveIndicatorColor = getColorStateList(R.color.white)
        bottomNavigationBar.selectedItemId = R.id.item_run

        bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_activities -> {
                    openActivitiesFragment()
                    true
                }
                R.id.item_run -> {
                    openRunFragment()
                    true
                }
                R.id.item_profile -> {
                    openProfileFragment()
                    true
                }
                else -> false
            }
        }
    }

    // metodo che apre l'activities fragment
    private fun openActivitiesFragment(){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<ActivitiesFragment>(R.id.fragmentContainerView)
        }
    }

    // metodo che apre il run fragments
    private fun openRunFragment(){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<RunFragment>(R.id.fragmentContainerView)
        }
    }

    // metodo che apre il profile fragments
    private fun openProfileFragment(){
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<ProfileFragment>(R.id.fragmentContainerView)
        }
    }
}