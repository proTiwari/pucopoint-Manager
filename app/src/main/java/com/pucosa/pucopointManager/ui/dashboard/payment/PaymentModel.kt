package com.pucosa.pucopointManager.ui.dashboard.payment

import android.os.Parcelable
import android.text.Editable
import android.widget.EditText
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize
import java.util.*

@Keep
@IgnoreExtraProperties
@Parcelize
data class PaymentModel(
    var amount: String? = "",
    var buyerId: String? = "",
    var buyerStatus: String? = "",
    var sellerId: String? = "",
    var sellerStatus: String? = "",
    var transectionId: String? = "",
    var bookName: String? = "",
    var status: String? = "",
    var upi: String? = "",
    var date: Date,
    var pucopointDoc: String = "",
    var pid: String = "",
    var name: String ="",
    var email: String = "",
    var phone: String = "",
    var altPhone: String? = "",
    var aadhar: String = "",
    var aadharImageUrl: String = "",
    var shopkeeperImageUrl: String = "",
    var shopImageUrl: String = "",
    var lat: Double = 0.0,
    var long: Double = 0.0,
    var geohash: String = "",
    var shopName: String = "",
    var country: String = "",
    var state: String = "",
    var city: String = "",
    var streetAddress: String = "",
    var pincode: String = "",
    var locality: String = "",
    var username: String = ""
): Parcelable
