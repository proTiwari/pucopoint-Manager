package com.pucosa.pucopointManager.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.firestore.IgnoreExtraProperties
import kotlinx.parcelize.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
data class Pucopoint(
    var pid: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var altPhone: String = "",
    var aadhar: String = "",
    var aadharImageUrl: String = "",
    var shopkeeperImageUrl: String = "",
    var shopImageUrl: String = "",
    var signaturePad: String? = "",
    var lat: Double? = 0.0,
    var long: Double? = 0.0,
    var geohash: String = "",
    var shopName: String = "",
    var country: String = "",
    var state: String = "",
    var city: String = "",
    var streetAddress: String = "",
    var pincode: String = "",
    var username: String = "",
    var phoneCountryCode: String = "",
    var altCountryCode: String = "",
    var phoneNum: String = "",
    var altNum: String = "",
    var manager: String = ""
): Parcelable {
    fun getFullAddressString(): String {
        return "$streetAddress, $city, $state, $country, $pincode"
    }
}