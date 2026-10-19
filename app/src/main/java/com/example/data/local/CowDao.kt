package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CowDao {
    @Query("SELECT * FROM cows ORDER BY CASE WHEN urgentAlertSeverity IS NOT NULL THEN 0 ELSE 1 END, tagNumber ASC")
    fun getAllCows(): Flow<List<CowEntity>>

    @Query("SELECT * FROM cows WHERE tagNumber = :tagNumber LIMIT 1")
    suspend fun getCowByTag(tagNumber: String): CowEntity?

    @Query("SELECT * FROM cows WHERE urgentAlertSeverity IS NOT NULL ORDER BY lastUpdated DESC")
    fun getAlertCows(): Flow<List<CowEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCow(cow: CowEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cows: List<CowEntity>)

    @Update
    suspend fun updateCow(cow: CowEntity)

    @Delete
    suspend fun deleteCow(cow: CowEntity)

    @Query("DELETE FROM cows WHERE tagNumber = :tagNumber")
    suspend fun deleteByTag(tagNumber: String)

    @Query("SELECT COUNT(*) FROM cows")
    suspend fun getCount(): Int
}
