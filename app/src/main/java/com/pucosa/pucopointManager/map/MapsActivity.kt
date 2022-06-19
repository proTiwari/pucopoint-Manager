package com.pucosa.pucopointManager.map

import android.R
import android.R.attr.centerY
import android.R.attr.radius
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.databinding.ActivityMapsBinding
import com.pucosa.pucopointManager.ui.newOnboarding.pages.PucopointList
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private val circles: ArrayList<Circle> = ArrayList()
    private var circle: Circle? = null
    private var count = 0
    private var countLocation = 0
    private lateinit var binding: ActivityMapsBinding
    private var locationArrayList: ArrayList<String>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.pucosa.pucopointManager.R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        GlobalScope.launch{
            location()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Synchronized
    @OptIn(DelicateCoroutinesApi::class)
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL


        binding.remove.setOnClickListener {
            count += 1
            for (i in circles) {
                i.isVisible = count%2 == 0
//                Toast.makeText(applicationContext, "$circle   ${locationArrayList?.get(i)}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "MapsActivity"
    }


    suspend fun location() {
        val query = Firebase.firestore.collection("pucopoints").get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val location = LatLng(
                        document.data["lat"] as Double,
                        document.data["long"] as Double
                    )
                    mMap!!.addMarker(
                        MarkerOptions().position(location).title(document.data["name"] as String?)
                    )
                    circle = mMap!!.addCircle(
                        CircleOptions()
                            .center(location)
                            .radius(3000.0)
                            .strokeColor(0x00000000)
                            .fillColor(0x22140077)
                    )
                    circles.add(circle!!)
//                    val vari = circle.toString().split('@')[1]
//                    locationArrayList?.add(vari)
                    countLocation += 1
                    mMap!!.moveCamera(CameraUpdateFactory.newLatLng(location))
                }

            }
            .addOnFailureListener { exception ->
                Log.d(PucopointList.TAG, "Error getting documents: ", exception)
            }
    }
}