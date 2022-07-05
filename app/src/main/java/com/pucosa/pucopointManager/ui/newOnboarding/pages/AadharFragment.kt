package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.FragmentAadarBinding
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.roomDatabase.Aadhaar
import com.pucosa.pucopointManager.roomDatabase.AppDatabase
import com.pucosa.pucopointManager.ui.newOnboarding.NewOnboardingViewModel
import com.pucosa.pucopointManager.utils.ImageCaptureManager
import kotlinx.coroutines.launch
import java.io.File

class AadharFragment : Fragment() {
    private lateinit var imageCaptureManager: ImageCaptureManager
    private var aadharUri: Uri = Uri.EMPTY
    private var aadharImageUrl: Uri? = null
    private var navController: NavController?= null
    private lateinit var binding: FragmentAadarBinding
//    private var binding = _binding!!
    private val pucoPointDoc = Firebase.firestore.collection("pucopoints").document()
    private lateinit var viewModel: NewOnboardingViewModel

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentAadarBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[NewOnboardingViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initImageCaptureManager()
        viewModel = ViewModelProvider(requireActivity())[NewOnboardingViewModel::class.java]

        val outputDir: File = context!!.cacheDir // context being the Activity pointer
        val outputFile: File = File.createTempFile("tempFile", ".html", outputDir)


        val Uidata = Observer<Pucopoint> {
            binding.aadharNumber.setText(viewModel.data.value?.aadhar)
        }

        viewModel.data.observe(requireActivity(),Uidata)

        binding.selectAadhar.setOnClickListener{
            imageCaptureManager.startImageChooser(
                uniqueRequestCode = AADHAR_IMAGE_REQUEST_CODE,
                cropAspect = intArrayOf(16, 9)
            )
        }

        binding.proceedButton.setOnClickListener{
            val aadharNumber = binding.aadharNumber.text.toString()
            viewLifecycleOwner.lifecycleScope.launch {
                if(aadharUri != Uri.EMPTY && isValidAadharNumber(aadharNumber)) {
                    viewModel.aadharDetailsChanged(aadharUri, aadharNumber)
                    catchData(aadharUri, aadharNumber)
                    navController = Navigation.findNavController(view)
                    binding.selectAadhar.setImageURI(null)
                    navController!!.navigate(R.id.action_aadarFragment_to_onboarding_agreement)
                }
                else{
                        Toast.makeText(requireContext(), "aadhar number is incorrect or image is not uploaded", Toast.LENGTH_LONG).show()

                }}}

        binding.back.setOnClickListener{
            val navController: NavController = Navigation.findNavController(view)
            navController.navigate(R.id.action_aadarFragment_to_shopImageFragment)
        }
    }

        private fun catchData(aadharUri: Uri, aadharNumber1: String) {

            val db = AppDatabase.getDatabase(context)

            val shopkeeperDatabaseMethods = db.shopkeeperDatabaseMethods()

            viewLifecycleOwner.lifecycleScope.launch{
                shopkeeperDatabaseMethods.insertAadhaar(Aadhaar(0,aadharNumber1, aadharUri.toString()))
            }

        }


    private fun isValidAadharNumber(target: String): Boolean {
        return target.length == 12
    }

    private fun initImageCaptureManager(){
        imageCaptureManager = ImageCaptureManager(this) { uri, _, uniqueRequestCode ->
            if(uniqueRequestCode == AADHAR_IMAGE_REQUEST_CODE) {
                aadharUri = uri?: Uri.EMPTY
                uri.let { binding.selectAadhar.setImageURI(uri) }
            }}}

    companion object {
        private const val TAG = "AadharFragment"
        private const val AADHAR_IMAGE_REQUEST_CODE = 1
    }}