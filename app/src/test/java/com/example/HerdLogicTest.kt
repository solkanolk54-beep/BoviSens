package com.example

import com.example.data.model.Cow
import com.example.data.model.HealthCategory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HerdLogicTest {

    private val sampleCows = listOf(
        Cow(
            id = "1",
            tagNumber = "DZ-104",
            name = "نجمة",
            dailyMilk = 18.2,
            healthStatus = "عرج",
            breed = "هولشتاين",
            barnGroup = "الحظيرة A",
            ruminationHours = 5.3,
            activityIndex = 48,
            bodyTemp = 38.9,
            lyingTimeHours = 14.8,
            reproductiveStatus = "ملقحة",
            daysInMilk = 140,
            urgentAlertTitle = "تنبيه عرج حاد",
            urgentAlertSeverity = "critical"
        ),
        Cow(
            id = "2",
            tagNumber = "DZ-108",
            name = "وردة",
            dailyMilk = 31.5,
            healthStatus = "بدء شبق",
            breed = "مونتبليارد",
            barnGroup = "الحظيرة B",
            ruminationHours = 6.8,
            activityIndex = 230,
            bodyTemp = 39.2,
            lyingTimeHours = 7.5,
            reproductiveStatus = "بدء الشبق",
            daysInMilk = 78,
            urgentAlertTitle = "تنبيه بدء الشبق",
            urgentAlertSeverity = "warning"
        ),
        Cow(
            id = "3",
            tagNumber = "DZ-115",
            name = "جميلة",
            dailyMilk = 34.8,
            healthStatus = "طبيعي",
            breed = "هولشتاين",
            barnGroup = "الحظيرة A",
            ruminationHours = 8.6,
            activityIndex = 104,
            bodyTemp = 38.6,
            lyingTimeHours = 11.2,
            reproductiveStatus = "حامل",
            daysInMilk = 95
        )
    )

    @Test
    fun testEstrusAndLamenessDetection() {
        val lamenessCows = sampleCows.filter { it.healthStatus.contains("عرج") }
        val estrusCows = sampleCows.filter { it.healthStatus.contains("شبق") }

        assertEquals(1, lamenessCows.size)
        assertEquals("DZ-104", lamenessCows.first().tagNumber)

        assertEquals(1, estrusCows.size)
        assertEquals("DZ-108", estrusCows.first().tagNumber)
        assertTrue(estrusCows.first().activityIndex > 180)
    }

    @Test
    fun testMilkTotalCalculation() {
        val totalMilk = sampleCows.sumOf { it.dailyMilk }
        assertEquals(84.5, totalMilk, 0.01)
    }

    @Test
    fun testUrgentAlertsFiltering() {
        val alertCows = sampleCows.filter { it.urgentAlertTitle != null }
        assertEquals(2, alertCows.size)
    }
}
