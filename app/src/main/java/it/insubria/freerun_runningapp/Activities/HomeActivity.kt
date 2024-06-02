package it.insubria.freerun_runningapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.RawContacts.Data
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.insubria.freerun_runningapp.Fragments.ActivitiesFragment
import it.insubria.freerun_runningapp.Fragments.ProfileFragment
import it.insubria.freerun_runningapp.Fragments.RunFragment
import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.Other.User
import it.insubria.freerun_runningapp.R

class HomeActivity : AppCompatActivity() {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        databaseManager = DatabaseManager()
        authenticationManager = AuthenticationManager()

        // recupero i dati dal database e inizializzo un nuovo Utente
        initUser()

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

    private fun initUser(){
        databaseManager.getUserInfo().addOnSuccessListener { document ->
            val email = authenticationManager.getCurrentUser()!!.email
            val name = document.getString("name")
            val gender = document.getString("gender")
            val weight = document.getDouble("weight")!!.toFloat()
            user = User.newInstance(email!!, name!!, weight, gender!!)
            //DEBUG todo Remove
            println("User init -> $user")
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