package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.github.gcacace.signaturepad.views.SignaturePad
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.constants.DbCollections
import com.pucosa.pucopointManager.databinding.FragmentOnboardingAgreementBinding
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.roomDatabase.AppDatabase
import com.pucosa.pucopointManager.roomDatabase.Signature
import com.pucosa.pucopointManager.ui.newOnboarding.NewOnboardingViewModel
import com.pucosa.pucopointManager.utils.ImageUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream


class OnboardingAgreement: Fragment() {
    private lateinit var binding : FragmentOnboardingAgreementBinding
    private lateinit var navController: NavController
    private var viewModel: NewOnboardingViewModel? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingAgreementBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[NewOnboardingViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.accept2.isEnabled = false
        super.onViewCreated(view, savedInstanceState)
        binding.progressbar.visibility = View.INVISIBLE
        val signaturePad = binding.signaturePad

        Observer<Boolean> { binding.accept2 }

        signaturePad.setOnSignedListener(object : SignaturePad.OnSignedListener {
            override fun onStartSigning() {}
            override fun onSigned() {

            }
            override fun onClear() {
            }
        })

        binding.clear.setOnClickListener{
            signaturePad.clear()
        }

        val checkBox: CheckBox = binding.checkBox1

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.accept2.isEnabled = isChecked
        }

        val viewer: TextView = binding.writtenAgreement

        val formattedText = "I agree to the <a href='https://pucosa.com/pucopoint_terms_conditions.pdf'>Terms and Conditions</a>"
        viewer.text = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)

        viewModel = ViewModelProvider(requireActivity())[NewOnboardingViewModel::class.java]
        navController = Navigation.findNavController(view)
        binding.accept2.setOnClickListener {
            binding.progressbar.visibility = View.VISIBLE
            binding.accept2.isEnabled = false
            binding.clear.isEnabled = false
            binding.checkBox1.isEnabled = false
            binding.writtenAgreement.isEnabled = false
            signaturePad.isEnabled = false
            onboardingAgreementImageUpload(signaturePad, view)
         }
        binding.cancel.setOnClickListener {
            viewModel!!.data.value = Pucopoint()
            Log.d(TAG, "cancel button pressed")
            Toast.makeText(requireContext(), "Cannot register", Toast.LENGTH_SHORT).show()

            val navController: NavController = Navigation.findNavController(view)
            navController.navigate(R.id.action_onboarding_agreement_to_pucoPointList)
        }
    }

    @Synchronized
        fun onboardingAgreementImageUpload(
        signaturePad: SignaturePad,
        view: View
    ) {
            lifecycleScope.launch() {
                val pucoPointRef = Firebase.firestore.collection(DbCollections.PUCOPOINTS).document()
                val id = pucoPointRef.id
                val model = viewModel?.data?.value!!
                val shopImageUrl: Uri = model.shopImageUrl.toUri()
                val shopkeeperImageUrl: Uri = model.shopkeeperImageUrl.toUri()
                val aadharUri: Uri = model.aadharImageUrl.toUri()
                val uploadShopJob = async(Dispatchers.IO){if(shopImageUrl != Uri.EMPTY)uploadImage(shopImageUrl, NewOnboardingViewModel.ImageType.SHOP, id) else null}
                val uploadShopkeeperJob = async(Dispatchers.IO){if(shopkeeperImageUrl != Uri.EMPTY)uploadImage(
                    shopkeeperImageUrl,
                    NewOnboardingViewModel.ImageType.SHOPKEEPER,
                    id
                ) else null}
                val uploadAadharImageJob = async(Dispatchers.IO){if(aadharUri != Uri.EMPTY)uploadImage(
                    aadharUri,
                    NewOnboardingViewModel.ImageType.AADHAR,
                    id
                ) else null}
                val uploadSignaturePad = async(Dispatchers.IO){if(!signaturePad.isEmpty)uploadSignature(
                    signaturePad.signatureBitmap,id
                ) else null}
                firebaseUriImageUpload(uploadShopJob.await(), uploadShopkeeperJob.await(), uploadAadharImageJob.await(), context!!, uploadSignaturePad.await(), id, view)
            }

        }

    private suspend fun uploadSignature(
        signaturePad: Bitmap,
        id: String?
    ): Uri? {
        val sign = compress(signaturePad)
        val model = viewModel?.data?.value!!
        Looper.prepare()


        val fileName = "SIGNATURE_${model.pid}.WEBP"
        val storageReference = FirebaseStorage.getInstance().getReference("/pucopoints/${id}/$fileName")
        val uploadTask = storageReference.putBytes(sign).
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
    private suspend fun firebaseUriImageUpload(
        shopUri: Uri?,
        shopkeeperUri: Uri?,
        aadharImageUri: Uri?,
        context: Context,
        signaturePad: Uri?,
        id: String,
        view: View
    ) {
        val pucoPointRef = Firebase.firestore.collection(DbCollections.PUCOPOINTS).document()
        val currData = viewModel?.data?.value


        catchData(id, shopUri.toString(), shopkeeperUri.toString(), aadharImageUri.toString(), signaturePad.toString())

        val db = AppDatabase.getDatabase(context)

        val shopkeeperdata = db.shopkeeperDatabaseMethods().excessShopkeeperInfo()
        val shopLocationinfo = db.shopkeeperDatabaseMethods().excessShopLocationInfo()
        val aadharInfo = db.shopkeeperDatabaseMethods().excessAadhar()
        val shopInfo = db.shopkeeperDatabaseMethods().excessShop()
        val signature = db.shopkeeperDatabaseMethods().excessSignature()

        Log.d("@@@@", "firebaseUriImageUpload: $shopkeeperdata")
        Log.d("@@@@", "firebaseUriImageUpload: $shopLocationinfo")
        Log.d("@@@@", "firebaseUriImageUpload: $aadharInfo")
        Log.d("@@@@", "firebaseUriImageUpload: $shopInfo")
        Log.d("@@@@", "firebaseUriImageUpload: $signature")

        val name = shopkeeperdata.name
        val email = shopkeeperdata.email
        val altPhone = shopkeeperdata.altPhone
        val phone = shopkeeperdata.phone
        val shopkeeperImageUrl = shopkeeperdata.shopOwnerImage

        val city = shopLocationinfo.city
        val state = shopLocationinfo.state
        val country = shopLocationinfo.country
        val lat = shopLocationinfo.lat.toDouble()
        val long = shopLocationinfo.lon.toDouble()
        val pincode = shopLocationinfo.pincode
        val streetAddress = shopLocationinfo.streetAddress

        val aadhaarImageUrl = aadharInfo.aadhaarImage
        val aadhaar = aadharInfo.aadharNumber

        val shopImageUrl = shopInfo.shopImage
        val shopName = shopInfo.shopName

        val signatureAgreement = signature.signatureImage
        val pid = signature.pid

        val username = shopkeeperdata.username
        val phoneCountryCode = shopkeeperdata.phoneCountryCode
        val altCountryCode = shopkeeperdata.altCountryCode
        val phoneNum = shopkeeperdata.phoneNum
        val altNum = shopkeeperdata.altNum

        currData?.name = name
        currData?.email = email
        currData?.altPhone = altPhone
        currData?.phone = phone
        currData?.shopkeeperImageUrl = shopkeeperImageUrl

        currData?.city = city
        currData?.state = state
        currData?.country = country
        currData?.lat = lat
        currData?.long = long
        currData?.pincode = pincode
        currData?.streetAddress = streetAddress

        currData?.aadharImageUrl = aadhaarImageUrl
        currData?.aadhar = aadhaar

        currData?.shopImageUrl = shopImageUrl
        currData?.shopName = shopName

        currData?.signaturePad = signatureAgreement
        currData?.pid = pid

        currData?.username = username
        currData?.altCountryCode = altCountryCode
        currData?.phoneCountryCode = phoneCountryCode
        currData?.phoneNum = phoneNum
        currData?.altNum = altNum

        currData?.manager = Firebase.auth.currentUser?.uid.toString()

        if(currData?.pid != "" &&
            currData?.name != "" &&
            currData?.email != "" &&
            currData?.phone != "" &&
            currData?.altPhone != "" &&
            currData?.aadhar != "" &&
            currData?.aadharImageUrl != "" &&
            currData?.shopImageUrl != "" &&
            currData?.shopkeeperImageUrl != "" &&
            currData?.signaturePad != "" &&
            currData?.lat != 0.0 &&
            currData?.long != 0.0 &&
            currData?.geohash != "" &&
            currData?.shopName != "" &&
            currData?.country != "" &&
            currData?.state != "" &&
            currData?.city != "" &&
            currData?.streetAddress != "" &&
            currData?.pincode != "" &&
            currData?.username != "" &&
            currData?.phoneCountryCode != "" &&
            currData?.altCountryCode != "" &&
            currData?.phoneNum != "" &&
            currData?.altNum != "" &&
            currData?.manager != "" ){

            if (currData != null) {

                pucoPointRef.set(currData).addOnSuccessListener {
                    Toast.makeText(requireContext(),"Successfully Saved", Toast.LENGTH_LONG).show()
                    viewModel?.data?.value = Pucopoint()
                    navController = Navigation.findNavController(view)
                    navController.navigate(R.id.action_global_pucoPointList)
                    val currData = viewModel?.data?.value
                    Log.d(OnboardingAgreement.TAG, "during onboarding data load")

                    currData?.pid = ""
                    currData?.name = ""
                    currData?.email = ""
                    currData?.phone = ""
                    currData?.altPhone = ""
                    currData?.aadhar = ""
                    currData?.aadharImageUrl = ""
                    currData?.shopImageUrl = ""
                    currData?.shopkeeperImageUrl = ""
                    currData?.signaturePad = ""
                    currData?.lat = 0.0
                    currData?.long = 0.0
                    currData?.geohash = ""
                    currData?.shopName = ""
                    currData?.country = ""
                    currData?.state = ""
                    currData?.city = ""
                    currData?.streetAddress = ""
                    currData?.pincode = ""
                    currData?.username = ""
                    currData?.phoneCountryCode = ""
                    currData?.altCountryCode = ""
                    currData?.phoneNum = ""
                    currData?.altNum = ""
                    currData?.manager = ""

                    viewLifecycleOwner.lifecycleScope.launch{

                        db.shopkeeperDatabaseMethods().deleteShopLocationrInfo()
                        db.shopkeeperDatabaseMethods().deleteShopLocationrInfo()
                        db.shopkeeperDatabaseMethods().deleteShop()
                        db.shopkeeperDatabaseMethods().deleteAadhaar()
                        db.shopkeeperDatabaseMethods().deleteSignature()

                    }

                }.addOnFailureListener{
                    Log.e(OnboardingAgreement.TAG, "onViewCreated: error while onboarding", it)
                    val currData = viewModel?.data?.value
                    currData?.pid = ""
                    currData?.name = ""
                    currData?.email = ""
                    currData?.phone = ""
                    currData?.altPhone = ""
                    currData?.aadhar = ""
                    currData?.aadharImageUrl = ""
                    currData?.shopImageUrl = ""
                    currData?.shopkeeperImageUrl = ""
                    currData?.signaturePad = ""
                    currData?.lat = 0.0
                    currData?.long = 0.0
                    currData?.geohash = ""
                    currData?.shopName = ""
                    currData?.country = ""
                    currData?.state = ""
                    currData?.city = ""
                    currData?.streetAddress = ""
                    currData?.pincode = ""
                    currData?.username = ""
                    currData?.phoneCountryCode = ""
                    currData?.altCountryCode = ""
                    currData?.phoneNum = ""
                    currData?.altNum = ""
                    currData?.manager = ""
                    viewLifecycleOwner.lifecycleScope.launch{

                        db.shopkeeperDatabaseMethods().deleteShopLocationrInfo()
                        db.shopkeeperDatabaseMethods().deleteShopLocationrInfo()
                        db.shopkeeperDatabaseMethods().deleteShop()
                        db.shopkeeperDatabaseMethods().deleteAadhaar()
                        db.shopkeeperDatabaseMethods().deleteSignature()

                    }
                    Toast.makeText(requireContext(),"Failed", Toast.LENGTH_LONG).show()
                }
            }
        } else{
            Log.d(NewOnboardingViewModel.TAG, "firebaseUriImageUpload: something went wrong")
            Toast.makeText(requireContext(), "something went wrong!", Toast.LENGTH_LONG).show()
            navController = Navigation.findNavController(view)
            navController.navigate(R.id.action_global_pucoPointList)
        }
    }

    private fun catchData(
        id: String,
        shopUri: String,
        shopkeeperUri: String,
        aadharImageUri: String,
        signaturePad: String
    ) {

        val db = AppDatabase.getDatabase(context!!)

        val shopkeeperDatabaseMethods = db.shopkeeperDatabaseMethods()

        viewLifecycleOwner.lifecycleScope.launch{
            shopkeeperDatabaseMethods.insertSignature(Signature(0,id, shopUri, shopkeeperUri, aadharImageUri,signaturePad))
        }
    }


    private suspend fun uploadImage(
        uri: Uri,
        type: NewOnboardingViewModel.ImageType,
        id: String?
    ): Uri? {
        val model = viewModel?.data?.value!!
        Looper.prepare()

        val filePath = uri.lastPathSegment
        val fileExtension = filePath?.let { filePath.substring(it.lastIndexOf('.') + 1) }
        val fileName = when (type) {
            NewOnboardingViewModel.ImageType.SHOPKEEPER -> "SHOPKEEPER_${model.pid}.$fileExtension"
            NewOnboardingViewModel.ImageType.SHOP -> "SHOP_${model.pid}.$fileExtension"
            NewOnboardingViewModel.ImageType.AADHAR -> "AADHAR_${model.pid}.$fileExtension"
            NewOnboardingViewModel.ImageType.SIGNATURE -> TODO()
        }
        val storageReference = FirebaseStorage.getInstance().getReference("/pucopoints/${id}/$fileName")

        val compressedUri = context?.let { ImageUtils.compressImage(uri, fileName , it) }

        val uploadTask = compressedUri?.let {
            storageReference.putFile(it).addOnSuccessListener {

            }.addOnFailureListener{

            }.addOnProgressListener {

            }
        }
        return uploadTask?.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            storageReference.downloadUrl
        }?.await()
    }
    
    
    companion object {
        const val TAG = "OnboardingAgreement"
    }

    enum class ImageType {
        SHOPKEEPER, AADHAR, SHOP, SIGNATURE
    }
}


