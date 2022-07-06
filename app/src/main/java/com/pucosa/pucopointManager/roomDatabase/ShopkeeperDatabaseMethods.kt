package com.pucosa.pucopointManager.roomDatabase

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.CoroutineScope


@Database(entities = [ShopkeepersInfo::class, ShopLocationInfo::class,  Aadhaar::class, Shop::class, Signature::class], version = 3)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shopkeeperDatabaseMethods(): ShopkeeperDatabaseMethods

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase{
            if(INSTANCE == null){
                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "database-name"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = db
            }else{
                return INSTANCE!!
            }
            return INSTANCE!!
        }

    }
}

@Dao
interface ShopkeeperDatabaseMethods {

    @Query("SELECT * FROM shopkeepersInfo")
    suspend fun excessShopkeeperInfo():ShopkeepersInfo
    @Insert
    suspend fun insertShopkeeperInfo(shopkeepersInfo: ShopkeepersInfo)
    @Query("DELETE FROM shopkeepersInfo")
    suspend fun deleteShopkeeperInfo()



    @Query("SELECT * FROM shopLocationInfo")
    suspend fun excessShopLocationInfo():ShopLocationInfo
    @Insert
    suspend fun insertShopLocationInfo(shopLocationInfo: ShopLocationInfo)
    @Query("DELETE FROM shopLocationInfo")
    suspend fun deleteShopLocationrInfo()


    @Query("SELECT * FROM aadhaar")
    suspend fun excessAadhar():Aadhaar
    @Insert
    suspend fun insertAadhaar(aadhaar: Aadhaar)
    @Query("DELETE FROM aadhaar")
    suspend fun deleteAadhaar()


    @Query("SELECT * FROM shop")
    suspend fun excessShop():Shop
    @Insert
    suspend fun insertShop(shop: Shop)
    @Query("DELETE FROM shop")
    suspend fun deleteShop()


    @Query("SELECT * FROM signature")
    suspend fun excessSignature():Signature
    @Insert
    suspend fun insertSignature(signature: Signature)
    @Query("DELETE FROM signature")
    suspend fun deleteSignature()


}