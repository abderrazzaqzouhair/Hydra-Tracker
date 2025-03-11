package com.app.hydratracker.ui.auth

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.app.hydratracker.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class AuthManager(private val fragment: Fragment) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    fun initialize() {
        googleSignInLauncher = fragment.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                handleSignInResult(task)
            }
        }
    }

    init {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(), gso)
    }

    fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }

    private fun handleSignInResult(completedTask: com.google.android.gms.tasks.Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            account?.let { firebaseAuthWithGoogle(it) }
        } catch (e: ApiException) {
            Log.d("this", "Google Sign-In failed: ${e.statusCode}")
        }
    }



    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(fragment.requireActivity()) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    fragment.requireActivity().supportFragmentManager.beginTransaction().replace(
                        R.id.fragment_container,
                        AuthCompletedFragment()
                    ).commit()
                    Log.d("this", "Firebase authentication success: ${user?.displayName}")
                    user?.let {
                        val userName = it.displayName ?: "Nom inconnu"
                        val userEmail = it.email ?: "Email inconnu"
                        val userBirthDay = "01-01-2000"
                        val userPhotoUrl = it.photoUrl?.toString() ?: "Aucune photo"

                        val userInfo = hashMapOf(
                            "fullName" to userName,
                            "email" to userEmail,
                            "birthDay" to userBirthDay,
                            "photoUrl" to userPhotoUrl
                        )
                        val db = FirebaseFirestore.getInstance()
                        db.collection("users").document(user.uid)
                            .set(userInfo)
                            .addOnSuccessListener {

                            }
                            .addOnFailureListener { e ->

                            }
                    }

                    } else {
                        Log.d("this", "Firebase authentication failed: ${task.exception?.message}")
                    }
                }
            }




    fun signUp(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Sign-Up Failed")
                }
            }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "Login Failed")
                }
            }
    }
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }




    fun resetPassword(email: String): String {
        var message = "Password Reset Email Has Been Sent"
        if (isValidEmail(email)) {
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    message = if (task.isSuccessful) {
                        "Password Reset Email Has Been Sent"
                    } else {
                        "Failed to send reset email."
                    }
                }

        }else{
            message =  "Please enter a valid email."
        }
        return message


    }

    fun logout(send:()->Unit) {
        Log.d("this", "log out")
        auth.signOut()
        send()
    }

    fun getFriendlyErrorMessage(error: String): String {
        return when {
            "network error" in error.lowercase() -> "Connection problem."
            "auth credential is incorrect" in error.lowercase() -> "Incorrect password."
            "no user record" in error.lowercase() -> "User not found."
            "badly formatted" in error.lowercase() -> "Invalid email."
            else -> "An error occurred."
        }
    }


}
