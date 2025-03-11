package com.app.hydratracker.ui.auth

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.app.hydratracker.R


class ForgotPassFragment : Fragment() {


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forgot_pass, container, false)
        val authManager = AuthManager(this)


        val emailInput = view.findViewById<EditText>(R.id.reset_email_input)
        val sendButton = view.findViewById<TextView>(R.id.btnsendLink)
        val errorMessage = view.findViewById<TextView>(R.id.errorSTextView)

        sendButton.setOnClickListener {
            val message = authManager.resetPassword(emailInput.text.toString())
            errorMessage.setTextColor(Color.RED)
            if("Been" in message){
                errorMessage.setTextColor(Color.GREEN)
            }
            errorMessage.text = message

        }
        return view
    }
}