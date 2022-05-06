package com.pucosa.pucopointManager.ui.login

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.models.model

class LoginViewModel(application: Application): AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    val data = MutableLiveData(model())
    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    private lateinit var navController: NavController
    var mAuth = FirebaseAuth.getInstance()
    var numCount = 0

    fun login(
        Email: String,
        Password: String,
        progressbar: ProgressBar?,
        view: View
    ): Int {

        try {
            progressbar?.visibility = View.VISIBLE
            mAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        progressbar?.visibility = View.INVISIBLE
                        navController = Navigation.findNavController(view)
                        navController.navigate(R.id.action_global_pucoPointList)

                    } else {
                        progressbar?.visibility = View.INVISIBLE
                        Log.d(Login.TAG, "login: error")
                        Toast.makeText(context, "User doesn't exist", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

        } catch (e: Exception) {
            Toast.makeText(context, "error in login function : $e", Toast.LENGTH_SHORT)
                .show()

        }
        return numCount
    }
}
