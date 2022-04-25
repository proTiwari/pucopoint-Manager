package com.pucosa.pucopointManager

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide.init
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.safetynet.SafetyNetAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.ui.newOnboarding.NewOnboardingViewModel


class MainActivity : AppCompatActivity() {

    var authStateListener: FirebaseAuth.AuthStateListener? = null
    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(/*context=*/this)
        val firebaseAppCheck = FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            SafetyNetAppCheckProviderFactory.getInstance()
        )

        setContentView(R.layout.activity_main)
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        setAuthStateListener()
    }

    private fun setAuthStateListener() {
        authStateListener = FirebaseAuth.AuthStateListener {
            if(it.currentUser != null) {
                if(navController!!.currentDestination?.id == R.id.login2) {
                    navController!!.navigate(R.id.action_login2_to_pucoPointList)
                }
            }
            else {
                navController!!.navigate(R.id.action_global_login2)
            }
        }
        Firebase.auth.addAuthStateListener(authStateListener!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        Firebase.auth.removeAuthStateListener(authStateListener!!)
        authStateListener = null
    }

    fun payment(item: MenuItem) {
        navController?.navigate(R.id.action_pucoPointList_to_paymentList)
    }

    fun home(view: View) {
        navController?.navigate(R.id.action_paymentFragment_to_pucoPointList)
    }

    fun onLogoutClicked(item: MenuItem) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Clearance Confirmation")
            .setMessage("Do you really want to mark this payment as cleared?")
            .setPositiveButton("Yes, Logout") { dialog, which ->
                Firebase.auth.signOut()
                dialog.dismiss()
            }
            .setNegativeButton("No, Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

//    fun plus(view: View) {
//        navController?.navigate(R.id.action_paymentList_to_onboarding_shopkeeper_info)
//    }
//
//    fun pluspucopointlist(view: View) {
//        navController?.navigate(R.id.action_pucoPointList_to_onboarding_shopkeeper_info)
//    }
}