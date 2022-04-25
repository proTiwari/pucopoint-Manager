package com.pucosa.pucopointManager.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

object Extensions {
    fun FirebaseAuth.AuthStateListener.applyInDisabledState(codeBlock: () -> Unit) {
        Firebase.auth.removeAuthStateListener(this)
        codeBlock()
        Firebase.auth.addAuthStateListener(this)
    }

    fun Int.toRupeesText(): String {
        return "₹ $this"
    }

    fun String.toRupeesText(): String {
        return "₹ $this"
    }
}