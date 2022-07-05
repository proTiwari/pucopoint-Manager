package com.pucosa.pucopointManager.ui.newOnboarding

import android.annotation.SuppressLint
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.room.Room
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.constants.DbCollections
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.roomDatabase.AppDatabase
import com.pucosa.pucopointManager.ui.newOnboarding.pages.OnboardingAgreement
import com.pucosa.pucopointManager.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class NewOnboardingViewModel(application: Application) : AndroidViewModel(application) {

    private lateinit var navController: NavController
    var data = MutableLiveData(Pucopoint())
    var countryCode1 = MutableLiveData<String>("+91")
    var countryCode2 = MutableLiveData<String>("+91")
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext

    var signaturePad = MutableLiveData<SignaturePad>()

    fun aadharDetailsChanged(url: Uri, aadhar: String) {
        val currData = data.value
        currData?.aadhar = aadhar
        currData?.aadharImageUrl = url.toString()
        data.value = currData
    }

    fun shopkeeperDetailsChanged(
        Name: String,
        Email: String,
        Phone: String,
        AltPhone: String,
        shopkeeperImageUrl: String,
        username: String,
        phoneCountryCode: String,
        phoneNum: String,
        altCountryCode: String,
        altNum: String
    ){
        val currData = data.value

        currData?.name = Name
        currData?.email = Email
        currData?.phone = Phone
        currData?.altPhone = AltPhone
        currData?.shopkeeperImageUrl = shopkeeperImageUrl
        currData?.username = username
        currData?.phoneCountryCode = phoneCountryCode
        currData?.altCountryCode = altCountryCode
        currData?.phoneNum = phoneNum
        currData?.altNum = altNum
        data.value = currData
    }

    fun shopDetailsChanged(
        country: String,
        state: String,
        city: String,
        streetAddress: String,
        pincode: String,
        latitude: Double,
        longitude: Double,
    ){
        val currData = data.value
        currData?.country = country
        currData?.state = state
        currData?.city = city
        currData?.streetAddress = streetAddress
        currData?.lat = latitude
        currData?.long = longitude
        currData?.pincode = pincode
        currData?.geohash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
        data.value = currData

    }

    fun shopImageDetailChanged(shopName: String, shopImageUrl: Uri) {
        val currData = data.value
        currData?.shopName = shopName
        currData?.shopImageUrl = shopImageUrl.toString()
        data.value = currData
    }

    companion object{
        const val TAG = "NewOnboardingViewModel"
    }

    enum class ImageType {
        SHOPKEEPER, AADHAR, SHOP, SIGNATURE
    }
}