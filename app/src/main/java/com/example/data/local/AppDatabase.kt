package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AiMessageEntity
import com.example.data.model.DailySaleEntity
import com.example.data.model.DeliveryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PickEntity
import com.example.data.model.ProductEntity
import com.example.data.model.TavernSettingsEntity

@Database(
    entities = [
        ProductEntity::class,
        DeliveryEntity::class,
        PickEntity::class,
        DailySaleEntity::class,
        OrderEntity::class,
        AiMessageEntity::class,
        TavernSettingsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun deliveryDao(): DeliveryDao
    abstract fun pickDao(): PickDao
    abstract fun dailySaleDao(): DailySaleDao
    abstract fun orderDao(): OrderDao
    abstract fun aiMessageDao(): AiMessageDao
    abstract fun settingsDao(): SettingsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cecil_pub_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
