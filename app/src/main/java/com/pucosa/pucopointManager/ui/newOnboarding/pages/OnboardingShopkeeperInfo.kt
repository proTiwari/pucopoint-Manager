package com.pucosa.pucopointManager.ui.newOnboarding.pages

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.hbb20.countrypicker.dialog.launchCountryPickerDialog
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.FragmentOnboardingShopkeeperInfoBinding
import com.pucosa.pucopointManager.ui.newOnboarding.NewOnboardingViewModel
import com.pucosa.pucopointManager.roomDatabase.AppDatabase
import com.pucosa.pucopointManager.roomDatabase.ShopkeepersInfo
import com.pucosa.pucopointManager.utils.ImageCaptureManager
import kotlinx.coroutines.launch
import java.util.regex.Pattern


class OnboardingShopkeeperInfo : Fragment() {
    var huckup = true
    var Phone = ""
    var AltPhone = ""
    var phoneCountryCode = ""
    var phoneNum = ""
    var altCountryCode = ""
    var altNum = ""
    private lateinit var username: String
    private lateinit var imageCaptureManager: ImageCaptureManager
    private var aadharUri: Uri = Uri.EMPTY
    private var shopkeeperUri: Uri = Uri.EMPTY
    private var aadharImageUrl: Uri? = null
    lateinit var viewModel: NewOnboardingViewModel
    private var shopkeeperImageUrl: Uri? = null
    var countryCode = ""
    var altcountryCode = ""
    private lateinit var binding: FragmentOnboardingShopkeeperInfoBinding
//    private var binding = _binding!!
    private var navController: NavController?= null

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOnboardingShopkeeperInfoBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[NewOnboardingViewModel::class.java]
        binding.data = viewModel
        binding.lifecycleOwner = this
        viewModel.data.value?.pid = ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = Navigation.findNavController(view)
        binding.back.setOnClickListener{
            navController?.navigate(R.id.action_onboarding_shopkeeper_info_to_pucoPointList)
        }

        binding.countryCodeInput.setOnClickListener {
            launchCountryCodePicker(requireContext())
        }

        binding.AltCountryCodeInput.setOnClickListener {
            altlaunchCountryCodePicker(requireContext())
        }

        binding.proceedButton.setOnClickListener {
            huckup = true
            if (CheckAllFields() && shopkeeperUri.toString() !="") {
                val Name = binding.Name.text.toString()
                val Email = binding.email.text.toString()
                username = Email.split('@')[0]
                if(binding.Phone.length()>10){
                    var count = 1
                    var la = ""
                    val le = binding.countryCodeInput.text.toString().length
                    for(i in binding.Phone.text.toString()){
                        la += i
                        if(count == le){
                            break
                        }
                        count += 1
                    }
                    if(la != binding.countryCodeInput.text.toString()){
                        Toast.makeText(requireContext(),"invalid country code $la", Toast.LENGTH_LONG).show()
                        huckup = false
                    }else{
                        Phone = binding.Phone.text.toString()
                        phoneNum = binding.Phone.text.toString()
                        phoneCountryCode = binding.countryCodeInput.text.toString()
                    }
                    if( (binding.Phone.text.toString().length - binding.countryCodeInput.text.toString().length) != 10){
                        val b = binding.Phone.text.toString().length
                        val a = binding.countryCodeInput.text.toString().length
                        Toast.makeText(requireContext(),"invalid phone number or country code $b - $a", Toast.LENGTH_LONG).show()
                        huckup = false
                    }
                }else{
                    Phone = binding.countryCodeInput.text.toString() + binding.Phone.text.toString()
                    phoneNum = binding.Phone.text.toString()
                    phoneCountryCode = binding.countryCodeInput.text.toString()
                }
                if(binding.AlternatePhone.length()>10){
                    var count = 1
                    var la = ""
                    val le = binding.AltCountryCodeInput.text.toString().length
                    for(i in binding.AlternatePhone.text.toString()){
                        la += i
                        if(count == le){
                            break
                        }
                        count += 1
                    }
                    if(la != binding.AltCountryCodeInput.text.toString()){
                        Toast.makeText(requireContext(),"invalid country code alt $la", Toast.LENGTH_LONG).show()

                        huckup = false
                    }else{
                        AltPhone =binding.AlternatePhone.text.toString()
                        altNum = binding.AlternatePhone.text.toString()
                        altCountryCode = binding.AltCountryCodeInput.text.toString()
                    }
                    if( (binding.AlternatePhone.text.toString().length - binding.AltCountryCodeInput.text.toString().length) != 10){
                        val b = binding.AlternatePhone.text.toString().length
                        val a = binding.AltCountryCodeInput.text.toString().length
                        Toast.makeText(requireContext(),"invalid phone number or country code $b - $a", Toast.LENGTH_LONG).show()
                        huckup = false
                    }
                } else{
                    AltPhone = binding.AltCountryCodeInput.text.toString() + binding.AlternatePhone.text.toString()
                    altNum = binding.AlternatePhone.text.toString()
                    altCountryCode = binding.AltCountryCodeInput.text.toString()
                }
                if (huckup){
                    if(AltPhone != "" && Phone != ""){
                        catchData(
                            Name,
                            Email,
                            Phone,
                            AltPhone,
                            shopkeeperUri.toString(),
                            username,
                            phoneCountryCode,
                            phoneNum,
                            altCountryCode,
                            altNum
                        )
                        val direction = OnboardingShopkeeperInfoDirections.actionOnboardingShopkeeperInfoToOnboardingShopInfo()
                        navController!!.navigate(direction)
                        closeKeyboard()
                    }else{
                        Toast.makeText(requireContext(),"something went wrong", Toast.LENGTH_LONG).show()
                    }
                }
            }
            if(shopkeeperUri.toString() ==""){
                Toast.makeText(requireContext(),"Image is not selected",Toast.LENGTH_LONG).show()
            }
        }
        isValidEmail(binding.email.toString())
        isValidPhoneNumber(binding.Phone)
        initImageCaptureManager()
        binding.userimage.setOnClickListener{
            imageCaptureManager.startImageChooser(
                uniqueRequestCode = SHOPKEEPER_IMAGE_REQUEST_CODE,
                forProfile = true
            )
        }
    }

    private fun altlaunchCountryCodePicker(context: Context) {
        context.launchCountryPickerDialog {
            Log.d(
                TAG,
                "launchCountryCodePicker: country details: phoneCode: ${it?.phoneCode}; flagEmoji: ${it?.flagEmoji}"
            )
            altsetCountryText(it?.phoneCode.toString(), it?.flagEmoji)
            altcountryCode = it?.phoneCode?.toString()!!
        }
    }

    private fun launchCountryCodePicker(context: Context) {
        context.launchCountryPickerDialog {
            Log.d(
                TAG,
                "launchCountryCodePicker: country details: phoneCode: ${it?.phoneCode}; flagEmoji: ${it?.flagEmoji}"
            )
            setCountryText(it?.phoneCode.toString(), it?.flagEmoji)
            countryCode = it?.phoneCode?.toString()!!
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCountryText(phoneCode: String?, flagEmoji: String?) {
        binding.countryCodeInput.setText("+$phoneCode")
    }

    @SuppressLint("SetTextI18n")
    private fun altsetCountryText(phoneCode: String?, flagEmoji: String?) {
        binding.AltCountryCodeInput.setText("+$phoneCode")
    }

    private fun setOnClickListeners() {

    }

    private fun catchData(
        Name: String,
        Email: String,
        Phone: String,
        AltPhone: String,
        shopkeeperImg: String,
        username: String,
        phoneCountryCode: String,
        phoneNum: String,
        altCountryCode: String,
        altNum: String,
    ) {
        val db = AppDatabase.getDatabase(context!!)

        val shopkeeperDatabaseMethods = db.shopkeeperDatabaseMethods()

        viewLifecycleOwner.lifecycleScope.launch{
            shopkeeperDatabaseMethods.insertShopkeeperInfo(ShopkeepersInfo(0, Name, Email, Phone, AltPhone, shopkeeperImg, username, phoneCountryCode, phoneNum, altCountryCode, altNum))
        }
    }


    private fun initImageCaptureManager() {
        imageCaptureManager = ImageCaptureManager(this) { uri, _, uniqueRequestCode ->
            if(uniqueRequestCode == SHOPKEEPER_IMAGE_REQUEST_CODE) {
                shopkeeperUri = uri?: Uri.EMPTY
                uri.let {
                    binding.userimage.setImageURI(uri)

                }
            }
        }
    }

    private fun closeKeyboard() {
        val view = requireActivity().currentFocus
        if (view != null) {
            val manager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }


    private fun isValidEmail(email: String): Boolean {
        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches()
    }
    private fun isValidPhoneNumber(target: EditText?): Boolean {
        for(i in target?.text.toString()){
            if (target?.text.toString()[0] == '+'){
                val variable = target?.text.toString().drop(1)
                for(j in variable){
                    if(!j.isDigit()){
                        Log.i(TAG, "isValidPhoneNumber: $j")
                        Toast.makeText(requireContext(),"phone number error at j: $j", Toast.LENGTH_LONG).show()
                        return true
                    }
                }

            } else{
                if(!i.isDigit()){
                    Log.i(TAG, "isValidPhoneNumber: $i")
                    Toast.makeText(requireContext(),"phone number error at i: $i", Toast.LENGTH_LONG).show()
                    return true
                }
            }

        }

        return if (target == null || target.length() < 10 || target.length() > 14) {
            true
        } else {
            Patterns.PHONE.matcher(target.toString()).matches()
        }
    }

    private fun CheckAllFields(): Boolean {
        if (binding.Name.length() == 0) {
            binding.Name.error = "This field is required"
            return false
        }

        if(!isValidEmail(binding.email.text.toString().trim())){
            Toast.makeText(context, "InValid Email Address.", Toast.LENGTH_SHORT).show();
            return false
        }
        if (isValidPhoneNumber(binding.Phone)) {
            binding.Phone.error = "inValid phone number"
            return false
        }
        if (isValidPhoneNumber(binding.AlternatePhone)) {
            binding.AlternatePhone!!.error = "inValid phone number"
            return false
        }

        if(binding.countryCodeInput.text.toString() == ""){
            Toast.makeText(requireContext(),"require country code", Toast.LENGTH_LONG).show()
            return false
        }

        if(binding.AltCountryCodeInput.text.toString() == ""){
            Toast.makeText(requireContext(),"require alt phone no. country code", Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    companion object {
        private const val TAG = "OnboardingShopkeeperInf"
        private const val AADHAR_IMAGE_REQUEST_CODE = 1
        private const val SHOPKEEPER_IMAGE_REQUEST_CODE = 2
    }

    private enum class ImageType {
        SHOPKEEPER, AADHAR
    }
}



