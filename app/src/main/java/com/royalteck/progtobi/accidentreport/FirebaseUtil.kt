package com.royalteck.progtobi.accidentreport

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.annotation.NonNull
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*
import kotlin.collections.ArrayList

object FirebaseUtil {
    private lateinit var caller: MainActivity
    lateinit var mFireStore: FirebaseFirestore
    private var mFirebaseUtil: FirebaseUtil? = null
    lateinit var mFirebaseAuth: FirebaseAuth
    lateinit var mAuthStateListener: FirebaseAuth.AuthStateListener
    private val RC_SIGN_IN = 123
    var isAdmin: Boolean = false

    fun openFbReference(callerActivity: MainActivity) {
        if (mFirebaseUtil == null) {
//            mFirebaseUtil = FirebaseUtil()
            mFireStore = FirebaseFirestore.getInstance()
            mFirebaseAuth = FirebaseAuth.getInstance()
            caller = callerActivity
            mAuthStateListener = object : FirebaseAuth.AuthStateListener {
                override fun onAuthStateChanged(@NonNull firebaseAuth: FirebaseAuth) {
                    // Choose authentication providers
                    if (firebaseAuth.getCurrentUser() == null) {
                        signin()
                    } else {
                        val userId = firebaseAuth.getUid()
                        if (userId != null) {
                            checkAdmin(userId)
                        }
                    }
                    Toast.makeText(callerActivity, "Welcome Back", Toast.LENGTH_SHORT).show()

                }
            }

        }

    }


    private fun checkAdmin(userId: String) {
        isAdmin = false
        val docRef = mFireStore.collection("administrators").document(userId)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d("DOCUMENT", "DocumentSnapshot data: ${document.data}")
                    isAdmin = true
                    caller.showMenu()
                } else {
                    Log.d("NO SUCH DOCUMENT", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("ERROR", "get failed with ", exception)
            }
    }

    fun signin() {
        val providers = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )


        // Create and launch sign-in intent
        caller!!.startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setLogo(R.mipmap.ic_launcher_round)
                .build(),
            RC_SIGN_IN
        )
    }

    fun attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener)
    }

    fun detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener)

    }

}
