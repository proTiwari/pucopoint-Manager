package com.pucosa.pucopointManager.map

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestoreException
import com.pucosa.pucopointManager.R
import com.pucosa.pucopointManager.models.Pucopoint

class MapAdapter(
    context: Context?,
    options: FirestoreRecyclerOptions<Pucopoint>,
    val loadingComplete: (itemCount: Int) -> Unit,
    val onItemClicked: (pucopoint: Pucopoint, Int) -> Unit
) : FirestoreRecyclerAdapter<Pucopoint, MapAdapter.ViewHolder>(options) {

    private var context: Context? = context

    private var searchText: String = ""
    
    // indicate if search mode
    private val isSearch: Boolean
        get() = searchText.isNotBlank()
    // store the list of filtered search items
    private var searchItems: MutableList<Pucopoint> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.d(TAG, "onCreateViewHolder: here we are")
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.mapsinglerow,parent,false))
    }

    override fun onError(e: FirebaseFirestoreException) {
        super.onError(e)
        Log.e(TAG, "onError: error while fetching inventory data : ", e)
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

    // Holds the views for adding it to image and text
    class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        //  val imageView: ImageView = itemView.findViewById(R.id.image)
        val name = itemView.findViewById<TextView>(R.id.name)
        val locality = itemView.findViewById<TextView>(R.id.locality)
        val phone = itemView.findViewById<TextView>(R.id.phone)
        val email = itemView.findViewById<TextView>(R.id.email)
        val image = itemView.findViewById<ImageView>(R.id.image)
    }


    @SuppressLint("NotifyDataSetChanged")
    fun search(text: String) {
        searchText = text
        notifyDataSetChanged()
    }

    companion object {
        const val TAG = "MapAdapter"
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
        model: Pucopoint
    ) {
        holder.name.text = model.name
        holder.locality.text = model.streetAddress
        holder.phone.text = model.phone
        holder.email.text = model.email
        context?.let { Glide.with(it).load(model.shopkeeperImageUrl).into(holder.image) }
        println(model.name)
        holder.itemView.setOnClickListener{
            onItemClicked(model, position)
        }
    }
}