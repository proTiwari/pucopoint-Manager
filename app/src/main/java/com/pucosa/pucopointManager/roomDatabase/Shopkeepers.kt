package com.pucosa.pucopointManager.roomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopkeepersInfo")
data class ShopkeepersInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var name: String,
    var email: String,
    var phone: String,
    var altPhone: String,
    var shopOwnerImage: String,
    var username: String,
    var phoneCountryCode: String,
    var phoneNum: String,
    var altCountryCode: String,
    var altNum: String,
){
    constructor() : this(0, "", "", "", "", "","", "", "", "", "")
}

@Entity(tableName = "shopLocationInfo")
data class ShopLocationInfo(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var lat: String,
    var lon: String,
    var city: String,
    var state: String,
    var country: String,
    var pincode: String,
    var streetAddress: String,
){
    constructor() : this(0, "", "", "", "", "", "", "")
}

@Entity(tableName = "aadhaar")
data class Aadhaar(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var aadharNumber: String,
    var aadhaarImage: String,
){
    constructor() : this(0, "", "")
}

@Entity(tableName = "shop")
data class Shop(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var shopName: String,
    var shopImage: String,
){
    constructor() : this(0, "", "")
}

@Entity(tableName = "signature")
data class Signature(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var pid: String,
    var shopUri: String,
    var shopkeeperUri: String,
    var aadharImageUri: String,
    var signatureImage: String,
){
    constructor() : this(0, "", "", "", "", "")
}