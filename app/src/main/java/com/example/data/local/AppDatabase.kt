package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [CowEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cowDao(): CowDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bovisens_herd_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialHerd(database.cowDao())
                    }
                }
            }
        }

        suspend fun populateInitialHerd(dao: CowDao) {
            val initialCows = listOf(
                CowEntity(
                    tagNumber = "DZ-104",
                    id = "c1",
                    name = "نجمة (Najma)",
                    dailyMilk = 18.2,
                    healthStatus = "عرج",
                    breed = "هولشتاين فريزيان",
                    barnGroup = "الحظيرة A - غربية",
                    ruminationHours = 5.3,
                    activityIndex = 48,
                    bodyTemp = 38.9,
                    lyingTimeHours = 14.8,
                    reproductiveStatus = "ملقحة (فحص سلبي)",
                    daysInMilk = 142,
                    urgentAlertTitle = "تنبيه عرج حاد: انخفاض الحركة بنسبة 52% مع قصر خطوة القائمة الخلفية اليسرى",
                    urgentAlertSeverity = "critical",
                    lastUpdated = System.currentTimeMillis() - 25 * 60 * 1000,
                    notes = "تحتاج لفحص حوافر عاجل وتضماد وقائي مع خافض التهاب غير ستيرويدي."
                ),
                CowEntity(
                    tagNumber = "DZ-108",
                    id = "c2",
                    name = "وردة (Warda)",
                    dailyMilk = 31.5,
                    healthStatus = "بدء شبق",
                    breed = "مونتبليارد",
                    barnGroup = "الحظيرة B - الأبقار الحلوب",
                    ruminationHours = 6.8,
                    activityIndex = 230,
                    bodyTemp = 39.2,
                    lyingTimeHours = 7.5,
                    reproductiveStatus = "بدء الشبق (جاهزة للتلقيح)",
                    daysInMilk = 78,
                    urgentAlertTitle = "تنبيه بدء الشبق: زيادة الحركة 230% - نافذة التلقيح المثالية خلال 10 إلى 14 ساعة",
                    urgentAlertSeverity = "warning",
                    lastUpdated = System.currentTimeMillis() - 10 * 60 * 1000,
                    notes = "مراقبة علامات الوقوف للركوب وتجهيز قشة التلقيح الاصطناعي المساء اليوم."
                ),
                CowEntity(
                    tagNumber = "DZ-115",
                    id = "c3",
                    name = "جميلة (Jamila)",
                    dailyMilk = 34.8,
                    healthStatus = "طبيعي",
                    breed = "هولشتاين أصلية",
                    barnGroup = "الحظيرة A - إنتاج ممتاز",
                    ruminationHours = 8.6,
                    activityIndex = 104,
                    bodyTemp = 38.6,
                    lyingTimeHours = 11.2,
                    reproductiveStatus = "حامل (الشهر 4)",
                    daysInMilk = 95,
                    urgentAlertTitle = null,
                    urgentAlertSeverity = null,
                    lastUpdated = System.currentTimeMillis() - 40 * 60 * 1000,
                    notes = "أداء ممتاز وإنتاجية حليب ثابتة واجترار مثالي."
                ),
                CowEntity(
                    tagNumber = "DZ-203",
                    id = "c4",
                    name = "أميرة (Amira)",
                    dailyMilk = 13.4,
                    healthStatus = "حرارة",
                    breed = "سويسرية بنية",
                    barnGroup = "حظيرة العزل الصحي",
                    ruminationHours = 4.2,
                    activityIndex = 52,
                    bodyTemp = 40.1,
                    lyingTimeHours = 15.6,
                    reproductiveStatus = "فترة جفاف متأخرة",
                    daysInMilk = 210,
                    urgentAlertTitle = "ارتفاع حرارة مفاجئ (40.1°C) مع هبوط حاد في وقت الاجترار (4.2 س)",
                    urgentAlertSeverity = "critical",
                    lastUpdated = System.currentTimeMillis() - 15 * 60 * 1000,
                    notes = "اشتباه بالتهاب الضرع السريري أو خمج معوي. تم استدعاء الطبيب البيطري."
                ),
                CowEntity(
                    tagNumber = "DZ-221",
                    id = "c5",
                    name = "سندس (Sundus)",
                    dailyMilk = 28.0,
                    healthStatus = "بدء شبق",
                    breed = "هولشتاين",
                    barnGroup = "الحظيرة B",
                    ruminationHours = 7.1,
                    activityIndex = 195,
                    bodyTemp = 38.9,
                    lyingTimeHours = 8.2,
                    reproductiveStatus = "شبق ناشط",
                    daysInMilk = 65,
                    urgentAlertTitle = "تنبيه شبق: نشاط حركة مرتفع بنسبة 195% ونشاط شم متبادل",
                    urgentAlertSeverity = "warning",
                    lastUpdated = System.currentTimeMillis() - 45 * 60 * 1000,
                    notes = "البقرة في الدورة الثانية بعد الولادة ومؤهلة للتلقيح."
                ),
                CowEntity(
                    tagNumber = "DZ-310",
                    id = "c6",
                    name = "ياقوتة (Yaqouta)",
                    dailyMilk = 24.2,
                    healthStatus = "عرج",
                    breed = "مونتبليارد",
                    barnGroup = "الحظيرة A",
                    ruminationHours = 6.4,
                    activityIndex = 65,
                    bodyTemp = 38.7,
                    lyingTimeHours = 13.5,
                    reproductiveStatus = "حامل (الشهر 2)",
                    daysInMilk = 160,
                    urgentAlertTitle = "تنبيه عرج خفيف: عدم تماثل في المشية في الممر الآلي للحلب",
                    urgentAlertSeverity = "warning",
                    lastUpdated = System.currentTimeMillis() - 90 * 60 * 1000,
                    notes = "حمام أقدام بمحلول كبريتات النحاس 5% والمتابعة غداً."
                ),
                CowEntity(
                    tagNumber = "DZ-120",
                    id = "c7",
                    name = "ريحانة (Raihana)",
                    dailyMilk = 29.8,
                    healthStatus = "طبيعي",
                    breed = "هولشتاين",
                    barnGroup = "الحظيرة B",
                    ruminationHours = 8.3,
                    activityIndex = 98,
                    bodyTemp = 38.6,
                    lyingTimeHours = 11.5,
                    reproductiveStatus = "حامل (الشهر 6)",
                    daysInMilk = 180,
                    urgentAlertTitle = null,
                    urgentAlertSeverity = null,
                    lastUpdated = System.currentTimeMillis() - 110 * 60 * 1000,
                    notes = "وضع صحي متوازن وشهية منتظمة."
                ),
                CowEntity(
                    tagNumber = "DZ-245",
                    id = "c8",
                    name = "بياض (Bayad)",
                    dailyMilk = 26.5,
                    healthStatus = "طبيعي",
                    breed = "هولشتاين",
                    barnGroup = "الحظيرة B",
                    ruminationHours = 8.0,
                    activityIndex = 102,
                    bodyTemp = 38.5,
                    lyingTimeHours = 10.9,
                    reproductiveStatus = "طبيعي",
                    daysInMilk = 120,
                    urgentAlertTitle = null,
                    urgentAlertSeverity = null,
                    lastUpdated = System.currentTimeMillis() - 150 * 60 * 1000,
                    notes = "مستقرة."
                ),
                CowEntity(
                    tagNumber = "DZ-340",
                    id = "c9",
                    name = "شهباء (Shahbaa)",
                    dailyMilk = 32.1,
                    healthStatus = "طبيعي",
                    breed = "سويسرية بنية",
                    barnGroup = "الحظيرة A",
                    ruminationHours = 8.7,
                    activityIndex = 100,
                    bodyTemp = 38.6,
                    lyingTimeHours = 11.8,
                    reproductiveStatus = "مفحوصة إيجابياً",
                    daysInMilk = 85,
                    urgentAlertTitle = null,
                    urgentAlertSeverity = null,
                    lastUpdated = System.currentTimeMillis() - 180 * 60 * 1000,
                    notes = "معدل اجترار رائع ومستمر."
                )
            )
            dao.insertAll(initialCows)
        }
    }
}
