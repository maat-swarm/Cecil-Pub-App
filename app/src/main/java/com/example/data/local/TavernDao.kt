package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AiMessageEntity
import com.example.data.model.DailySaleEntity
import com.example.data.model.DeliveryEntity
import com.example.data.model.OrderEntity
import com.example.data.model.PickEntity
import com.example.data.model.ProductEntity
import com.example.data.model.TavernSettingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE active = 1 ORDER BY category ASC, name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): ProductEntity?

    @Query("SELECT * FROM products")
    suspend fun getAllProductsList(): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("UPDATE products SET floorStockCases = :cases, floorStockLoose = :loose, stockConfidence = :confidence, lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateFloorStock(id: Long, cases: Int, loose: Int, confidence: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE products SET warehouseStockCases = :cases, warehouseStockLoose = :loose, lastUpdated = :timestamp WHERE id = :id")
    suspend fun updateWarehouseStock(id: Long, cases: Int, loose: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE products SET price = price + :deltaPrice WHERE category = 'Beers & Lagers'")
    suspend fun bulkUpdateBeerPrice(deltaPrice: Double)
}

@Dao
interface DeliveryDao {
    @Query("SELECT * FROM deliveries ORDER BY createdAt DESC")
    fun getAllDeliveries(): Flow<List<DeliveryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDelivery(delivery: DeliveryEntity): Long
}

@Dao
interface PickDao {
    @Query("SELECT * FROM picks ORDER BY createdAt DESC")
    fun getAllPicks(): Flow<List<PickEntity>>

    @Query("SELECT * FROM picks ORDER BY createdAt DESC LIMIT :limit")
    fun getRecentPicks(limit: Int): Flow<List<PickEntity>>

    @Query("SELECT * FROM picks WHERE productId = :productId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLastPickForProduct(productId: Long): PickEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPick(pick: PickEntity): Long
}

@Dao
interface DailySaleDao {
    @Query("SELECT * FROM daily_sales ORDER BY createdAt DESC")
    fun getAllSales(): Flow<List<DailySaleEntity>>

    @Query("SELECT * FROM daily_sales WHERE date = :date ORDER BY createdAt DESC")
    fun getSalesForDate(date: String): Flow<List<DailySaleEntity>>

    @Query("SELECT * FROM daily_sales WHERE date = :date")
    suspend fun getSalesListForDate(date: String): List<DailySaleEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSale(sale: DailySaleEntity): Long
}

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: OrderEntity): Long

    @Update
    suspend fun updateOrder(order: OrderEntity)
}

@Dao
interface AiMessageDao {
    @Query("SELECT * FROM ai_messages ORDER BY createdAt ASC")
    fun getAllMessages(): Flow<List<AiMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: AiMessageEntity): Long

    @Query("DELETE FROM ai_messages")
    suspend fun clearAll()
}

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    fun getSettings(): Flow<TavernSettingsEntity?>

    @Query("SELECT * FROM settings WHERE id = 1 LIMIT 1")
    suspend fun getSettingsSync(): TavernSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(settings: TavernSettingsEntity)
}
