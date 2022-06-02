package com.pucosa.pucopointManager.ui.notification

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.NotificationFragmentBinding

class NotificationFragment : Fragment() {

    lateinit var binding: NotificationFragmentBinding
    public lateinit var navController:NavController
    private var bottomNavigationView: BottomNavigationView? = null

    companion object {
        fun newInstance() = NotificationFragment()
    }

    private lateinit var viewModel: NotificationViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = NotificationFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        bottomNavigationView?.itemIconTintList = null
        binding.bottomAppBar.menu.findItem(R.id.notification).isChecked = true
        binding.bottomAppBar.menu.findItem(R.id.home).setOnMenuItemClickListener {
            navController.navigate(R.id.action_notificationFragment_to_pucoPointList)
            true
        }

        binding.bottomAppBar.menu.findItem(R.id.plusicon).setOnMenuItemClickListener {
            navController.navigate(R.id.action_notificationFragment_to_onboarding_shopkeeper_info)
            true
        }

        binding.bottomAppBar.menu.findItem(R.id.payment).setOnMenuItemClickListener {
            navController.navigate(R.id.action_notificationFragment_to_paymentFragment)
            true
        }
    }
}