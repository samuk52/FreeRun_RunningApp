package it.insubria.freerun_runningapp.Other

import androidx.core.graphics.rotationMatrix
import androidx.core.view.ContentInfoCompat.Flags
import it.insubria.freerun_runningapp.Managers.AuthenticationManager
import it.insubria.freerun_runningapp.Managers.DatabaseManager
import java.io.Serializable

class User (private val email: String, private var name: String, private var weight: Float, private var gender: String){

    private val databaseManager = DatabaseManager()

    // -- GETTER -- //
    fun getEmail(): String{
        return email
    }

    fun getName(): String{
        return name
    }

    fun getWeight(): Float{
        return weight
    }

    fun getGender(): String{
        return gender
    }

    // -- SETTER -- //
    fun setName(name: String){
        // modifico il dato solo se è diverso da quello attuale
        if(this.name != name && name.isNotBlank()) {
            this.name = name
            databaseManager.updateName(name) // aggiorno il nome nel database
        }
    }

    fun setWeight(weight: Float){
        // modifico il dato solo se è diverso da quello attuale
        if (this.weight != weight) {
            this.weight = weight
            databaseManager.updateWeight(weight) // aggiorno il peso dell'utente nel database
        }
    }

    fun setGender(gender: String){
        // modifico il dato solo se è diverso da quello attuale
        if(this.gender != gender && gender.isNotBlank()) {
            this.gender = gender
            databaseManager.updateGender(gender) // aggiorno il genere dell'utente nel database
        }
    }

    override fun toString(): String {
        return "Email: $email, Name: $name, Weight: $weight, Gender: $gender"
    }

    companion object{

        @JvmStatic
        private var user: User? = null

        @JvmStatic
        fun newInstance(email: String, name: String, weight: Float, gender: String): User{
            if (user == null) {
                user = User(email, name, weight, gender)
            }
            return user!!
        }
        @JvmStatic
        fun getInstance(): User{
            return user!!
        }
        @JvmStatic
        fun reset(){
            user = null
        }

    }
}