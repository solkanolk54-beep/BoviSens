package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.Cow

@Entity(tableName = "cows")
data class CowEntity(
    @PrimaryKey
    val tagNumber: String,
    val id: String,
    val name: String,
    val dailyMilk: Double,
    val healthStatus: String,
    val breed: String,
    val barnGroup: String,
    val ruminationHours: Double,
    val activityIndex: Int,
    val bodyTemp: Double,
    val lyingTimeHours: Double,
    val reproductiveStatus: String,
    val daysInMilk: Int,
    val urgentAlertTitle: String?,
    val urgentAlertSeverity: String?,
    val lastUpdated: Long,
    val notes: String
) {
    fun toDomain(): Cow {
        return Cow(
            id = id,
            tagNumber = tagNumber,
            name = name,
            dailyMilk = dailyMilk,
            healthStatus = healthStatus,
            breed = breed,
            barnGroup = barnGroup,
            ruminationHours = ruminationHours,
            activityIndex = activityIndex,
            bodyTemp = bodyTemp,
            lyingTimeHours = lyingTimeHours,
            reproductiveStatus = reproductiveStatus,
            daysInMilk = daysInMilk,
            urgentAlertTitle = urgentAlertTitle,
            urgentAlertSeverity = urgentAlertSeverity,
            lastUpdated = lastUpdated,
            notes = notes
        )
    }

    companion object {
        fun fromDomain(cow: Cow): CowEntity {
            return CowEntity(
                tagNumber = cow.tagNumber,
                id = cow.id,
                name = cow.name,
                dailyMilk = cow.dailyMilk,
                healthStatus = cow.healthStatus,
                breed = cow.breed,
                barnGroup = cow.barnGroup,
                ruminationHours = cow.ruminationHours,
                activityIndex = cow.activityIndex,
                bodyTemp = cow.bodyTemp,
                lyingTimeHours = cow.lyingTimeHours,
                reproductiveStatus = cow.reproductiveStatus,
                daysInMilk = cow.daysInMilk,
                urgentAlertTitle = cow.urgentAlertTitle,
                urgentAlertSeverity = cow.urgentAlertSeverity,
                lastUpdated = cow.lastUpdated,
                notes = cow.notes
            )
        }
    }
}
