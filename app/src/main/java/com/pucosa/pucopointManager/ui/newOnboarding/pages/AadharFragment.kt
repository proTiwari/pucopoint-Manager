package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import com.pucosa.pucopointManager.ui.newOnboarding.NewOnboardingViewModel
import com.pucosa.pucopointManager.utils.ImageCaptureManager
import kotlinx.coroutines.launch

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


        val Uidata = Observer<Pucopoint> {
            // Update the UI, in this case, a TextView.
            binding.aadharNumber.setText(viewModel.data.value?.aadhar)
        }

        viewModel.data.observe(requireActivity(),Uidata)

//        viewModel.data.observe(viewLifecycleOwner) {
//            binding.aadharNumber.setText(it.aadhar)
//            viewModel.aadharDetailsChanged(aadharUri, binding.aadharNumber.toString())
//        }

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
            //        aadharImageUrl = uploadImage(aadharUri)
                viewModel.aadharDetailsChanged(aadharUri, aadharNumber)
                    navController = Navigation.findNavController(view)
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
//    private suspend fun uploadImage(uri: Uri): Uri? {
//        val progressDialog = ProgressDialog(requireContext())
//        progressDialog.setMessage("Uploading File ...")
//        progressDialog.setCancelable(false)
//        progressDialog.show()
//        val filePath = uri.lastPathSegment
//        val fileExtension = filePath?.let { filePath.substring(it.lastIndexOf('.') + 1) }
//        val fileName = "AADHAR_${pucoPointDoc.id}.$fileExtension"
//        val storageReference = FirebaseStorage.getInstance().getReference("/pucopoints/${pucoPointDoc.id}/$fileName")
//        val compressedUri = ImageUtils.compressImage(uri, fileName, requireContext())
//        val uploadTask = storageReference.putFile(compressedUri).
//        addOnSuccessListener {
//            Toast.makeText(requireContext(),"Successfuly uploaded",Toast.LENGTH_LONG).show()
//            if(progressDialog.isShowing) progressDialog.dismiss()
//        }.addOnFailureListener{
//            if(progressDialog.isShowing) progressDialog.dismiss()
//            Toast.makeText(requireContext(),"Failed",Toast.LENGTH_SHORT).show()
//        }.addOnProgressListener {
//                taskSnapshot ->
//            val progress = (100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount)
//            progressDialog.progress = progress.toInt()
//        }
//        return uploadTask.continueWithTask { task ->
//            if (!task.isSuccessful) {
//                task.exception?.let {
//                    throw it
//                }}
//            storageReference.downloadUrl
//        }.await()
//    }

    private fun isValidAadharNumber(target: String): Boolean {
        return target == null || target.length == 12
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