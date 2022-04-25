package com.pucosa.pucopointManager.ui.newOnboarding

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.android.gms.location.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.pucosa.pucopointManager.constants.DbCollections
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.ui.newOnboarding.pages.OnboardingAgreement
import com.pucosa.pucopointManager.utils.ImageUtils
import com.pucosa.pucopointManager.utils.LocationUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class NewOnboardingViewModel(application: Application) : AndroidViewModel(application) {

    var data = MutableLiveData(Pucopoint())
    var countryCode1 = MutableLiveData<String>("+91")
    var countryCode2 = MutableLiveData<String>("+91")
    @SuppressLint("StaticFieldLeak")
    private val context = getApplication<Application>().applicationContext
    var signaturePad = MutableLiveData<SignaturePad>()




    fun aadharDetailsChanged(url: Uri, aadhar: String) {
        val currData = data.value!!
        currData.aadhar = aadhar
        currData.aadharImageUrl = url.toString()
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
        val currData = data.value!!

        currData.name = Name
        currData.email = Email
        currData.phone = Phone
        currData.altPhone = AltPhone
        currData.shopkeeperImageUrl = shopkeeperImageUrl
        currData.username = username
        currData.phoneCountryCode = phoneCountryCode
        currData.altCountryCode = altCountryCode
        currData.phoneNum = phoneNum
        currData.altNum = altNum
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
        val currData = data.value!!
        currData.country = country
        currData.state = state
        currData.city = city
        currData.streetAddress = streetAddress
        currData.lat = latitude
        currData.lon = longitude
        currData.pincode = pincode
        currData.geohash = GeoFireUtils.getGeoHashForLocation(GeoLocation(latitude, longitude))
        data.value = currData

    }

    fun shopImageDetailChanged(shopName: String, shopImageUrl: Uri) {
        val currData = data.value!!
        currData.shopName = shopName
        currData.shopImageUrl = shopImageUrl.toString()
        data.value = currData
    }


    @Synchronized
    fun OnboardingAgreementImageUpload(
        context: Context,
        signaturePad: SignaturePad,
        view: View
    ) {
        viewModelScope.launch {

            val model = data.value!!
            val shopImageUrl: Uri = model.shopImageUrl.toUri()
            val shopkeeperImageUrl: Uri = model.shopkeeperImageUrl.toUri()
            val aadharUri: Uri = model.aadharImageUrl.toUri()
            val uploadShopJob = async(Dispatchers.IO){if(shopImageUrl != Uri.EMPTY)uploadImage(shopImageUrl, ImageType.SHOP, context, view) else null}
            val uploadShopkeeperJob = async(Dispatchers.IO){if(shopkeeperImageUrl != Uri.EMPTY)uploadImage(shopkeeperImageUrl, ImageType.SHOPKEEPER, context, view) else null}
            val uploadAadharImageJob = async(Dispatchers.IO){if(aadharUri != Uri.EMPTY)uploadImage(aadharUri, ImageType.AADHAR, context, view) else null}
            val uploadSignaturePad = async(Dispatchers.IO){if(!signaturePad.isEmpty)uploadSignature(
                signaturePad.signatureBitmap,
                context, view
            ) else null}
            firebaseUriImageUpload(uploadShopJob.await(), uploadShopkeeperJob.await(), uploadAadharImageJob.await(), context, uploadSignaturePad.await())
        }

    }

    private suspend fun uploadSignature(signaturePad: Bitmap, context: Context, view: View): Uri? {
        val sign = compress(signaturePad)
        val model = data.value!!
        Looper.prepare()
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Uploading File ...")
        progressDialog.setCancelable(false)

        val fileName = "SIGNATURE_${model.pid}.WEBP"
        val storageReference = FirebaseStorage.getInstance().getReference("/pucopoints/${model.pid}/$fileName")
        val uploadTask = storageReference.putBytes(sign).
        addOnSuccessListener {
            progressDialog.dismiss()
        }.addOnFailureListener{
           progressDialog.dismiss()

        }.addOnProgressListener {
        }

        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl
        }.await()
    }

    @Synchronized
    fun compress(yourBitmap: Bitmap): ByteArray {
        //converted into webp into lowest quality
        val stream = ByteArrayOutputStream()
        yourBitmap.compress(Bitmap.CompressFormat.WEBP, 0, stream) //0=lowest, 100=highest quality
        val byteArray = stream.toByteArray()
        //convert your byteArray into bitmap
        return byteArray
    }

    @Synchronized
    private fun firebaseUriImageUpload(
        shopUri: Uri?,
        shopkeeperUri: Uri?,
        aadharImageUri: Uri?,
        context: Context,
        signaturePad: Uri?,
    ) {
        val pucoPointRef = Firebase.firestore.collection(DbCollections.PUCOPOINTS).document()
        val currData = data.value!!
        currData.pid = pucoPointRef.id
        currData.shopImageUrl = shopUri.toString()
        currData.shopkeeperImageUrl = shopkeeperUri.toString()
        currData.aadharImageUrl = aadharImageUri.toString()
        currData.signaturePad = signaturePad.toString()
        currData.manager = Firebase.auth.currentUser!!.uid
        data.value = currData
        pucoPointRef.set(currData).addOnSuccessListener {
            Toast.makeText(context,"Successfully Saved", Toast.LENGTH_LONG).show()
            data.value = Pucopoint()
            Log.d(OnboardingAgreement.TAG, "during onboarding data load")
        }.addOnFailureListener{
            Log.e(OnboardingAgreement.TAG, "onViewCreated: error while onboarding", it)
            Toast.makeText(context,"Failed", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun uploadImage(uri: Uri, type: ImageType, context: Context, view: View): Uri{
        val model = data.value!!
        Looper.prepare()

        val filePath = uri.lastPathSegment
        val fileExtension = filePath?.let { filePath.substring(it.lastIndexOf('.') + 1) }
        val fileName = when (type) {
            ImageType.SHOPKEEPER -> "SHOPKEEPER_${model.pid}.$fileExtension"
            ImageType.SHOP -> "SHOP_${model.pid}.$fileExtension"
            ImageType.AADHAR -> "AADHAR_${model.pid}.$fileExtension"
            ImageType.SIGNATURE -> TODO()
        }
        val storageReference = FirebaseStorage.getInstance().getReference("/pucopoints/${model.pid}/$fileName")

        val compressedUri = ImageUtils.compressImage(uri, fileName , context)

        val uploadTask = storageReference.putFile(compressedUri).
        addOnSuccessListener {

        }.addOnFailureListener{

        }.addOnProgressListener {

        }
        return uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl
        }.await()
    }



    // shopkeeper information

    // shop info




    companion object{
        private const val TAG = "NewOnboardingViewModel"
    }

    enum class ImageType {
        SHOPKEEPER, AADHAR, SHOP, SIGNATURE
    }
}