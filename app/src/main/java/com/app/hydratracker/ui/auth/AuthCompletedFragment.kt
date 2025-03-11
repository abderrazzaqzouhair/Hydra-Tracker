package com.app.hydratracker.ui.auth

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.app.hydratracker.HomeActivity
import com.app.hydratracker.R


class AuthCompletedFragment : Fragment() {

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_auth_completed, container, false)
        view.findViewById<TextView>(R.id.btnContinue).setOnClickListener {
            Intent(requireContext(), HomeActivity::class.java).also {
                startActivity(it)
            }
        }
        return view
    }

}