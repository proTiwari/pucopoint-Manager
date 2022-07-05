package com.pucosa.pucopointManager.roomDatabase

import android.content.Context
import androidx.room.*
import com.pucosa.pucopointManager.ui.newOnboarding.pages.AadharFragment
import com.pucosa.pucopointManager.ui.newOnboarding.pages.OnboardingShopInfo


@Database(entities = [ShopkeepersInfo::class, ShopLocationInfo::class,  Aadhaar::class, Shop::class, Signature::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun shopkeeperDatabaseMethods(): ShopkeeperDatabaseMethods

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context?): AppDatabase{
            if(INSTANCE == null){
                val db = Room.databaseBuilder(
                    context!!,
                    AppDatabase::class.java, "database-name"
                ).build()
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

//    @Query("SELECT * FROM shopkeepersInfo")
//    suspend fun getAll(shopkeepersInfo: ShopkeepersInfo)
//    @Query("SELECT * FROM shopkeepersInfo WHERE id IN ()")
//    suspend fun loadAllByIds(shopkeepersInfo: ShopkeepersInfo)
//    @Query("SELECT * FROM shopkeepersInfo WHERE shopkeepersInfo LIKE :shopkeepersInfo LIMIT 1")
//    suspend fun findByName(shopkeepersInfo: ShopkeepersInfo)

    @Query("SELECT * FROM shopkeepersInfo")
    suspend fun excessShopkeeperInfo():ShopkeepersInfo

    @Insert
    suspend fun insertShopkeeperInfo(shopkeepersInfo: ShopkeepersInfo)



    @Insert
    suspend fun insertShopLocationInfo(shopLocationInfo: ShopLocationInfo)


    @Insert
    suspend fun insertAadhaar(aadhaar: Aadhaar)


    @Insert
    suspend fun insertShop(shop: Shop)


    @Insert
    suspend fun insertSignature(signature: Signature)


}