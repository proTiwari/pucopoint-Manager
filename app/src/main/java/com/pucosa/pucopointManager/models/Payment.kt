package com.pucosa.pucopointManager.models

import android.os.Parcelable
import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pucosa.pucopointManager.constants.DbCollections
import kotlinx.parcelize.Parcelize

@Keep
@IgnoreExtraProperties
@Parcelize
data class Payment(
    var exchangeId: String = "",
    var orderNo: Long = 0,
    var stackholders: List<String> = listOf("", ""),
    var paymentId: String = "",
    var from: String = "",
    var to: String = "",
    var fromDescription: String = "",
    var toDescription: String = "",
    var amount: Double = 0.0,
    var cleared: Boolean = false,
    var creationTimestamp: Timestamp = Timestamp.now(),
    var clearanceTimestamp: Timestamp? = null,
    var ppManagerId: String = ""
): Parcelable {
    companion object {
        fun makePaymentClearedWriteBatch(writeBatch: WriteBatch, exchangeId: String, paymentId: String): WriteBatch {
            val doc = Firebase.firestore.collection("${DbCollections.EXCHANGES}/$exchangeId/${DbCollections.PAYMENTS}").document(paymentId)
            val data = mapOf(
                Payment::cleared.name to true,
                Payment::clearanceTimestamp.name to Timestamp.now()
            )
            return writeBatch.update(doc, data)
        }
    }
}
