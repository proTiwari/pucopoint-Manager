package com.pucosa.pucopointManager.ui.login

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.FragmentLoginBinding

class Login : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var navController: NavController
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(requireActivity())[LoginViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.progressbar.visibility = View.INVISIBLE
        // login button click
        binding.loginId.setOnClickListener {
            if (binding.EdtEmail.text.toString() != "" && binding.EdtPassword.text.toString() != "") {

                // navigation
                navController = Navigation.findNavController(view)
                if (viewModel.login(
                        binding.EdtEmail.text.toString(),
                        binding.EdtPassword.text.toString(),
                        binding.progressbar
                    ) == 1
                ) {
                    navController.navigate(R.id.action_login2_to_pucoPointList)
                }
            } else {
                Toast.makeText(requireContext(), "fill all the fields", Toast.LENGTH_SHORT).show()
            }

        }
    }

    companion object {
        const val TAG = "Login"
    }
}