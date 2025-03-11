package com.app.hydratracker.ui.auth

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserViewModel(val context: Context) : ViewModel() {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid


    var _username = MutableLiveData<String>()
    var _email = MutableLiveData<String>()
    var _birth = MutableLiveData<String>()
    var _failed = MutableLiveData<String>()
    var _isDataLoaded =MutableLiveData<Boolean>()

    val username : LiveData<String> get() = _username
    val email : LiveData<String> get() = _email
    val birth : LiveData<String> get() = _birth
    val failed : LiveData<String> get() = _failed
    val isDataLoaded :LiveData<Boolean> get() = _isDataLoaded




    fun getUserInfo() {
        if (PreferencesManager(context).prefs.contains("name")) {
            loadUserDataFromPreferences()
        } else {
            getUserInfoFireBase()
        }
    }



    private fun loadUserDataFromPreferences() {
        val prefs = PreferencesManager(context).prefs
        _username.value = prefs.getString("username", "")
        _email.value = prefs.getString("email", "")
        _birth.value = prefs.getString("profileImage", "default_profile")
        _isDataLoaded.value = true
    }
    private fun getUserInfoFireBase() {
        val preferencesManager = PreferencesManager(context)
        if (userId != null) {
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        _email.value = document.getString("email")
                        _username.value = document.getString("fullName")
                        _birth.value = document.getString("birthDay")

                        email.value?.let { username.value?.let { it1 ->
                            birth.value?.let { it2 ->
                                preferencesManager.saveUserDataToPreferences(it, it1, it2)
                            }
                        } }


                        _isDataLoaded.value = true

                    }
                    else {
                        _failed.value = "No use data found."
                        _isDataLoaded.value = false
                    }
                }
                .addOnFailureListener { e ->
                    _failed.value = "Failed to fetch user info: ${e.message}"
                    _isDataLoaded.value = false
                }
        }
        else {
            _failed.value = "No authenticated user found."
            _isDataLoaded.value = false

        }
    }


}