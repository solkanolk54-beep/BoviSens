package com.example.data.repository

import com.example.data.local.CowDao
import com.example.data.local.CowEntity
import com.example.data.model.Cow
import com.example.data.remote.BoviSensApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.UUID

class HerdRepository(
    private val cowDao: CowDao
) {
    private var currentApiBaseUrl: String = BoviSensApiService.DEFAULT_BASE_URL
    private var apiService: BoviSensApiService = BoviSensApiService.create(currentApiBaseUrl)

    fun setApiBaseUrl(newUrl: String) {
        val trimmed = newUrl.trim()
        if (trimmed.isNotEmpty() && trimmed != currentApiBaseUrl) {
            currentApiBaseUrl = trimmed
            apiService = BoviSensApiService.create(currentApiBaseUrl)
        }
    }

    fun getApiBaseUrl(): String = currentApiBaseUrl

    fun observeAllCows(): Flow<List<Cow>> {
        return cowDao.getAllCows().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun observeAlertCows(): Flow<List<Cow>> {
        return cowDao.getAlertCows().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getCow(tagNumber: String): Cow? = withContext(Dispatchers.IO) {
        cowDao.getCowByTag(tagNumber)?.toDomain()
    }

    suspend fun insertCow(cow: Cow) = withContext(Dispatchers.IO) {
        cowDao.insertCow(CowEntity.fromDomain(cow))
    }

    suspend fun updateCow(cow: Cow) = withContext(Dispatchers.IO) {
        cowDao.updateCow(CowEntity.fromDomain(cow))
    }

    suspend fun deleteCow(tagNumber: String) = withContext(Dispatchers.IO) {
        cowDao.deleteByTag(tagNumber)
    }

    suspend fun resolveAlert(tagNumber: String, resolutionNote: String) = withContext(Dispatchers.IO) {
        val existing = cowDao.getCowByTag(tagNumber)
        if (existing != null) {
            val updated = existing.copy(
                urgentAlertTitle = null,
                urgentAlertSeverity = null,
                healthStatus = "تحت الملاحظة",
                notes = if (resolutionNote.isNotBlank()) "${existing.notes}\n[تم الفحص/الإجراء]: $resolutionNote" else existing.notes,
                lastUpdated = System.currentTimeMillis()
            )
            cowDao.updateCow(updated)
        }
    }

    suspend fun refreshFromApi(): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getHerdData()
            if (response.isSuccessful && response.body() != null) {
                val dtos = response.body()!!
                val entities = dtos.map { dto ->
                    CowEntity(
                        tagNumber = dto.tagNumber,
                        id = dto.id ?: UUID.randomUUID().toString(),
                        name = dto.name ?: "بقرة ${dto.tagNumber}",
                        dailyMilk = dto.dailyMilk ?: 0.0,
                        healthStatus = dto.healthStatus ?: "طبيعي",
                        breed = dto.breed ?: "هولشتاين",
                        barnGroup = dto.barnGroup ?: "الحظيرة العامة",
                        ruminationHours = dto.ruminationHours ?: 8.0,
                        activityIndex = dto.activityIndex ?: 100,
                        bodyTemp = dto.bodyTemp ?: 38.6,
                        lyingTimeHours = dto.lyingTimeHours ?: 11.0,
                        reproductiveStatus = dto.reproductiveStatus ?: "عادي",
                        daysInMilk = dto.daysInMilk ?: 100,
                        urgentAlertTitle = dto.urgentAlertTitle,
                        urgentAlertSeverity = dto.urgentAlertSeverity,
                        lastUpdated = System.currentTimeMillis(),
                        notes = ""
                    )
                }
                cowDao.insertAll(entities)
                Result.success(entities.size)
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun simulateLiveTelemetryUpdate() = withContext(Dispatchers.IO) {
        val existing = cowDao.getAllCows()
        // Make sure DB is populated if it was empty
        if (cowDao.getCount() == 0) {
            com.example.data.local.AppDatabase.populateInitialHerd(cowDao)
        }
    }
}
