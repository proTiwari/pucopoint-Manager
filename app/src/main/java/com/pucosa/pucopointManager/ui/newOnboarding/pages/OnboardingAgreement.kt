package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
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
                    binding.accept2.isEnabled = false
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

        val viewer: TextView = binding.writtenAgreement

        val formattedText = "I agree to the <a href='https://pucosa.com/pucopoint_terms_conditions.pdf'>Terms and Conditions</a>"
        viewer.text = HtmlCompat.fromHtml(formattedText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        binding.writtenAgreement.setOnClickListener{
            val url = "https://pucosa.com/pucopoint_terms_conditions.pdf"
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
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


