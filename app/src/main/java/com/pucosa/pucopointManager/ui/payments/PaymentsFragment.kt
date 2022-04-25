package com.pucosa.pucopointManager.ui.payments

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.pucosa.pucopointManager.databinding.FragmentPaymentsBinding

class PaymentsFragment : Fragment() {
    companion object {
        fun newInstance() = PaymentsFragment()
    }

    private var _binding: FragmentPaymentsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: PaymentsViewModel

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