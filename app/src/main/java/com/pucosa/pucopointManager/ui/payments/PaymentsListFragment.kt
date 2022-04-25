package com.pucosa.pucopointManager.ui.payments

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.constants.DbCollections
import com.pucosa.pucopointManager.constants.PaymentStackholders
import com.pucosa.pucopointManager.databinding.FragmentPaymentsListBinding
import com.pucosa.pucopointManager.models.Payment

class PaymentsListFragment(val mode: PaymentsListMode) : Fragment() {
    private var _binding: FragmentPaymentsListBinding? = null
    private val binding get() = _binding!!

    private var paymentsListAdapter: PaymentsListAdapter? = null
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentsListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        setOnClickListeners()
        setLiveDataObservers()
        configureNotificationRecyclerView()
    }

    private fun setOnClickListeners() {}

    private fun setLiveDataObservers() {

    }

    private fun configureNotificationRecyclerView() {
        val currentUser = Firebase.auth.currentUser!!
        val query = Firebase.firestore.collectionGroup(DbCollections.PAYMENTS)
            .whereArrayContains(Payment::stackholders.name, PaymentStackholders.Pucoreads.name)
            .whereEqualTo(Payment::ppManagerId.name, currentUser.uid)
            .whereEqualTo(Payment::cleared.name, mode == PaymentsListMode.CLEARED)
            .orderBy(Payment::creationTimestamp.name, Query.Direction.DESCENDING).limit(50)

        val options: FirestoreRecyclerOptions<Payment> = FirestoreRecyclerOptions.Builder<Payment>()
            .setQuery(query, Payment::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        paymentsListAdapter = PaymentsListAdapter(
            fragment = this,
            options = options,
            loadingComplete = {
                binding.progressBar.isVisible = false
                binding.recyclerView.isVisible = it > 0
                binding.emptyListPlaceholder.isVisible = it <= 0
            },
            onClickItem = {
                val action = PaymentsFragmentDirections.actionPaymentFragmentToPaymentFullDetail(it)
                navController.navigate(action)
            }
        )

        val linearLayoutManager = LinearLayoutManager(context)
        val dividerItemDecoration = DividerItemDecoration(
            requireContext(),
            linearLayoutManager.orientation
        )
        binding.recyclerView.layoutManager = linearLayoutManager
        binding.recyclerView.adapter = paymentsListAdapter
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    companion object {
        private const val TAG = "PaymentsListFragment"
    }
}

enum class PaymentsListMode {
    PENDING, CLEARED
}