package com.pucosa.pucopointManager.utils

import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.firebase.geofire.GeoLocation
import java.util.*

object LocationUtils {
    fun geocodeLocation(context: Context, l: GeoLocation): MutableList<Address>? {
        val geoCoder = Geocoder(context, Locale.getDefault())
        return geoCoder.getFromLocation(l.latitude, l.longitude, 3)
    }
}