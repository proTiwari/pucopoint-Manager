package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.annotation.SuppressLint
import android.content.Context
import android.net.*
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.pucosa.pucopointManager.ui.login.Login


internal object CheckInternet {
    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    fun getConnectivityStatusString(context: Context) {
        val connectivityManager = context.getSystemService(ConnectivityManager::class.java)
        connectivityManager?.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network : Network) {
//                Log.e(Login.TAG, "The default network is now: " + network)
                Toast.makeText(context,"Network available", Toast.LENGTH_LONG).show()
            }

            override fun onLost(network : Network) {
//                Log.e(Login.TAG, "The application no longer has a default network. The last default network was " + network)
                Toast.makeText(context,"Network lost", Toast.LENGTH_LONG).show()
            }

            override fun onCapabilitiesChanged(network : Network, networkCapabilities : NetworkCapabilities) {
//                Log.e(Login.TAG, "The default network changed capabilities: " + networkCapabilities)
                //    Toast.makeText(requireContext(),"The default network changed capabilities: \" + $networkCapabilities",Toast.LENGTH_LONG).show()

            }

            override fun onLinkPropertiesChanged(network : Network, linkProperties : LinkProperties) {
//                Log.e(Login.TAG, "The default network changed link properties: " + linkProperties)
                //  Toast.makeText(requireContext(),"The default network changed link properties: \" + $linkProperties",Toast.LENGTH_LONG).show()

            }
        })
    }
}