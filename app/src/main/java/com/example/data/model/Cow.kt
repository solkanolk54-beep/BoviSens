package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CowDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "tag_number") val tagNumber: String,
    @Json(name = "name") val name: String? = null,
    @Json(name = "daily_milk") val dailyMilk: Double? = 0.0,
    @Json(name = "health_status") val healthStatus: String? = "طبيعي",
    @Json(name = "breed") val breed: String? = "هولشتاين",
    @Json(name = "barn_group") val barnGroup: String? = "الحظيرة A",
    @Json(name = "rumination_hours") val ruminationHours: Double? = 8.0,
    @Json(name = "activity_index") val activityIndex: Int? = 100,
    @Json(name = "body_temp") val bodyTemp: Double? = 38.6,
    @Json(name = "lying_time_hours") val lyingTimeHours: Double? = 11.0,
    @Json(name = "reproductive_status") val reproductiveStatus: String? = "عادي",
    @Json(name = "days_in_milk") val daysInMilk: Int? = 90,
    @Json(name = "urgent_alert_title") val urgentAlertTitle: String? = null,
    @Json(name = "urgent_alert_severity") val urgentAlertSeverity: String? = null
)

data class Cow(
    val id: String,
    val tagNumber: String,
    val name: String,
    val dailyMilk: Double,
    val healthStatus: String, // طبيعي, عرج, بدء شبق, حرارة, تحت الملاحظة
    val breed: String,
    val barnGroup: String,
    val ruminationHours: Double, // وقت الاجترار بالساعات (الطبيعي 7.5 - 9 س)
    val activityIndex: Int, // مؤشر الحركة (%100 طبيعي، >170% شبق، <60% عرج/مرض)
    val bodyTemp: Double, // درجة حرارة الجسم °C (38.5 - 39.2 طبيعي)
    val lyingTimeHours: Double, // ساعات الرقود
    val reproductiveStatus: String, // شبق، تلقيح، حمل، جفاف
    val daysInMilk: Int, // أيام الحلب
    val urgentAlertTitle: String? = null,
    val urgentAlertSeverity: String? = null, // "critical", "warning", "info", null
    val lastUpdated: Long = System.currentTimeMillis(),
    val notes: String = ""
)

enum class HealthCategory(val arabicLabel: String, val englishLabel: String) {
    ALL("الكل", "All"),
    NORMAL("طبيعي", "Normal"),
    LAMENESS("عرج", "Lameness"),
    ESTRUS("بدء شبق", "Heat / Estrus"),
    FEVER("حرارة / التهاب", "Fever"),
    OBSERVATION("تحت الملاحظة", "Observation")
}

data class HerdSummary(
    val totalCows: Int = 0,
    val milkingCows: Int = 0,
    val totalMilkToday: Double = 0.0,
    val avgMilkPerCow: Double = 0.0,
    val estrusAlertsCount: Int = 0,
    val lamenessAlertsCount: Int = 0,
    val feverAlertsCount: Int = 0,
    val healthyPercentage: Int = 100
)
