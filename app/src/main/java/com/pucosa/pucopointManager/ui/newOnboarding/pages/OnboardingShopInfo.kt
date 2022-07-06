package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.firebase.geofire.GeoLocation
import com.google.android.gms.location.*
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.FragmentOnboardingShopInfoBinding
import com.pucosa.pucopointManager.roomDatabase.AppDatabase
import com.pucosa.pucopointManager.roomDatabase.ShopLocationInfo
import com.pucosa.pucopointManager.ui.newOnboarding.NewOnboardingViewModel
import com.pucosa.pucopointManager.utils.LocationUtils
import kotlinx.coroutines.launch


class OnboardingShopInfo : Fragment() {

    private lateinit var binding: FragmentOnboardingShopInfoBinding
//    private var binding = _binding!!
    lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var navController: NavController
    private var PERMISSION_ID: Int = 44
    var imageUri: Uri = Uri.EMPTY
    var shopImageUrl: Uri? = null
    var locality :String? = null
    private lateinit var viewModel: NewOnboardingViewModel
    var geohash : String? = ""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentOnboardingShopInfoBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[NewOnboardingViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.back.setOnClickListener {
            navController.navigate(R.id.action_onboarding_shop_info_to_onboarding_shopkeeper_info)
        }
        binding.getLocation.setOnClickListener {
            requestPermission()
            getLastLocation()
        }

        navController = Navigation.findNavController(view)
        binding.proceedButton.setOnClickListener {
            val country = binding.country.text.toString()
            val state = binding.state.text.toString()
            val city = binding.city.text.toString()
            val pincode = binding.pincode.text.toString()
            val latitude = binding.latitude.text.toString().toDoubleOrNull()
            val longitude = binding.longitude.text.toString().toDoubleOrNull()
            val streetAddress = binding.StreetAddress.text.toString()
            if (country != "" && state != "" && city != "" && pincode != "" && latitude != 0.0 && longitude != 0.0 && streetAddress != "") {
                if (latitude != null) {
                    if (longitude != null) {

                       catchData(country, state, city, streetAddress, pincode, latitude.toString(), longitude.toString())

//                        viewModel.shopDetailsChanged(
//                            country, state, city, streetAddress, pincode, latitude, longitude
//                        )

                        navController = Navigation.findNavController(view)
                        navController.navigate(R.id.action_onboarding_shop_info_to_shopImageFragment)
                    }
                }

            } else {
                Toast.makeText(requireContext(), "Enter complete location details", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun catchData(country: String, state: String, city: String, streetAddress: String, pincode: String, latitude: String, longitude: String){
        val db = AppDatabase.getDatabase(context!!)

        val shopkeeperDatabaseMethods = db.shopkeeperDatabaseMethods()

        viewLifecycleOwner.lifecycleScope.launch{
            shopkeeperDatabaseMethods.insertShopLocationInfo(ShopLocationInfo(0, latitude, longitude, city, state, country, pincode, streetAddress))
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult (
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Debug:", "You have the Permission")
            }
        }
    }


    @RequiresApi(Build.VERSION_CODES.S)
    @Synchronized
    fun getLastLocation() {
        if (CheckPermission()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return
                }
                fusedLocationProviderClient.lastLocation.addOnCompleteListener { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        NewLocationData()
                    } else {
                        setGeocodedLocation(location.latitude, location.longitude)
                    }
                }
            } else {
                Toast.makeText(
                    context,
                    "Please Turn on Your device Location",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            requestPermission()
        }
    }



    @RequiresApi(Build.VERSION_CODES.S)
    fun NewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = android.location.LocationRequest.QUALITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()!!
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            setGeocodedLocation(lastLocation.latitude, lastLocation.longitude)
        }
    }

    private fun CheckPermission(): Boolean {
        if (
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    fun requestPermission() {
        Log.i(TAG, "setGeocodedLocation:   requestPermission")

        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }




    private fun setGeocodedLocation(lat: Double, long: Double){
        val add = LocationUtils.geocodeLocation(requireContext(), GeoLocation(lat, long))
        val cityName = add?.get(0)?.locality?.toString()
        val countryName = add?.get(0)?.countryName?.toString()
        val postCode = add?.get(0)?.postalCode?.toString()
        val state = add?.get(0)?.adminArea?.toString()
        val latitude = add?.get(0)?.latitude?.toString()
        val longitude = add?.get(0)?.longitude?.toString()

        if (countryName != null) {
            viewModel.data.value?.country = countryName
        }
        if (state != null) {
            viewModel.data.value?.state = state
        }
        if (cityName != null) {
            viewModel.data.value?.city = cityName
        }
        if (postCode != null) {
            viewModel.data.value?.pincode = postCode
        }
        viewModel.data.value?.lat = latitude?.toDouble()
        viewModel.data.value?.long = longitude?.toDouble()

        binding.country.setText(countryName)
        binding.state.setText(state)
        binding.city.setText(cityName)
        binding.pincode.setText(postCode)
        binding.latitude.setText(latitude)
        binding.longitude.setText(longitude)

    }

    companion object {
            private const val TAG = "OnboardingShopInfo"
            private const val SHOP_IMAGE_REQUEST_CODE = 3
        }
}