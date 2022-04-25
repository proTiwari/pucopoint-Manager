package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.github.gcacace.signaturepad.views.SignaturePad
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.FragmentOnboardingAgreementBinding
import com.pucosa.pucopointManager.models.Pucopoint
import com.pucosa.pucopointManager.ui.newOnboarding.NewOnboardingViewModel


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
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.accept2.isEnabled = false
        super.onViewCreated(view, savedInstanceState)
        binding.progressbar.visibility = View.INVISIBLE
        val signaturePad = binding.signaturePad

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

        val checkBox: CheckBox = binding.checkBox1 //get the view using findViewById

        val invisibleView: View? = null //do the same

        checkBox.setOnCheckedChangeListener { buttonView, isChecked ->
            binding.accept2.isEnabled = isChecked
        }


        viewModel = ViewModelProvider(requireActivity())[NewOnboardingViewModel::class.java]
        navController = Navigation.findNavController(view)
        binding.accept2.setOnClickListener {
            binding.progressbar.visibility = View.VISIBLE
            binding.accept2.isEnabled
            onboardingImageUploadFun(signaturePad, view)
                binding.progressbar.visibility = View.INVISIBLE
                navController.navigate(R.id.action_global_pucoPointList)


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
    private fun onboardingImageUploadFun(
        signaturePad: SignaturePad,
        view: View,
    ) {
        viewModel?.OnboardingAgreementImageUpload(requireContext(),signaturePad, view)
    }
    companion object {
        const val TAG = "OnboardingAgreement"
    }
}

