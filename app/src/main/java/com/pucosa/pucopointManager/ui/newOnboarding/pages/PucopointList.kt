package com.pucosa.pucopointManager.ui.newOnboarding.pages
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.PucopointListBinding
import com.pucosa.pucopointManager.databinding.SinglerowBinding
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.ui.newOnboarding.pages.recycle.CustomAdapter


class PucopointList : Fragment() {
    private lateinit var binding: PucopointListBinding
    private lateinit var navController: NavController
    var userAdapter: CustomAdapter? = null
    private lateinit var bindingSinglerowBinding: SinglerowBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = PucopointListBinding.inflate(inflater, container, false)
        bindingSinglerowBinding = SinglerowBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)



        setListAdapter()
        binding.plusicon.setOnClickListener {
            navController.navigate(R.id.action_pucoPointList_to_onboarding_shopkeeper_info)
        }
////            val action = PucopointListDirections.actionPucoPointListToOnboardingShopkeeperInfo()
////            closeKeyboard()
////            navController.navigate(action)
//
//        }
//        binding.bottomAppBar{
//
//            Navigation.findNavController(view).navigate(R.id.action_pucoPointList_to_paymentList)
////            val action = PucopointListDirections.actionPucoPointListToPaymentList()
////            navController.navigate(action)
//        }

    }


    private fun setListAdapter() {

        val query = Firebase.firestore.collection("pucopoints")
            .whereEqualTo("manager", Firebase.auth.currentUser!!.uid)

        val options: FirestoreRecyclerOptions<Pucopoint> = FirestoreRecyclerOptions.Builder<Pucopoint>()
            .setQuery(query, Pucopoint::class.java)
            .setLifecycleOwner(viewLifecycleOwner)
            .build()


        try {
            userAdapter = CustomAdapter(
                context,
                options = options,
                loadingComplete = {
                }
            ) { model, position ->
                val direction =
                    PucopointListDirections.actionPucoPointListToShopkeeperFullDetail(model)
                navController.navigate(direction)
            }
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
        val linearLayoutManager = LinearLayoutManager(context)
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.itemAnimator = null
        binding.recycler.adapter = userAdapter!!
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.searchmenu,menu)
        val searchView = menu.findItem(R.id.search_view).actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s:String?): Boolean {
                Log.d(TAG, "onQueryTextSubmit: $s")
                if (s != null) {
                    userAdapter?.search(s)
                }
                return true
            }
            override fun onQueryTextChange(s:String?): Boolean {
                Log.d(TAG, "onQueryTextChange: $s")
                if (s != null) {
                    userAdapter?.search(s)
                }
                return true
            }
        })
    }
    companion object {
        private const val TAG = "PucopointList"
    }
}