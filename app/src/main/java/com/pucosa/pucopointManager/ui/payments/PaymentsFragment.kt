package com.pucosa.pucopointManager.ui.payments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayoutMediator
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.FragmentPaymentsBinding

class PaymentsFragment : Fragment() {
    companion object {
        fun newInstance() = PaymentsFragment()
    }

    private var _binding: FragmentPaymentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PaymentsViewModel
    private lateinit var navController:NavController
    private var bottomNavigationView: BottomNavigationView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaymentsViewModel::class.java)
        navController = Navigation.findNavController(view)
        binding.bottomAppBar.menu.findItem(R.id.payment).isChecked = true

        binding.bottomAppBar.menu.findItem(R.id.notification).setOnMenuItemClickListener {
            navController.navigate(R.id.action_paymentFragment_to_notificationFragment)
            true
        }
        bottomNavigationView?.itemIconTintList = null

        binding.bottomAppBar.menu.findItem(R.id.plusicon).setOnMenuItemClickListener {
            navController.navigate(R.id.action_paymentFragment_to_onboarding_shopkeeper_info2)
            true
        }

        binding.bottomAppBar.menu.findItem(R.id.home).setOnMenuItemClickListener {
            navController.navigate(R.id.action_paymentFragment_to_pucoPointList)
            true
        }
        initTabLayout()
    }

    private fun initTabLayout() {
        val tabFragmentList = arrayListOf<Fragment>(
            PaymentsListFragment(PaymentsListMode.PENDING),
            PaymentsListFragment(PaymentsListMode.CLEARED)
        )

        val tabTitleList = arrayListOf(
            "Pending Payments",
            "Payments History"
        )

        val pagerAdapter = PaymentsPagerAdapter(tabFragmentList, requireActivity().supportFragmentManager, lifecycle)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = tabTitleList[position]
        }.attach()
    }

    class PaymentsPagerAdapter(
        private val list: ArrayList<Fragment>,
        fragmentManager: FragmentManager,
        lifecycle: Lifecycle
    ): FragmentStateAdapter(fragmentManager, lifecycle) {
        override fun getItemCount(): Int {
            return list.size
        }

        override fun createFragment(position: Int): Fragment {
            return list[position]
        }

    }

}