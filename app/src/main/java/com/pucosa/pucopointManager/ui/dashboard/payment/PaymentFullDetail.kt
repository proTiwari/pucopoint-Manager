package com.pucosa.pucopointManager.ui.dashboard.payment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.constants.DbCollections
import com.pucosa.pucopointManager.databinding.FragmentPaymentFullDetailBinding
import com.pucosa.pucopointManager.databinding.PaymentListBinding
import com.pucosa.pucopointManager.models.Payment
import com.pucosa.pucopointManager.ui.newOnboarding.pages.siglePageAdapter.ShopkeeperFullDetailArgs
import com.pucosa.pucopointManager.utils.Extensions.toRupeesText
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class PaymentFullDetail : Fragment() {
    private lateinit var binding: FragmentPaymentFullDetailBinding
    private val args: PaymentFullDetailArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentPaymentFullDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.clearBtn.isVisible = !args.paymentfulldetail.cleared
        setOnClickListeners()
        setDetails()
    }

    private fun setDetails() {
        val payment = args.paymentfulldetail
        with(binding) {
            exchangeIdVal.text = payment.exchangeId
            orderNoVal.text = payment.orderNo.toString()
            toVal.text = payment.to
            toDesVal.text = payment.toDescription
            fromVal.text = payment.from
            fromDesVal.text = payment.fromDescription
            statusVal.text = if(payment.cleared) "cleared" else "pending"
            amountVal.text = payment.amount.toString().toRupeesText()
            clearanceTimeVal.text = payment.clearanceTimestamp?.toDate().toString()
            creationTimeVal.text = payment.creationTimestamp.toDate().toString()
            ppManagerVal.text = payment.ppManagerId
            paymentIdVal.text = payment.paymentId
        }
    }

    private fun setOnClickListeners() {
        binding.back.setOnClickListener{
            findNavController().navigate(R.id.action_paymentFullDetail_to_paymentList)
        }

        binding.clearBtn.setOnClickListener {
            if(args.paymentfulldetail.cleared) throw Exception("Payment is already cleared. Still trying to clear it.")

            //clear it
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Clearance Confirmation")
                .setMessage("Do you really want to mark this payment as cleared?")
                .setPositiveButton("Yes, clear it") { dialog, which ->
                    dialog.dismiss()
                    clearThePayment()
                }
                .setNegativeButton("No, Cancel") { dialog, which ->
                    dialog.dismiss()
                }
                .show()
        }
    }

    private fun clearThePayment() {
        viewLifecycleOwner.lifecycleScope.launch {
            val map = mapOf(
                Payment::cleared.name to true,
                Payment::clearanceTimestamp.name to Timestamp.now(),
            )
            try {
                Firebase.firestore.document("${DbCollections.EXCHANGES}/${args.paymentfulldetail.exchangeId}/${DbCollections.PAYMENTS}/${args.paymentfulldetail.paymentId}")
                    .update(map)
                    .await()
                Toast.makeText(
                    requireContext(),
                    "Payment cleared successfully.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.clearBtn.isVisible = false
                binding.statusVal.text = "cleared"
                binding.clearanceTimeVal.text = (map[Payment::clearanceTimestamp.name] as Timestamp?)?.toDate().toString()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "setOnClickListeners: exception occurred when clearing the payment",
                    e
                )
                Toast.makeText(requireContext(), "Something went wrong", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    companion object {
        private const val TAG = "PaymentFullDetail"
    }
}

