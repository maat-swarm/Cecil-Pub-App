package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String, // "Beers & Lagers", "Ciders & Coolers", "Spirits", "Wines", "Premium", "Non-Alcoholic"
    val price: Double, // Price per bottle/unit in Rand
    val warehouseStockCases: Int,
    val warehouseStockLoose: Int = 0,
    val floorStockCases: Int,
    val floorStockLoose: Int = 0,
    val caseSizeUnits: Int = 12,
    val stockConfidence: String = "exact", // "exact" or "estimated"
    val reorderLevelCases: Int = 2,
    val active: Boolean = true,
    val supplier: String = "SAB", // "SAB", "Heineken", "Other"
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val totalFloorBottles: Int
        get() = (floorStockCases * caseSizeUnits) + floorStockLoose

    val totalWarehouseBottles: Int
        get() = (warehouseStockCases * caseSizeUnits) + warehouseStockLoose

    val isLowStock: Boolean
        get() = (warehouseStockCases + floorStockCases) <= reorderLevelCases
}

@Entity(tableName = "deliveries")
data class DeliveryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val cases: Int,
    val supplier: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "picks")
data class PickEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productName: String,
    val cases: Int,
    val looseUnits: Int = 0,
    val pickedBy: String = "Cecil", // "Cecil" or "Helper"
    val auto: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_sales")
data class DailySaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val totalRand: Double,
    val cashRand: Double,
    val cardRand: Double,
    val eftRand: Double,
    val salesCount: Int = 1,
    val lineItemsJson: String = "[]",
    val source: String = "quick", // "pos_photo", "manual", "quick"
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val supplier: String, // "SAB", "Heineken", "Other"
    val itemsJson: String, // e.g. [{"name":"Castle Lager","cases":5,"casePrice":280.0}]
    val totalEstimate: Double,
    val status: String = "draft", // "draft" or "sent"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "ai_messages")
data class AiMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val role: String, // "user" or "assistant"
    val content: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "settings")
data class TavernSettingsEntity(
    @PrimaryKey
    val id: Long = 1,
    val businessName: String = "Cecil's Pub",
    val address: String = "Skylab Street, Tlamatlama Ext, Tembisa",
    val ownerPhone: String = "072 555 1234",
    val ownerEmail: String = "websitemaatmaster@gmail.com",
    val helperPin: String = "1234",
    val supplierSabPhone: String = "011 881 8111",
    val supplierHeinekenPhone: String = "011 878 1200",
    val notifyEveningCheck: Boolean = true,
    val notifyMorningVerify: Boolean = true,
    val notifySundayCount: Boolean = true,
    val streakDays: Int = 5,
    val lastReconcileDate: String = ""
)

data class DiscrepancyReport(
    val productId: Long,
    val productName: String,
    val missingUnits: Int,
    val expectedFloorUnits: Int,
    val actualFloorUnits: Int,
    val lastPickBy: String,
    val lastPickTimestamp: Long
)

data class OrderItem(
    val productId: Long,
    val productName: String,
    val supplier: String,
    val suggestedCases: Int,
    var selectedCases: Int,
    val estimatedCasePrice: Double,
    val reason: String
)
