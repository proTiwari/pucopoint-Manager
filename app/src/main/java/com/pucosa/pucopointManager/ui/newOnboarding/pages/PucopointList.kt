package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.PucopointListBinding
import com.pucosa.pucopointManager.databinding.SinglerowBinding
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.roomDatabase.AppDatabase
import com.pucosa.pucopointManager.ui.newOnboarding.pages.recycle.CustomAdapter
import kotlinx.coroutines.launch


class PucopointList : Fragment() {
    private lateinit var binding: PucopointListBinding
//    private var _binding = _binding!!
    private lateinit var navController: NavController
    var userAdapter: CustomAdapter? = null
    private lateinit var bindingSinglerowBinding: SinglerowBinding
    private var bottomNavigationView: BottomNavigationView? = null

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
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

        deletePreviousDatabase()

        navController = Navigation.findNavController(view)
        bottomNavigationView?.itemIconTintList = null
        binding.bottomAppBar.menu.findItem(R.id.notification).setOnMenuItemClickListener {
            navController.navigate(R.id.action_pucoPointList_to_notificationFragment)
            true
        }

        binding.bottomAppBar.menu.findItem(R.id.plusicon).setOnMenuItemClickListener {
            navController.navigate(R.id.action_pucoPointList_to_onboarding_shopkeeper_info)
            true
        }

        binding.bottomAppBar.menu.findItem(R.id.payment).setOnMenuItemClickListener {
            navController.navigate(R.id.action_pucoPointList_to_paymentList)
            true
        }

        setListAdapter()

    }

    private fun deletePreviousDatabase() {
        val db = AppDatabase.getDatabase(context!!)
        viewLifecycleOwner.lifecycleScope.launch{
            db.shopkeeperDatabaseMethods().deleteShopLocationrInfo()
            db.shopkeeperDatabaseMethods().deleteShopLocationrInfo()
            db.shopkeeperDatabaseMethods().deleteShop()
            db.shopkeeperDatabaseMethods().deleteAadhaar()
            db.shopkeeperDatabaseMethods().deleteSignature()
        }
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
//        val linearLayoutManager = LinearLayoutManager(context)

            val linearLayoutManager = StaggeredGridLayoutManager(
                1,  //The number of Columns in the grid
                LinearLayoutManager.VERTICAL
            )
            binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.itemAnimator = null
        binding.recycler.adapter = userAdapter!!
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.searchmenu, menu)

        val searchView = menu.findItem(R.id.search).actionView as SearchView


//        menu.findItem(R.id.search).setOnMenuItemClickListener {
//            val direction = PucopointListDirections.actionPucoPointListToMapsActivity(map)
//            navController.navigate(direction)
//        }



        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s:String?): Boolean {
                Log.d(TAG, "onQueryTextSubmit: $s")
                println("1 $s")
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
        const val TAG = "PucopointList"
    }
}