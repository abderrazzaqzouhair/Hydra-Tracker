package com.app.hydratracker.ui.auth

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import com.app.hydratracker.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar


class SignUpFragment : Fragment() {


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_sign_up, container, false)
        var texshow = false
        val authManager = AuthManager(this)
        val createAccount = view.findViewById<TextView>(R.id.btnCreateAccount)
        val emailInput = view.findViewById<EditText>(R.id.ca_email_input)
        val passwordInput = view.findViewById<EditText>(R.id.ca_password_input)
        val fullNameInput = view.findViewById<EditText>(R.id.fullName_input)
        val etDateNaissance = view.findViewById<TextView>(R.id.etDateNaissance)
        val errorSTextView = view.findViewById<TextView>(R.id.errorSTextView)
        val logIn = view.findViewById<TextView>(R.id.tv_log_In)
        val showPass = view.findViewById<ImageView>(R.id.showPass)
        val termsTextView: TextView = view.findViewById(R.id.terms_of_service)
        val privacyPolicyTextView: TextView = view.findViewById(R.id.private_policy)

        termsTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://policies.google.com/terms"))
            startActivity(intent)
        }

        privacyPolicyTextView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.privacypolicygenerator.info/live.php?token=example"))
            startActivity(intent)
        }

        showPass.setOnClickListener {
            if (texshow) {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                texshow = false
            } else {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                texshow = true
            }
            passwordInput.setSelection(passwordInput.text.length)
        }

        logIn.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                LoginFragment()
            )?.commit()

        }

        etDateNaissance.setOnClickListener { showDatePickerDialog(etDateNaissance) }

        createAccount.setOnClickListener {
                val email = emailInput.text.toString().trim()
                val password = passwordInput.text.toString().trim()
                val fullName = fullNameInput.text.toString().trim()
                val birthDate = etDateNaissance.text.toString().trim()
                if (email.isEmpty() || password.isEmpty() || fullName.isEmpty() || birthDate.isEmpty()) {
                    Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailInput.error = "invalide Email"
                    return@setOnClickListener
                }

                if (password.length < 6) {
                    passwordInput.error = "Password must contain at least 6 characters"
                    return@setOnClickListener
                }


            authManager.signUp(
                emailInput.text.toString(),
                passwordInput.text.toString(),
                onSuccess = {
                    Toast.makeText(requireActivity(), "Sign-Up Successful!", Toast.LENGTH_SHORT).show()
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null) {
                        val userInfo = hashMapOf(
                            "fullName" to fullNameInput.text.toString(),
                            "email" to emailInput.text.toString(),
                            "birthDay" to etDateNaissance.text.toString()
                        )
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(user.uid)
                            .set(userInfo)
                            .addOnSuccessListener {
                                activity?.supportFragmentManager?.beginTransaction()?.replace(
                                    R.id.fragment_container,
                                    AuthCompletedFragment()
                                )?.commit()
                            }
                            .addOnFailureListener { e ->
                                errorSTextView.text = "Error saving user info:${authManager.getFriendlyErrorMessage(e.toString())}"
                            }
                    }
                },
                onFailure = { error ->
                    errorSTextView.text = authManager.getFriendlyErrorMessage(error)
                }
            )

        }

        return  view
    }

    private fun showDatePickerDialog(etDateNaissance: TextView) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_date_picker)

        val npDay = dialog.findViewById<NumberPicker>(R.id.npDay)
        val npMonth = dialog.findViewById<NumberPicker>(R.id.npMonth)
        val npYear = dialog.findViewById<NumberPicker>(R.id.npYear)
        val btnConfirm = dialog.findViewById<Button>(R.id.btnConfirm)

        val months = arrayOf("janv.", "févr.", "mars", "avr.", "mai", "juin", "juil.", "août", "sept.", "oct.", "nov.", "déc.")


        val calendar = Calendar.getInstance()
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)
        val currentMonth = calendar.get(Calendar.MONTH) // 0-based index
        val currentYear = calendar.get(Calendar.YEAR)

        npDay.minValue = 1
        npDay.maxValue = 31
        npDay.value = currentDay

        npMonth.minValue = 0
        npMonth.maxValue = months.size - 1
        npMonth.displayedValues = months
        npMonth.value = currentMonth

        npYear.minValue = 1950
        npYear.maxValue = currentYear
        npYear.value = currentYear - 18

        btnConfirm.setOnClickListener {
            val selectedYear = npYear.value
            val selectedMonth = npMonth.value
            val selectedDay = npDay.value
            val age = currentYear - selectedYear
            if (age < 18 || (age == 18 && selectedMonth > calendar.get(Calendar.MONTH)) || (age == 18 && selectedMonth == calendar.get(Calendar.MONTH) && selectedDay > calendar.get(Calendar.DAY_OF_MONTH))) {
                etDateNaissance.error = "You must be at least 18 years old"
                Toast.makeText(requireContext(), "You must be at least 18 years old", Toast.LENGTH_SHORT).show()
            } else {
                val selectedDate = "$selectedDay ${months[selectedMonth]} $selectedYear"
                etDateNaissance.setText(selectedDate)
                etDateNaissance.error = null
                dialog.dismiss()
            }
        }

        dialog.show()
    }


}