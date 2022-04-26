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
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.FragmentShopImageBinding
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.ui.newOnboarding.NewOnboardingViewModel
import com.pucosa.pucopointManager.utils.ImageCaptureManager

class ShopImageFragment : Fragment() {
    private lateinit var binding: FragmentShopImageBinding
    lateinit var viewModel: NewOnboardingViewModel
    private lateinit var navController: NavController
    private lateinit var imageCaptureManager: ImageCaptureManager
    private var shopImageUri: Uri = Uri.EMPTY

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopImageBinding.inflate(inflater, container, false)
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
            binding.shopName.setText(viewModel.data.value?.shopName)
        }
        viewModel.data.observe(requireActivity(),Uidata)

        binding.proceedButton.setOnClickListener{
            val shopName = binding.shopName.text.toString()
            if(shopName != "" && shopImageUri != Uri.EMPTY) {
                viewModel.shopImageDetailChanged(shopName, shopImageUri)
                navController = Navigation.findNavController(view)
                navController.navigate(R.id.action_shopImageFragment_to_aadarFragment)
            }else{
                Toast.makeText(requireContext(), "shop name or image is not uploaded", Toast.LENGTH_LONG).show()
            }
        }

        binding.back.setOnClickListener{
            val navController: NavController = Navigation.findNavController(view)
            navController.navigate(R.id.action_shopImageFragment_to_onboarding_shop_info)
        }

        binding.selectShopImage.setOnClickListener{
            imageCaptureManager.startImageChooser(
                uniqueRequestCode = SHOP_IMAGE_REQUEST_CODE,
                forProfile = true
            )
        }
    }

    private fun initImageCaptureManager() {
        imageCaptureManager = ImageCaptureManager(this) { uri, _, uniqueRequestCode ->
            if(uniqueRequestCode == SHOP_IMAGE_REQUEST_CODE) {
                shopImageUri = uri?: Uri.EMPTY
                uri.let { binding.selectShopImage.setImageURI(uri) }
            }
        }}

        companion object {
            private val TAG = "shopImageFragment"
            private val SHOP_IMAGE_REQUEST_CODE = 1

        }

        private enum class ImageType {
            SHOPIMAGE
        }
}
