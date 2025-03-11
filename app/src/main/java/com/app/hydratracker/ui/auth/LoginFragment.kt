package com.app.hydratracker.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.app.hydratracker.R


class LoginFragment : Fragment() {
    lateinit var authManager:AuthManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authManager = AuthManager(this)
        authManager.initialize()
    }


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_login, container, false)

        val signInButton: CardView = view.findViewById(R.id.sign_in_button_google)
        signInButton.setOnClickListener {
            authManager.signInWithGoogle()
        }
        val btnLOGIN = view.findViewById<TextView>(R.id.btnLOGIN)
        val emailInput = view.findViewById<TextView>(R.id.email_input)
        val passwordInput = view.findViewById<EditText>(R.id.password_input)
        val forgotPassword = view.findViewById<TextView>(R.id.forgotPassword)
        val signUp = view.findViewById<TextView>(R.id.sign_up)
        val errorText = view.findViewById<TextView>(R.id.errorSTextView)

        signUp.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                SignUpFragment()
            )?.commit()
        }
        forgotPassword.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.replace(
                R.id.fragment_container,
                ForgotPassFragment()
            )?.commit()
        }

        btnLOGIN.setOnClickListener {
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            if (email.isEmpty() || password.isEmpty()) {
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
            authManager.login(emailInput.text.toString(), passwordInput.text.toString(),
                onSuccess = {
                    Toast.makeText(requireActivity(), "Login Successful!", Toast.LENGTH_SHORT).show()
                    activity?.supportFragmentManager?.beginTransaction()?.replace(
                        R.id.fragment_container,
                        AuthCompletedFragment()
                    )?.commit()
                },
                onFailure = { error ->
                    errorText.text = authManager.getFriendlyErrorMessage(error)}
            )

        }



        return view
    }


}