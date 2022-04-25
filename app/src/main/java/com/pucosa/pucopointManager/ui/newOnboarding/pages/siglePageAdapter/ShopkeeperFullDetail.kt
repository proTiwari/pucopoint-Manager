package com.pucosa.pucopointManager.ui.newOnboarding.pages.siglePageAdapter

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.databinding.PucopointFullDetailBinding
import com.pucosa.pucopointManager.models.Pucopoint


class ShopkeeperFullDetail : Fragment() {

    private val args: ShopkeeperFullDetailArgs by navArgs()
    private lateinit var navController: NavController
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance();
    private val collectionReference: CollectionReference = db.collection("pucopoints")
    var userAdapter: PageAdapterViewModel? = null
    private lateinit var binding: PucopointFullDetailBinding
    private var pucopoint: Pucopoint? = null



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = PucopointFullDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val storage = Firebase.storage
        val storageRef = storage.reference
        navController = Navigation.findNavController(view)
        val pucopoint = args.pucopoint

        binding.name.text = pucopoint.name
        binding.AlternatePhone.text = pucopoint.altPhone
        binding.aadhar.text = pucopoint.aadhar
        binding.country.text = pucopoint.country
        binding.city.text = pucopoint.city
        binding.shopName.text = pucopoint.shopName
        binding.email.text = pucopoint.email
        binding.latitude.text = pucopoint.lat.toString()
        binding.longitude.text = pucopoint.lon.toString()
        binding.state.text = pucopoint.state
        binding.phone.text = pucopoint.phone
        binding.pincode.text = pucopoint.pincode
        binding.username.text = pucopoint.username
        binding.streetAddress.text = pucopoint.streetAddress
        binding.back.setOnClickListener{
            navController.navigate(R.id.action_shopkeeperFullDetail_to_pucoPointList)
        }

        binding.delete.setOnClickListener{




            val builder = AlertDialog.Builder(context)
            builder.setCancelable(true)
            builder.setTitle("ATTENTION")
            builder.setMessage("ARE YOU SURE YOU WANT TO DELETE THIS PUCOPOINT")
            builder.setPositiveButton(
                "Confirm"
            ) { dialog, which ->
                deletingData(pucopoint.pid)
                val desertRef = storageRef.child("gs://pucoread.appspot.com/pucopoints/${pucopoint.pid}")
                Log.d(TAG, "onViewCreated: processing${pucopoint.pid}")
                //gs://pucoread.appspot.com/pucopoints/BXUcuCUObl4OfRW7sNh6
                desertRef.delete().addOnSuccessListener {
                    // File deleted successfully
                    Log.d(TAG, "onViewCreated: successful${pucopoint.pid}")
                }.addOnFailureListener {
                    // Uh-oh, an error occurred!
                    Log.d(TAG, "onViewCreated: faild${pucopoint.pid}")
                }
                navController.navigate(R.id.action_shopkeeperFullDetail_to_pucoPointList)
            }
            builder.setNegativeButton(
                android.R.string.cancel
            ) { dialog, which ->
                dialog.cancel()
            }
            val dialog: AlertDialog = builder.create()
            dialog.show()


        }

        Glide.with(requireActivity()).load(pucopoint.shopkeeperImageUrl).into(binding.shopkeeperImgView)
        Glide.with(requireActivity()).load(pucopoint.shopImageUrl).into(binding.shopImgView)
        Glide.with(requireActivity()).load(pucopoint.aadharImageUrl).into(binding.aadharImgView)
        Glide.with(requireActivity()).load(pucopoint.signaturePad).into(binding.signaturePad)

    }





    private fun deletingData(pid: String) {
        val pucoPointRef = Firebase.firestore.document("pucopoints/${pid}")
        Log.d(TAG, "onViewCreated: {$pid}")
        pucoPointRef.delete()
    }

    companion object {
        private const val TAG = "ShopkeeperFullDetail"
    }
}

