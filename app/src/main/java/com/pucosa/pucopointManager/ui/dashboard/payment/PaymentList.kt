package com.pucosa.pucopointManager.ui.dashboard.payment

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.PaymentListBinding
import com.pucosa.pucopointManager.models.Pucopoint


class PaymentList : Fragment() {
    private lateinit var binding: PaymentListBinding
    private lateinit var navController: NavController
    var userAdapter: PaymentCustomAdapterClass? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = PaymentListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setListAdapter()

        binding.plusicon.setOnClickListener{
            closeKeyboard()
            navController.navigate(R.id.action_paymentFragment_to_onboarding_shopkeeper_info2)
        }
    }

    private fun setListAdapter() {

        val query = Firebase.firestore.collection("pucopoints")

        val options: FirestoreRecyclerOptions<Pucopoint> = FirestoreRecyclerOptions.Builder<Pucopoint>()
            .setQuery(query, Pucopoint::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()

        userAdapter = PaymentCustomAdapterClass(
            options = options,
            loadingComplete = {

            },
            onItemClicked = { model, position ->
            //val direction = PaymentsFragmentDirections.actionPaymentFragmentToPaymentFullDetail(model as Model)
            //navController.navigate(direction)
        }
        )

        val linearLayoutManager = LinearLayoutManager(context)
        binding.paymentrecyclerview.layoutManager = linearLayoutManager
        binding.paymentrecyclerview.adapter = userAdapter!!
    }

    private fun closeKeyboard() {
        // this will give us the view which is currently focus in this layout
        val view = requireActivity().currentFocus
        // if nothing is currently focus then this will protect the app from crash
        if (view != null) {
            // now assign the system service to InputMethodManager
            val manager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.searchmenu,menu)
//        val searchView = menu.findItem(R.id.search_view).actionView as SearchView
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(s:String?): Boolean {
//                Log.d(TAG, "onQueryTextSubmit: $s")
//                if (s != null) {
//                    userAdapter?.search(s)
//                }
//                return true
//            }
//            override fun onQueryTextChange(s:String?): Boolean {
//                Log.d(TAG, "onQueryTextChange: $s")
//                if (s != null) {
//                    userAdapter?.search(s)
//                }
//                return true
//            }
//        })
//    }

    companion object {
        private const val TAG = "paymentList"
    }
}

