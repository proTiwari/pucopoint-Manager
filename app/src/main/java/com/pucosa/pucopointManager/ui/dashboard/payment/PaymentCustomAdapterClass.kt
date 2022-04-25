package com.pucosa.pucopointManager.ui.dashboard.payment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.pucosa.pucopointManager.R

import com.pucosa.pucopointManager.ui.newOnboarding.pages.recycle.CustomAdapter
import com.pucosa.pucopointManager.models.Pucopoint

class PaymentCustomAdapterClass(
    options: FirestoreRecyclerOptions<Pucopoint>,
    val loadingComplete: (itemCount: Int) -> Unit,
    val onItemClicked: (Any?, Any?) -> Unit
): FirestoreRecyclerAdapter<Pucopoint, PaymentCustomAdapterClass.ViewHolder>(options)  {


    private var searchText: String = ""
    // indicate if search mode
    private val isSearch: Boolean
        get() = searchText.isNotBlank()
    // store the list of filtered search items
    private var searchItems: MutableList<Pucopoint> = mutableListOf()

    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //  val imageView: ImageView = itemView.findViewById(R.id.image)
        val status = itemView.findViewById<TextView>(R.id.status)
        val pid = itemView.findViewById<TextView>(R.id.pucopointid)
    }
    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e(CustomAdapter.TAG, "onError: error while fetching inventory data : ", e)
    }

    override fun onDataChanged() {
        super.onDataChanged()
        loadingComplete(itemCount)
    }

    override fun getItemCount(): Int {
        return if (isSearch) {
            searchItems = mutableListOf()
            for (position in 0 until super.getItemCount()) {
                val item = super.getItem(position)
                if (item.streetAddress.contains(searchText)) {
                    searchItems.add(item)
                }
            }
            searchItems.size
        }

        else {
            super.getItemCount()
        }
    }

    override fun getItem(position: Int): Pucopoint {
        return if (!isSearch) {
            super.getItem(position)
        }
        else {
            searchItems[position]
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return PaymentCustomAdapterClass.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.singlerowpayment, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, pucopoint: Pucopoint) {
        holder.status.text = pucopoint.name
        holder.pid.text = pucopoint.streetAddress
        holder.itemView.setOnClickListener{
            onItemClicked(pucopoint, position)
        }
    }

    fun search(text: String) {
        searchText = text
        notifyDataSetChanged()
    }

    companion object {
        private const val TAG = "PaymentCustomAdapter"
    }

}