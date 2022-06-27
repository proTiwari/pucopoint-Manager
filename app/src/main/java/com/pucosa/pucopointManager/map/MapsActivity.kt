package com.pucosa.pucopointManager.map

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.databinding.ActivityMapsBinding
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.models.model
import com.pucosa.pucopointManager.ui.newOnboarding.pages.PucopointList
import id.zelory.compressor.determineImageRotation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private val circles: ArrayList<Circle> = ArrayList()
    private val circles1: ArrayList<Circle> = ArrayList()
    private var circle1: Circle? = null
    private var circle: Circle? = null
    private var context: Context? = null
    private var count = 0
    private var markerArray: ArrayList<Marker> = ArrayList()
    private var marker: Marker? = null
    private var countcircles = 1
    private var countLocation = 0
    var userAdapter: MapAdapter? = null
    private lateinit var binding: ActivityMapsBinding
    private var locationArrayList: ArrayList<String>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val mapFragment = supportFragmentManager
            .findFragmentById(com.pucosa.pucopointManager.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        Log.d(TAG, "onCreate: mapactivity")
        lifecycleScope.launch{
            location()
        }
        setListAdapter()

        binding.toggleListVisibilityBtn.setOnClickListener {
            val visibility = binding.recycler.isVisible
            binding.recycler.isVisible = !visibility
        }
    }

    private fun setListAdapter() {
        val query = Firebase.firestore.collection("pucopoints")
            .whereEqualTo("manager", Firebase.auth.currentUser!!.uid)

        val options: FirestoreRecyclerOptions<Pucopoint> = FirestoreRecyclerOptions.Builder<Pucopoint>()
            .setQuery(query, Pucopoint::class.java)
            .setLifecycleOwner(this)
            .build()

            userAdapter = MapAdapter(
                this,
                options = options,
                loadingComplete = {
                }
            ) { model, position ->
                val lat = model.lat.toString().toDouble()
                val long = model.long.toString().toDouble()
                val location = LatLng(lat, long)
                mMap!!.moveCamera(CameraUpdateFactory.newLatLng(location))
                Log.d(TAG, "setListAdapter: ${model.email}")
            }

        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recycler.layoutManager = linearLayoutManager
        binding.recycler.adapter = userAdapter
        
    }

    @Synchronized
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL

        binding.createCircles.setOnClickListener {
            
            countcircles += 1
            Log.d(TAG, "onMapReady: $countcircles")
            if(countcircles%2 == 0){
                binding.createCircles.text = "remove circles"
                createCircle(mMap!!)
            }else{
                mMap!!.setOnMapClickListener(null)
                for (i in circles1) {
                    binding.createCircles.text = "create circles"
                    i.remove()
                }
                for (j in markerArray){
                    j.remove()
                }
            }

        }
        binding.remove.setOnClickListener {
            count += 1
            for (i in circles) {
                i.isVisible = count%2 == 0
            }
        }
    }

    private fun createCircle(mMap: GoogleMap) {
        Log.d(TAG, "createCircle:fun $countcircles")
        if(countcircles%2 == 0) {
            mMap.setOnMapClickListener { p0 ->
                marker = mMap.addMarker(
                    MarkerOptions().position(p0)
                )
                markerArray.add(marker!!)

                circle1 = mMap.addCircle(
                    CircleOptions()
                        .center(p0)
                        .radius(3000.0)
                        .strokeColor(0x00000000)
                        .fillColor(0x22140077)
                )
                circles1.add(circle1!!)
            }
        }
    }

    private suspend fun location() {
       Firebase.firestore.collection("pucopoints").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val location = LatLng(
                        document.data["lat"] as Double,
                        document.data["long"] as Double
                    )

                    val str = document.data["shopName"] as String
                    mMap!!.addMarker(
                        MarkerOptions().position(location).title(str)
                    )

                    circle = mMap!!.addCircle(
                        CircleOptions()
                            .center(location)
                            .radius(3000.0)
                            .strokeColor(0x00000000)
                            .fillColor(0x22140077)
                    )
                    circles.add(circle!!)
                    countLocation += 1
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(location))
                }

            }
            .addOnFailureListener { exception ->
                Log.d(PucopointList.TAG, "Error getting documents: ", exception)
            }
    }
    companion object {
        private const val TAG = "MapsActivity"
    }
}