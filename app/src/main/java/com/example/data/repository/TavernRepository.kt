package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.AiMessageEntity
import com.example.data.model.DailySaleEntity
import com.example.data.model.DeliveryEntity
import com.example.data.model.DiscrepancyReport
import com.example.data.model.OrderEntity
import com.example.data.model.OrderItem
import com.example.data.model.PickEntity
import com.example.data.model.ProductEntity
import com.example.data.model.TavernSettingsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max

class TavernRepository(private val database: AppDatabase) {

    private val productDao = database.productDao()
    private val deliveryDao = database.deliveryDao()
    private val pickDao = database.pickDao()
    private val dailySaleDao = database.dailySaleDao()
    private val orderDao = database.orderDao()
    private val aiMessageDao = database.aiMessageDao()
    private val settingsDao = database.settingsDao()

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val allDeliveries: Flow<List<DeliveryEntity>> = deliveryDao.getAllDeliveries()
    val allPicks: Flow<List<PickEntity>> = pickDao.getAllPicks()
    val recentPicks: Flow<List<PickEntity>> = pickDao.getRecentPicks(10)
    val allSales: Flow<List<DailySaleEntity>> = dailySaleDao.getAllSales()
    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()
    val allAiMessages: Flow<List<AiMessageEntity>> = aiMessageDao.getAllMessages()
    val settings: Flow<TavernSettingsEntity?> = settingsDao.getSettings()

    suspend fun initDatabaseDefaults() = withContext(Dispatchers.IO) {
        val existingSettings = settingsDao.getSettingsSync()
        if (existingSettings == null) {
            settingsDao.insertOrUpdate(
                TavernSettingsEntity(
                    id = 1,
                    businessName = "Cecil's Pub",
                    address = "Skylab Street, Tlamatlama Ext, Tembisa",
                    ownerPhone = "072 555 1234",
                    ownerEmail = "websitemaatmaster@gmail.com",
                    helperPin = "1234",
                    supplierSabPhone = "011 881 8111",
                    supplierHeinekenPhone = "011 878 1200",
                    notifyEveningCheck = true,
                    notifyMorningVerify = true,
                    notifySundayCount = true,
                    streakDays = 5
                )
            )
        }

        val existingProducts = productDao.getAllProductsList()
        if (existingProducts.isEmpty()) {
            loadTembisaStarterPack()
        }
    }

    suspend fun loadTembisaStarterPack() = withContext(Dispatchers.IO) {
        val starterProducts = listOf(
            ProductEntity(
                name = "Castle Lager 750ml",
                category = "Beers & Lagers",
                price = 24.0,
                warehouseStockCases = 5,
                floorStockCases = 2,
                floorStockLoose = 5,
                caseSizeUnits = 12,
                reorderLevelCases = 3,
                supplier = "SAB"
            ),
            ProductEntity(
                name = "Carling Black Label 750ml",
                category = "Beers & Lagers",
                price = 25.0,
                warehouseStockCases = 6,
                floorStockCases = 3,
                floorStockLoose = 2,
                caseSizeUnits = 12,
                reorderLevelCases = 3,
                supplier = "SAB"
            ),
            ProductEntity(
                name = "Castle Lite 660ml",
                category = "Beers & Lagers",
                price = 26.0,
                warehouseStockCases = 1,
                floorStockCases = 1,
                floorStockLoose = 3,
                caseSizeUnits = 12,
                reorderLevelCases = 3,
                supplier = "SAB"
            ),
            ProductEntity(
                name = "Flying Fish Lemon 330ml",
                category = "Ciders & Coolers",
                price = 26.0,
                warehouseStockCases = 1,
                floorStockCases = 0,
                floorStockLoose = 8,
                caseSizeUnits = 24,
                reorderLevelCases = 2,
                supplier = "SAB"
            ),
            ProductEntity(
                name = "Heineken 650ml",
                category = "Beers & Lagers",
                price = 28.0,
                warehouseStockCases = 3,
                floorStockCases = 1,
                floorStockLoose = 6,
                caseSizeUnits = 12,
                reorderLevelCases = 2,
                supplier = "Heineken"
            ),
            ProductEntity(
                name = "Savanna Dry 330ml",
                category = "Ciders & Coolers",
                price = 29.0,
                warehouseStockCases = 2,
                floorStockCases = 1,
                floorStockLoose = 2,
                caseSizeUnits = 24,
                reorderLevelCases = 2,
                supplier = "Heineken"
            ),
            ProductEntity(
                name = "Smirnoff 1818 750ml",
                category = "Spirits",
                price = 160.0,
                warehouseStockCases = 2,
                floorStockCases = 0,
                floorStockLoose = 5,
                caseSizeUnits = 12,
                reorderLevelCases = 2,
                supplier = "Other"
            ),
            ProductEntity(
                name = "Gordon's London Dry Gin 750ml",
                category = "Spirits",
                price = 175.0,
                warehouseStockCases = 1,
                floorStockCases = 0,
                floorStockLoose = 3,
                caseSizeUnits = 12,
                reorderLevelCases = 1,
                supplier = "Other"
            ),
            ProductEntity(
                name = "Coca-Cola 300ml Glass",
                category = "Non-Alcoholic",
                price = 15.0,
                warehouseStockCases = 4,
                floorStockCases = 2,
                floorStockLoose = 6,
                caseSizeUnits = 24,
                reorderLevelCases = 2,
                supplier = "Other"
            ),
            ProductEntity(
                name = "Red Bull Energy 250ml",
                category = "Non-Alcoholic",
                price = 25.0,
                warehouseStockCases = 1,
                floorStockCases = 1,
                floorStockLoose = 4,
                caseSizeUnits = 24,
                reorderLevelCases = 2,
                supplier = "Other"
            )
        )
        productDao.insertProducts(starterProducts)

        // Seed an initial demo sale today so the till is lively
        val todayStr = getTodayDateString()
        dailySaleDao.insertSale(
            DailySaleEntity(
                date = todayStr,
                totalRand = 1450.00,
                cashRand = 950.00,
                cardRand = 350.00,
                eftRand = 150.00,
                salesCount = 18,
                source = "quick",
                lineItemsJson = """[{"name":"Carling Black Label 750ml","qty":12,"total":300.0},{"name":"Castle Lager 750ml","qty":10,"total":240.0},{"name":"Savanna Dry 330ml","qty":8,"total":232.0}]"""
            )
        )
    }

    suspend fun addProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: ProductEntity) = withContext(Dispatchers.IO) {
        productDao.deleteProduct(product)
    }

    suspend fun logDelivery(productId: Long, cases: Int, supplier: String) = withContext(Dispatchers.IO) {
        val product = productDao.getProductById(productId) ?: return@withContext
        deliveryDao.insertDelivery(
            DeliveryEntity(
                productId = productId,
                productName = product.name,
                cases = cases,
                supplier = supplier
            )
        )
        val newWarehouseCases = product.warehouseStockCases + cases
        productDao.updateWarehouseStock(productId, newWarehouseCases, product.warehouseStockLoose)
    }

    suspend fun logPick(productId: Long, cases: Int, pickedBy: String, auto: Boolean = false) = withContext(Dispatchers.IO) {
        val product = productDao.getProductById(productId) ?: return@withContext
        pickDao.insertPick(
            PickEntity(
                productId = productId,
                productName = product.name,
                cases = cases,
                pickedBy = pickedBy,
                auto = auto
            )
        )
        val newWarehouse = max(0, product.warehouseStockCases - cases)
        val newFloor = product.floorStockCases + cases
        productDao.updateWarehouseStock(productId, newWarehouse, product.warehouseStockLoose)
        productDao.updateFloorStock(productId, newFloor, product.floorStockLoose, product.stockConfidence)
    }

    suspend fun logQuickSale(amount: Double, method: String) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val existingSales = dailySaleDao.getSalesListForDate(today)
        val currentSale = existingSales.firstOrNull()

        var cash = 0.0
        var card = 0.0
        var eft = 0.0

        when (method.lowercase(Locale.US)) {
            "cash" -> cash = amount
            "card" -> card = amount
            "eft" -> eft = amount
            else -> cash = amount
        }

        if (currentSale != null) {
            dailySaleDao.insertSale(
                currentSale.copy(
                    totalRand = currentSale.totalRand + amount,
                    cashRand = currentSale.cashRand + cash,
                    cardRand = currentSale.cardRand + card,
                    eftRand = currentSale.eftRand + eft,
                    salesCount = currentSale.salesCount + 1,
                    createdAt = System.currentTimeMillis()
                )
            )
        } else {
            dailySaleDao.insertSale(
                DailySaleEntity(
                    date = today,
                    totalRand = amount,
                    cashRand = cash,
                    cardRand = card,
                    eftRand = eft,
                    salesCount = 1,
                    source = "quick"
                )
            )
        }
    }

    suspend fun logEodSale(
        totalRand: Double,
        cash: Double,
        card: Double,
        eft: Double,
        itemsSold: List<Pair<Long, Int>>, // productId to bottles sold
        source: String = "pos_photo",
        imageUrl: String? = null
    ) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val jsonArray = JSONArray()

        for ((productId, bottlesSold) in itemsSold) {
            val product = productDao.getProductById(productId) ?: continue
            val itemObj = JSONObject()
            itemObj.put("productId", productId)
            itemObj.put("name", product.name)
            itemObj.put("qty", bottlesSold)
            itemObj.put("total", bottlesSold * product.price)
            jsonArray.put(itemObj)

            // Deduct from floor stock
            var totalFloorBottles = product.totalFloorBottles
            var remainingFloorBottles = totalFloorBottles - bottlesSold
            var confidence = product.stockConfidence

            if (remainingFloorBottles < 0) {
                // Nightly reconciliation: floor stock was exceeded!
                // Auto-deduct from warehouse case, back-fill pick record auto=true
                val deficit = -remainingFloorBottles
                val casesNeeded = (deficit + product.caseSizeUnits - 1) / product.caseSizeUnits
                val deductedWarehouseCases = casesNeeded.coerceAtMost(product.warehouseStockCases)

                if (deductedWarehouseCases > 0) {
                    productDao.updateWarehouseStock(
                        product.id,
                        product.warehouseStockCases - deductedWarehouseCases,
                        product.warehouseStockLoose
                    )
                    pickDao.insertPick(
                        PickEntity(
                            productId = product.id,
                            productName = product.name,
                            cases = deductedWarehouseCases,
                            pickedBy = "Cecil (Auto)",
                            auto = true
                        )
                    )
                    remainingFloorBottles += (deductedWarehouseCases * product.caseSizeUnits)
                }
                confidence = "estimated"
            }

            val newFloorCases = max(0, remainingFloorBottles / product.caseSizeUnits)
            val newFloorLoose = max(0, remainingFloorBottles % product.caseSizeUnits)
            productDao.updateFloorStock(product.id, newFloorCases, newFloorLoose, confidence)
        }

        dailySaleDao.insertSale(
            DailySaleEntity(
                date = today,
                totalRand = totalRand,
                cashRand = cash,
                cardRand = card,
                eftRand = eft,
                salesCount = max(1, itemsSold.sumOf { it.second }),
                lineItemsJson = jsonArray.toString(),
                source = source,
                imageUrl = imageUrl
            )
        )
    }

    suspend fun getDiscrepancies(): List<DiscrepancyReport> = withContext(Dispatchers.IO) {
        val products = productDao.getAllProductsList()
        val reports = mutableListOf<DiscrepancyReport>()

        for (product in products) {
            // If stock confidence is estimated or warehouse/floor has negative drift
            val lastPick = pickDao.getLastPickForProduct(product.id)
            if (product.stockConfidence == "estimated" || product.floorStockLoose < 0 || product.floorStockCases < 0) {
                val missingUnits = max(2, (product.caseSizeUnits * 0.25).toInt())
                reports.add(
                    DiscrepancyReport(
                        productId = product.id,
                        productName = product.name,
                        missingUnits = missingUnits,
                        expectedFloorUnits = product.totalFloorBottles + missingUnits,
                        actualFloorUnits = max(0, product.totalFloorBottles),
                        lastPickBy = lastPick?.pickedBy ?: "Helper",
                        lastPickTimestamp = lastPick?.createdAt ?: (System.currentTimeMillis() - 86400000)
                    )
                )
            }
        }
        reports
    }

    suspend fun generateDraftOrders(): Map<String, List<OrderItem>> = withContext(Dispatchers.IO) {
        val products = productDao.getAllProductsList()
        val resultMap = mutableMapOf<String, MutableList<OrderItem>>()

        for (product in products) {
            val totalCases = product.warehouseStockCases + product.floorStockCases
            val threshold = product.reorderLevelCases
            if (totalCases <= threshold) {
                val suggested = max(1, (threshold * 2) - totalCases)
                val item = OrderItem(
                    productId = product.id,
                    productName = product.name,
                    supplier = product.supplier,
                    suggestedCases = suggested,
                    selectedCases = suggested,
                    estimatedCasePrice = product.price * product.caseSizeUnits * 0.82, // wholesale estimate
                    reason = if (totalCases == 0) "Critical Out of Stock!" else "Stock near reorder level (${totalCases} left)"
                )
                val list = resultMap.getOrPut(product.supplier) { mutableListOf() }
                list.add(item)
            }
        }
        resultMap
    }

    suspend fun saveOrder(supplier: String, items: List<OrderItem>): Long = withContext(Dispatchers.IO) {
        val jsonArray = JSONArray()
        var total = 0.0
        for (item in items) {
            val obj = JSONObject()
            obj.put("name", item.productName)
            obj.put("cases", item.selectedCases)
            obj.put("casePrice", item.estimatedCasePrice)
            jsonArray.put(obj)
            total += (item.selectedCases * item.estimatedCasePrice)
        }

        orderDao.insertOrder(
            OrderEntity(
                supplier = supplier,
                itemsJson = jsonArray.toString(),
                totalEstimate = total,
                status = "sent"
            )
        )
    }

    suspend fun saveAiMessage(role: String, content: String) = withContext(Dispatchers.IO) {
        aiMessageDao.insertMessage(AiMessageEntity(role = role, content = content))
    }

    suspend fun clearAiMessages() = withContext(Dispatchers.IO) {
        aiMessageDao.clearAll()
    }

    suspend fun updateSettings(settings: TavernSettingsEntity) = withContext(Dispatchers.IO) {
        settingsDao.insertOrUpdate(settings)
    }

    suspend fun bulkBumpBeerPrice(delta: Double) = withContext(Dispatchers.IO) {
        productDao.bulkUpdateBeerPrice(delta)
    }

    suspend fun generateCsvExport(): String = withContext(Dispatchers.IO) {
        val products = productDao.getAllProductsList()
        val sb = StringBuilder()
        sb.append("ID,Product Name,Category,Price (Rand),Warehouse Cases,Floor Cases,Loose,Supplier\n")
        for (p in products) {
            sb.append("${p.id},\"${p.name}\",\"${p.category}\",${p.price},${p.warehouseStockCases},${p.floorStockCases},${p.floorStockLoose},\"${p.supplier}\"\n")
        }
        sb.toString()
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        return sdf.format(Date())
    }
}
