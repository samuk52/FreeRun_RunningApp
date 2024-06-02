package it.insubria.freerun_runningapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.RadioGroup
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.android.material.textfield.TextInputLayout
import it.insubria.freerun_runningapp.Other.User
import it.insubria.freerun_runningapp.R
import java.lang.IndexOutOfBoundsException

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_NAME = "name"
private const val ARG_WEIGHT = "weight"
private const val ARG_GENDER = "gender"

class EditProfileFragment : Fragment() {

    private lateinit var user: User

    private lateinit var etName: TextInputLayout
    private lateinit var integerWeightPicker: NumberPicker
    private lateinit var decimalWeightPicker: NumberPicker
    private lateinit var avatarImageView: ImageView
    private lateinit var radioGroup: RadioGroup

    // parameters
    private var name: String? = null
    private var weight: String? = null
    private var gender: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // recupero gli argomenti passati al fragment
        arguments?.let {
            name = it.getString(ARG_NAME)
            weight = it.getString(ARG_WEIGHT)
            gender = it.getString(ARG_GENDER)
        }
        user = User.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etName = view.findViewById(R.id.editNameText)
        radioGroup = view.findViewById(R.id.editProfileRadioGroup)
        avatarImageView = view.findViewById(R.id.editAvatarImageView)
        integerWeightPicker = view.findViewById(R.id.editIntegerWeightPicker)
        integerWeightPicker.minValue = 0
        integerWeightPicker.maxValue = 300
        decimalWeightPicker = view.findViewById(R.id.editDecimalWeightPicker)
        decimalWeightPicker.minValue = 0
        decimalWeightPicker.maxValue = 9

        // gestico il pulsante per terminare le modifiche
        view.findViewById<Button>(R.id.doneButton).setOnClickListener{
            // TODO implementare comportamento
            editCompleted()
        }

        updateUI()
    }

    private fun updateUI(){
        // set editText name
        etName.editText!!.setText(name)
        // set integer and decimal weight numeric picker value
        val arrayValueWeight = weight!!.split(".")
        try {
            integerWeightPicker.value = arrayValueWeight[0].toInt()
            decimalWeightPicker.value = arrayValueWeight[1].toInt()
        }catch (e: IndexOutOfBoundsException){
            integerWeightPicker.value = arrayValueWeight[0].toInt()
            decimalWeightPicker.value = 0
        }
        // set gender
        when(gender){
            "Man" -> {
                radioGroup.check(R.id.editManRadioButton)
                avatarImageView.setImageResource(R.drawable.runner_man) // modifico l'avatar
            }
            "Woman" -> {
                radioGroup.check(R.id.editWomanRadioButton)
                avatarImageView.setImageResource(R.drawable.runner_woman) // modifico l'avatar
            }
        }
    }

    // metodo che viene eseguito quando il pulsante edit Button viene cliccato
    private fun editCompleted(){
        // il nome lo aggiorno solo se il contenuto della editText non è vuoto
        if(etName.editText!!.text.toString().isNotBlank()) {
            // recupero il nome
            val name = etName.editText!!.text.toString()
            user.setName(name)
        }
        // recupero i vari dati
        var gender = ""
        when(radioGroup.checkedRadioButtonId){
            R.id.editManRadioButton -> { gender = "Man" }
            R.id.editWomanRadioButton -> { gender = "Woman" }
        }
        val weight = integerWeightPicker.value + (decimalWeightPicker.value / 10f)

        // aggiorno i dati dell'utente
        user.setWeight(weight)
        user.setGender(gender)

        // apro il fragment del profilo
        openProfileFragment()
    }

    private fun openProfileFragment(){
        parentFragmentManager.commit {
            setReorderingAllowed(true)
            replace<ProfileFragment>(R.id.fragmentContainerView)
        }
    }

    companion object {
        // Use factory method to create a new instance of
        // this fragment using the provided parameters.
        @JvmStatic
        fun newInstance(name: String, weight: String, gender: String) =
            EditProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_NAME, name)
                    putString(ARG_WEIGHT, weight)
                    putString(ARG_GENDER, gender)
                }
            }
    }
}