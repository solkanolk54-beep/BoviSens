package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.Cow
import com.example.data.model.HealthCategory
import com.example.data.model.HerdSummary
import com.example.data.repository.HerdRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class BoviTab {
    DASHBOARD,
    HERD_LIST,
    URGENT_ALERTS,
    SETTINGS
}

data class HerdUiState(
    val cows: List<Cow> = emptyList(),
    val filteredCows: List<Cow> = emptyList(),
    val alertCows: List<Cow> = emptyList(),
    val selectedFilter: HealthCategory = HealthCategory.ALL,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val statusMessage: String? = null,
    val selectedCow: Cow? = null,
    val activeTab: BoviTab = BoviTab.DASHBOARD,
    val apiBaseUrl: String = "http://10.0.2.2:3000/",
    val summary: HerdSummary = HerdSummary()
)

class HerdViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = HerdRepository(database.cowDao())

    private val _uiState = MutableStateFlow(HerdUiState())
    val uiState: StateFlow<HerdUiState> = _uiState.asStateFlow()

    init {
        // Ensure default records are created if first run
        viewModelScope.launch {
            if (database.cowDao().getCount() == 0) {
                AppDatabase.populateInitialHerd(database.cowDao())
            }
        }

        // Combine database flows with search and filter state
        viewModelScope.launch {
            repository.observeAllCows().collect { cowList ->
                updateCowsList(cowList)
            }
        }
    }

    private fun updateCowsList(cowList: List<Cow>) {
        val current = _uiState.value
        val filtered = filterCows(cowList, current.searchQuery, current.selectedFilter)
        val alerts = cowList.filter { it.urgentAlertTitle != null }

        val totalCows = cowList.size
        val milkingCows = cowList.count { it.dailyMilk > 0 }
        val totalMilk = cowList.sumOf { it.dailyMilk }
        val avgMilk = if (milkingCows > 0) totalMilk / milkingCows else 0.0
        val estrusCount = cowList.count { it.healthStatus.contains("شبق") }
        val lamenessCount = cowList.count { it.healthStatus.contains("عرج") }
        val feverCount = cowList.count { it.healthStatus.contains("حرارة") || it.healthStatus.contains("التهاب") }
        val healthyCount = cowList.count { it.healthStatus == "طبيعي" }
        val healthyPercentage = if (totalCows > 0) (healthyCount * 100) / totalCows else 100

        val summary = HerdSummary(
            totalCows = totalCows,
            milkingCows = milkingCows,
            totalMilkToday = (totalMilk * 10).toInt() / 10.0,
            avgMilkPerCow = (avgMilk * 10).toInt() / 10.0,
            estrusAlertsCount = estrusCount,
            lamenessAlertsCount = lamenessCount,
            feverAlertsCount = feverCount,
            healthyPercentage = healthyPercentage
        )

        val updatedSelected = current.selectedCow?.let { sel ->
            cowList.find { it.tagNumber == sel.tagNumber }
        }

        _uiState.value = current.copy(
            cows = cowList,
            filteredCows = filtered,
            alertCows = alerts,
            selectedCow = updatedSelected,
            summary = summary
        )
    }

    private fun filterCows(list: List<Cow>, query: String, filter: HealthCategory): List<Cow> {
        return list.filter { cow ->
            val matchesFilter = when (filter) {
                HealthCategory.ALL -> true
                HealthCategory.NORMAL -> cow.healthStatus == "طبيعي"
                HealthCategory.LAMENESS -> cow.healthStatus.contains("عرج")
                HealthCategory.ESTRUS -> cow.healthStatus.contains("شبق")
                HealthCategory.FEVER -> cow.healthStatus.contains("حرارة") || cow.healthStatus.contains("التهاب")
                HealthCategory.OBSERVATION -> cow.healthStatus.contains("ملاحظة") || cow.healthStatus != "طبيعي"
            }
            val matchesQuery = query.isBlank() ||
                    cow.tagNumber.contains(query, ignoreCase = true) ||
                    cow.name.contains(query, ignoreCase = true) ||
                    cow.breed.contains(query, ignoreCase = true) ||
                    cow.barnGroup.contains(query, ignoreCase = true)

            matchesFilter && matchesQuery
        }
    }

    fun setFilter(filter: HealthCategory) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter,
            filteredCows = filterCows(_uiState.value.cows, _uiState.value.searchQuery, filter)
        )
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredCows = filterCows(_uiState.value.cows, query, _uiState.value.selectedFilter)
        )
    }

    fun selectTab(tab: BoviTab) {
        _uiState.value = _uiState.value.copy(activeTab = tab)
    }

    fun selectCow(cow: Cow?) {
        _uiState.value = _uiState.value.copy(selectedCow = cow)
    }

    fun dismissMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null)
    }

    fun setApiBaseUrl(url: String) {
        repository.setApiBaseUrl(url)
        _uiState.value = _uiState.value.copy(apiBaseUrl = url)
    }

    fun fetchHerdData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isRefreshing = true, statusMessage = "جارٍ الاتصال بمستشعرات BoviSens والخادم...")
            val result = repository.refreshFromApi()
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    statusMessage = "تم تحديث بيانات القطيع بنجاح (${result.getOrNull()} بقرة متصلة)"
                )
            } else {
                // If local server is not active or unreachable, do live simulated telemetry update
                simulateTelemetryStream()
                _uiState.value = _uiState.value.copy(
                    isRefreshing = false,
                    statusMessage = "ملاحظة: تعذر الوصول إلى الخادم الخارجي (${result.exceptionOrNull()?.localizedMessage ?: "timeout"}). تم تفعيل البث الذاتي والبيانات المخزنة محلياً 24/24."
                )
            }
        }
    }

    fun resolveAlert(tagNumber: String, resolutionNote: String) {
        viewModelScope.launch {
            repository.resolveAlert(tagNumber, resolutionNote)
            _uiState.value = _uiState.value.copy(
                statusMessage = "تم تسجيل الإجراء للبقرة $tagNumber وإغلاق التنبيه العاجل"
            )
        }
    }

    fun addNewCow(
        tagNumber: String,
        name: String,
        breed: String,
        barnGroup: String,
        dailyMilk: Double,
        healthStatus: String,
        notes: String
    ) {
        viewModelScope.launch {
            val cow = Cow(
                id = java.util.UUID.randomUUID().toString(),
                tagNumber = tagNumber.trim().uppercase(),
                name = name.trim().ifEmpty { "بقرة $tagNumber" },
                dailyMilk = dailyMilk,
                healthStatus = healthStatus,
                breed = breed.ifEmpty { "هولشتاين" },
                barnGroup = barnGroup.ifEmpty { "الحظيرة العامة" },
                ruminationHours = if (healthStatus.contains("عرج") || healthStatus.contains("حرارة")) 5.2 else 8.2,
                activityIndex = if (healthStatus.contains("شبق")) 210 else 100,
                bodyTemp = if (healthStatus.contains("حرارة")) 39.9 else 38.6,
                lyingTimeHours = 11.0,
                reproductiveStatus = if (healthStatus.contains("شبق")) "بدء شبق (جاهزة للتلقيح)" else "طبيعي",
                daysInMilk = 90,
                urgentAlertTitle = if (healthStatus.contains("عرج")) "اشتباه عرج في أحد القوائم"
                else if (healthStatus.contains("شبق")) "تنبيه بدء شبق جاهزة للتلقيح"
                else if (healthStatus.contains("حرارة")) "ارتفاع حرارة مفاجئ" else null,
                urgentAlertSeverity = if (healthStatus == "طبيعي") null else "warning",
                notes = notes
            )
            repository.insertCow(cow)
            _uiState.value = _uiState.value.copy(
                statusMessage = "تمت إضافة البقرة رقم $tagNumber إلى نظام المراقبة 24/24"
            )
        }
    }

    fun deleteCow(tagNumber: String) {
        viewModelScope.launch {
            repository.deleteCow(tagNumber)
            _uiState.value = _uiState.value.copy(
                selectedCow = null,
                statusMessage = "تم حذف البقرة $tagNumber من السجل"
            )
        }
    }

    fun simulateTelemetryStream() {
        viewModelScope.launch {
            val list = _uiState.value.cows
            list.forEach { cow ->
                // Slight realistic variations
                val tempDelta = (Random.nextDouble(-0.1, 0.1) * 10).toInt() / 10.0
                val milkDelta = (Random.nextDouble(-0.3, 0.3) * 10).toInt() / 10.0
                val rumDelta = (Random.nextDouble(-0.2, 0.2) * 10).toInt() / 10.0
                val newTemp = ((cow.bodyTemp + tempDelta) * 10).toInt() / 10.0
                val newMilk = kotlin.math.max(0.0, ((cow.dailyMilk + milkDelta) * 10).toInt() / 10.0)
                val newRum = kotlin.math.max(2.0, ((cow.ruminationHours + rumDelta) * 10).toInt() / 10.0)
                repository.updateCow(
                    cow.copy(
                        bodyTemp = newTemp,
                        dailyMilk = newMilk,
                        ruminationHours = newRum,
                        lastUpdated = System.currentTimeMillis()
                    )
                )
            }
        }
    }
}
