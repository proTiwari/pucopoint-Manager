package com.pucosa.pucopointManager.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Map(
    var list: MutableList<String>? = null

): Parcelable
