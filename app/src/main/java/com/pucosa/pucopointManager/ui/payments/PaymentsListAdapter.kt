package com.pucosa.pucopointManager.ui.payments

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.constants.PaymentStackholders
import com.pucosa.pucopointManager.databinding.FragmentPaymentsListItemBinding
import com.pucosa.pucopointManager.models.Payment
import com.pucosa.pucopointManager.utils.Extensions.toRupeesText
import java.text.SimpleDateFormat
import java.util.*

class PaymentsListAdapter(
    options: FirestoreRecyclerOptions<Payment>,
    val fragment: Fragment,
    val loadingComplete: (itemCount: Int) -> Unit,
    val onClickItem: (p: Payment) -> Unit
) : FirestoreRecyclerAdapter<Payment, PaymentsListAdapter.ViewHolder>(options) {

    private var inflater: LayoutInflater = LayoutInflater.from(fragment.requireContext())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentPaymentsListItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Payment) {
        holder.setListDetails(model)
        holder.itemView.setOnClickListener {
            onClickItem(model)
        }
    }

    inner class ViewHolder(val binding: FragmentPaymentsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun setListDetails(payment: Payment) {
            val user = Firebase.auth.currentUser!!
            val moneyPre = when(PaymentStackholders.Pucoreads.name) {
                payment.to -> if(payment.cleared) "Received" else "To be Received"
                payment.from -> if(payment.cleared) "Payed" else "To be payed"
                else -> throw Exception("wrong payment details")
            }

            val indicatorRes = if(payment.cleared) R.color.colorTertiary else R.color.colorSecondary
            binding.signalColorView.setBackgroundColor(ContextCompat.getColor(fragment.requireContext(), indicatorRes))
            binding.moneyDetailTv.text = "${moneyPre}: ${payment.amount.toString().toRupeesText()}"

            binding.dateTv.isVisible = payment.clearanceTimestamp != null
            if(payment.clearanceTimestamp != null) {
                val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.dateTv.text = "Cleared on ${formatter.format(payment.clearanceTimestamp!!.toDate())}"
            }

            binding.messageTv.text = when(PaymentStackholders.Pucoreads.name) {
                payment.to -> {
                    if (payment.cleared)
                        "You received ${payment.amount.toString().toRupeesText()} from ${payment.fromDescription} on order having order no: ${payment.orderNo}."
                    else
                        "You will be receiving ${payment.amount.toString().toRupeesText()} from ${payment.fromDescription} on order having order no: ${payment.orderNo}."
                }
                payment.from -> {
                    val sh = PaymentStackholders.valueOf(payment.toDescription)
                    if (payment.cleared)
                        "You payed ${payment.amount.toString().toRupeesText()} to ${payment.fromDescription} on order having order no: ${payment.orderNo}."
                    else
                        "You need to pay ${payment.amount.toString().toRupeesText()} to ${payment.fromDescription} on order having order no: ${payment.orderNo}."
                }
                else -> throw Exception("wrong payment details")
            }


        }

    }

    override fun onDataChanged() {
        super.onDataChanged()
        loadingComplete(itemCount)
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e(TAG, "onError: error while fetching inventory data : ", e)

    }

    companion object {
        private const val TAG = "PaymentsListAdapter"
    }

}