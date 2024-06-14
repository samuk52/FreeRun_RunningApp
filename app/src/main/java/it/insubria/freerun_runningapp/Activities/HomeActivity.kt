package it.insubria.freerun_runningapp.Activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.RawContacts.Data
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.bottomnavigation.BottomNavigationView
import it.insubria.freerun_runningapp.Fragments.ActivitiesFragment
import it.insubria.freerun_runningapp.Fragments.ProfileFragment
import it.insubria.freerun_runningapp.Fragments.ProgressDialogFragment
import it.insubria.freerun_runningapp.Fragments.RunFragment
import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import it.insubria.freerun_runningapp.Other.User
import it.insubria.freerun_runningapp.R
import it.insubria.freerun_runningapp.Utilities.GuiUtilities

class HomeActivity : AppCompatActivity() {

    private lateinit var databaseManager: DatabaseManager
    private lateinit var authenticationManager: AuthenticationManager
    private lateinit var guiUtilities: GuiUtilities
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        databaseManager = DatabaseManager()
        authenticationManager = AuthenticationManager()
        guiUtilities = GuiUtilities(this)

        // recupero i dati dal database e inizializzo un nuovo Utente
        initUser()

        val bottomNavigationBar = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationBar.itemActiveIndicatorColor = getColorStateList(R.color.white)
        bottomNavigationBar.selectedItemId = R.id.item_run

        bottomNavigationBar.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.item_activities -> {
                    guiUtilities.openActivitiesFragment(supportFragmentManager)
                    true
                }
                R.id.item_run -> {
                    guiUtilities.openRunFragment(supportFragmentManager)
                    true
                }
                R.id.item_profile -> {
                    guiUtilities.openProfileFragment(supportFragmentManager)
                    true
                }
                else -> false
            }
        }

        GuiUtilities.showProgressDialogFragment(supportFragmentManager)

    }

    private fun initUser(){
        // recupero le informazioni dell'utente solo se non sono state ancora caricate.
        if (User.getInstance() == null) {
            databaseManager.getUserInfo().addOnSuccessListener { document ->
                val email = authenticationManager.getCurrentUser()!!.email!!
                val name = document.getString("name")!!
                val gender = document.getString("gender")!!
                val weight = document.getDouble("weight")!!.toFloat()
                user = User.newInstance(email, name, weight, gender)
                // TODO DEGUG remove
                Toast.makeText(this, "$name, $gender, $weight, $email", Toast.LENGTH_LONG).show()
                //DEBUG todo Remove
                println("User init -> $user")
                // chiudo il progress dialog fragment
                GuiUtilities.closeProgressDialogFragment()
            }
        }
    }
}