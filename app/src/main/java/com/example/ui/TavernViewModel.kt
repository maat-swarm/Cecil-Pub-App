package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.TavernAiOperationsService
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
import com.example.data.repository.TavernRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TavernViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    val repository = TavernRepository(database)
    private val aiService = TavernAiOperationsService()

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sales: StateFlow<List<DailySaleEntity>> = repository.allSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val deliveries: StateFlow<List<DeliveryEntity>> = repository.allDeliveries
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val picks: StateFlow<List<PickEntity>> = repository.allPicks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentPicks: StateFlow<List<PickEntity>> = repository.recentPicks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val aiMessages: StateFlow<List<AiMessageEntity>> = repository.allAiMessages
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<TavernSettingsEntity?> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _discrepancies = MutableStateFlow<List<DiscrepancyReport>>(emptyList())
    val discrepancies: StateFlow<List<DiscrepancyReport>> = _discrepancies.asStateFlow()

    private val _draftOrders = MutableStateFlow<Map<String, List<OrderItem>>>(emptyMap())
    val draftOrders: StateFlow<Map<String, List<OrderItem>>> = _draftOrders.asStateFlow()

    private val _burstMessage = MutableStateFlow<String?>(null)
    val burstMessage: StateFlow<String?> = _burstMessage.asStateFlow()

    private val _isAiTyping = MutableStateFlow(false)
    val isAiTyping: StateFlow<Boolean> = _isAiTyping.asStateFlow()

    private val _isHelperMode = MutableStateFlow(false)
    val isHelperMode: StateFlow<Boolean> = _isHelperMode.asStateFlow()

    val todayTill: StateFlow<Double> = sales.combine(products) { salesList, _ ->
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        salesList.filter { it.date == todayStr }.sumOf { it.totalRand }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val todaySalesCount: StateFlow<Int> = sales.combine(products) { salesList, _ ->
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        salesList.filter { it.date == todayStr }.sumOf { it.salesCount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        viewModelScope.launch {
            repository.initDatabaseDefaults()
            refreshDiscrepanciesAndDrafts()

            // Initial AI welcome if empty
            val initialMsgs = repository.allAiMessages
            // Check if messages empty
        }
    }

    fun refreshDiscrepanciesAndDrafts() {
        viewModelScope.launch {
            _discrepancies.value = repository.getDiscrepancies()
            _draftOrders.value = repository.generateDraftOrders()
        }
    }

    fun logDelivery(productId: Long, cases: Int, supplier: String) {
        viewModelScope.launch {
            repository.logDelivery(productId, cases, supplier)
            _burstMessage.value = "Delivered ✓"
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun logPick(productId: Long, cases: Int, isHelper: Boolean = false) {
        viewModelScope.launch {
            val user = if (isHelper || _isHelperMode.value) "Helper" else "Cecil"
            repository.logPick(productId, cases, pickedBy = user)
            _burstMessage.value = "Picked ✓"
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun logQuickSale(amount: Double, paymentMethod: String) {
        viewModelScope.launch {
            repository.logQuickSale(amount, paymentMethod)
            _burstMessage.value = "Sale Logged ✓"
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun logEodSale(
        totalRand: Double,
        cash: Double,
        card: Double,
        eft: Double,
        itemsSold: List<Pair<Long, Int>>,
        source: String = "pos_photo",
        imageUrl: String? = null
    ) {
        viewModelScope.launch {
            repository.logEodSale(totalRand, cash, card, eft, itemsSold, source, imageUrl)
            _burstMessage.value = "POS Day Closed ✓"
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun sendAiMessage(messageText: String) {
        if (messageText.isBlank()) return
        viewModelScope.launch {
            repository.saveAiMessage("user", messageText)
            _isAiTyping.value = true

            val answer = aiService.answerQuestion(
                userMessage = messageText,
                products = products.value,
                sales = sales.value,
                discrepancies = _discrepancies.value,
                draftOrders = _draftOrders.value,
                todayTill = todayTill.value
            )

            _isAiTyping.value = false
            repository.saveAiMessage("assistant", answer)
        }
    }

    fun clearAiMessages() {
        viewModelScope.launch {
            repository.clearAiMessages()
        }
    }

    fun saveDraftOrder(supplier: String, items: List<OrderItem>) {
        viewModelScope.launch {
            repository.saveOrder(supplier, items)
            _burstMessage.value = "Order Saved ✓"
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun updateDraftQuantity(supplier: String, productId: Long, delta: Int) {
        val currentDrafts = _draftOrders.value.toMutableMap()
        val supplierItems = currentDrafts[supplier]?.toMutableList() ?: return
        val index = supplierItems.indexOfFirst { it.productId == productId }
        if (index != -1) {
            val item = supplierItems[index]
            val newQty = (item.selectedCases + delta).coerceAtLeast(0)
            supplierItems[index] = item.copy(selectedCases = newQty)
            currentDrafts[supplier] = supplierItems
            _draftOrders.value = currentDrafts
        }
    }

    fun bulkBumpBeerPrice(delta: Double) {
        viewModelScope.launch {
            repository.bulkBumpBeerPrice(delta)
            _burstMessage.value = "Prices Updated ✓"
        }
    }

    fun addProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.addProduct(product)
            _burstMessage.value = "Product Added ✓"
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun updateProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.updateProduct(product)
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun deleteProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun loadStarterPack() {
        viewModelScope.launch {
            repository.loadTembisaStarterPack()
            _burstMessage.value = "Loaded Beers ✓"
            refreshDiscrepanciesAndDrafts()
        }
    }

    fun updateSettings(newSettings: TavernSettingsEntity) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            _burstMessage.value = "Settings Saved ✓"
        }
    }

    fun toggleHelperMode(enteredPin: String): Boolean {
        val currentPin = settings.value?.helperPin ?: "1234"
        if (enteredPin == currentPin) {
            _isHelperMode.value = !_isHelperMode.value
            return true
        }
        return false
    }

    fun exitHelperMode() {
        _isHelperMode.value = false
    }

    fun dismissBurst() {
        _burstMessage.value = null
    }
}
