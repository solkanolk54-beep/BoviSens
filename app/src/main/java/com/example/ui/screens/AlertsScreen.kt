package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Cow
import com.example.ui.theme.AlertCriticalRed
import com.example.ui.theme.AlertEstrusPurple
import com.example.ui.theme.AlertLamenessOrange
import com.example.ui.theme.BoviGreenPrimary
import com.example.ui.theme.StatusNormalGreen

@Composable
fun AlertsScreen(
    alertCows: List<Cow>,
    onSelectCow: (Cow) -> Unit,
    onResolveAlert: (tag: String, note: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf("الكل") }
    val filterTabs = listOf("الكل", "حالات العرج", "بدء الشبق", "الحرارة والالتهابات")

    val filteredList = alertCows.filter { cow ->
        when (selectedFilter) {
            "حالات العرج" -> cow.healthStatus.contains("عرج")
            "بدء الشبق" -> cow.healthStatus.contains("شبق")
            "الحرارة والالتهابات" -> cow.healthStatus.contains("حرارة") || cow.healthStatus.contains("التهاب")
            else -> true
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("alerts_screen")
    ) {
        // Filter tabs row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterTabs) { tab ->
                val isSelected = selectedFilter == tab
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = tab },
                    label = { Text(tab) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = BoviGreenPrimary.copy(alpha = 0.15f),
                        selectedLabelColor = BoviGreenPrimary
                    )
                )
            }
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(StatusNormalGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusNormalGreen,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "لا توجد تنبيهات عاجلة معلقة",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "جميع الأبقار في هذه الفئة بحالة طبيعية ومستقرة، ونظام BoviSens يراقب المستشعرات 24/24.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(filteredList, key = { it.tagNumber }) { cow ->
                    AlertItemCard(
                        cow = cow,
                        onClick = { onSelectCow(cow) },
                        onResolve = { note -> onResolveAlert(cow.tagNumber, note) }
                    )
                }
            }
        }
    }
}

@Composable
fun AlertItemCard(
    cow: Cow,
    onClick: () -> Unit,
    onResolve: (note: String) -> Unit
) {
    val isLameness = cow.healthStatus.contains("عرج")
    val isEstrus = cow.healthStatus.contains("شبق")
    val isFever = cow.healthStatus.contains("حرارة")

    val accentColor = when {
        isLameness -> AlertLamenessOrange
        isEstrus -> AlertEstrusPurple
        isFever -> AlertCriticalRed
        else -> AlertLamenessOrange
    }

    val alertTitlePrefix = when {
        isLameness -> "تنبيه عرج حركي (Lameness Alert)"
        isEstrus -> "تنبيه بدء شبق وتلقيح (Heat / Estrus Alert)"
        isFever -> "تنبيه حمى واشتباه التهاب (Fever Alert)"
        else -> "تنبيه صحي عاجل"
    }

    val recommendation = when {
        isLameness -> "الإجراء الموصى به: فحص الحافر الخلفي، حمام كبريتات النحاس 5%، وتوفير فرشة تبن جافة لمنع تفاقم التقرح."
        isEstrus -> "الإجراء الموصى به: تجهيز جرعة التلقيح الاصطناعي (AI) وتنفيذ التلقيح خلال نافذة الـ 12 ساعة القادمة لتحقيق أعلى نسبة إخصاب."
        isFever -> "الإجراء الموصى به: عزل البقرة فوراً، فحص حلمات الضرع بجهاز CMT للتأكد من التهاب الضرع، وقياس الحرارة الدورية."
        else -> "متابعة بيطرية مستمرة."
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("alert_card_${cow.tagNumber}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isEstrus) Icons.Default.Favorite else Icons.Default.Warning,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = alertTitlePrefix,
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                            color = accentColor
                        )
                        Text(
                            text = "البقرة #${cow.tagNumber} (${cow.name}) • ${cow.barnGroup}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "عاجل 24/24",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = accentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Alert details description
            Text(
                text = cow.urgentAlertTitle ?: "تغير مفاجئ في نمط سلوك البقرة تم رصده بواسطة طوق BoviSens",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Telemetry indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DirectionsRun, contentDescription = null, tint = accentColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "النشاط: ${cow.activityIndex}%", style = MaterialTheme.typography.labelMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Pets, contentDescription = null, tint = BoviGreenPrimary, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "الاجترار: ${cow.ruminationHours} س", style = MaterialTheme.typography.labelMedium)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Thermostat, contentDescription = null, tint = if (cow.bodyTemp > 39.4) AlertCriticalRed else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "الحرارة: ${cow.bodyTemp}°C", style = MaterialTheme.typography.labelMedium)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Veterinary Recommendation
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = BoviGreenPrimary.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, BoviGreenPrimary.copy(alpha = 0.2f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Healing,
                        contentDescription = null,
                        tint = BoviGreenPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = recommendation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("عرض السجل الكامل", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Button(
                    onClick = {
                        val note = if (isEstrus) "تم إجراء التلقيح الاصطناعي بنجاح وتسجيل موعد الحمل المتوقع"
                        else if (isLameness) "تم فحص الحافر وتطبيق ضماد كبريتات النحاس والعزل"
                        else "تم إعطاء مضاد التهاب وخافض حرارة والملاحظة"
                        onResolve(note)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    modifier = Modifier.weight(1.3f)
                ) {
                    Icon(
                        imageVector = if (isEstrus) Icons.Default.Favorite else Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isEstrus) "تسجيل التلقيح" else "تسجيل الإجراء")
                }
            }
        }
    }
}
