package com.pucosa.pucopointManager.ui.login

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.pucosa.pucopointManager.models.LoginModel

class LoginViewModel(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    val data = MutableLiveData(LoginModel())
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    var mAuth = FirebaseAuth.getInstance()
    var numCount = 0

    fun login(Email: String, Password: String, progressbar: ProgressBar?): Int {

        try {
            progressbar?.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        progressbar?.visibility = View.INVISIBLE
                        numCount = 1

                    } else {
                        progressbar?.visibility = View.INVISIBLE
                        Log.d(Login.TAG, "login: error")
                        Toast.makeText(context, "User doesn't exist", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            return numCount
        } catch (e: Exception) {
            Toast.makeText(context, "error in login function : $e", Toast.LENGTH_SHORT)
                .show()
            numCount = 0
            return numCount
        }
    }
}
