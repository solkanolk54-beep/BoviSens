package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.Cow
import com.example.data.model.HealthCategory
import com.example.data.model.HerdSummary
import com.example.ui.components.CowItemCard
import com.example.ui.components.MetricCard
import com.example.ui.theme.AlertCriticalRed
import com.example.ui.theme.AlertEstrusPurple
import com.example.ui.theme.AlertLamenessOrange
import com.example.ui.theme.AlertMilkBlue
import com.example.ui.theme.BoviGreenDark
import com.example.ui.theme.BoviGreenPrimary
import com.example.ui.theme.BoviMintAccent
import com.example.ui.theme.StatusNormalGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HerdDashboardScreen(
    cows: List<Cow>,
    summary: HerdSummary,
    selectedFilter: HealthCategory,
    searchQuery: String,
    statusMessage: String?,
    isRefreshing: Boolean,
    onFilterChange: (HealthCategory) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectCow: (Cow) -> Unit,
    onRefresh: () -> Unit,
    onAddCowClick: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenAlertsTab: () -> Unit,
    onDismissMessage: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("herd_dashboard_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Hero Farm Card with Telemetry Header
            item {
                FarmHeroSection(
                    summary = summary,
                    onOpenSettings = onOpenSettings,
                    onRefresh = onRefresh,
                    isRefreshing = isRefreshing
                )
            }

            // Status message toast/banner if present
            if (statusMessage != null) {
                item {
                    StatusBanner(
                        message = statusMessage,
                        onDismiss = onDismissMessage
                    )
                }
            }

            // Quick Stats Row (2x2 Grid)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "إجمالي القطيع",
                            value = "${summary.totalCows}",
                            unit = "رأس",
                            subtitle = "${summary.milkingCows} بقرة حلوب منتجة",
                            icon = Icons.Default.Pets,
                            iconTint = BoviGreenPrimary,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_total_cows"
                        )
                        MetricCard(
                            title = "إنتاج الحليب اليوم",
                            value = "${summary.totalMilkToday}",
                            unit = "لتر",
                            subtitle = "المتوسط: ${summary.avgMilkPerCow} ل/بقرة",
                            icon = Icons.Default.LocalDrink,
                            iconTint = AlertMilkBlue,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_total_milk"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MetricCard(
                            title = "تنبيهات الشبق",
                            value = "${summary.estrusAlertsCount}",
                            unit = "حالة",
                            subtitle = if (summary.estrusAlertsCount > 0) "جاهزة للتلقيح الفوري!" else "لا توجد حالات حالياً",
                            icon = Icons.Default.Favorite,
                            iconTint = AlertEstrusPurple,
                            containerColor = if (summary.estrusAlertsCount > 0) AlertEstrusPurple.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_estrus_alerts"
                        )
                        MetricCard(
                            title = "حالات العرج",
                            value = "${summary.lamenessAlertsCount}",
                            unit = "حالة",
                            subtitle = if (summary.lamenessAlertsCount > 0) "تحتاج فحص الحوافر" else "المشية طبيعية",
                            icon = Icons.Default.Warning,
                            iconTint = AlertLamenessOrange,
                            containerColor = if (summary.lamenessAlertsCount > 0) AlertLamenessOrange.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
                            modifier = Modifier.weight(1f),
                            testTag = "metric_lameness_alerts"
                        )
                    }
                }
            }

            // Urgent Banner Callout if alerts exist
            if (summary.estrusAlertsCount > 0 || summary.lamenessAlertsCount > 0 || summary.feverAlertsCount > 0) {
                item {
                    UrgentAlertCallout(
                        estrusCount = summary.estrusAlertsCount,
                        lamenessCount = summary.lamenessAlertsCount,
                        feverCount = summary.feverAlertsCount,
                        onViewAlerts = onOpenAlertsTab
                    )
                }
            }

            // Search Bar & Filter Section Header
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = onSearchChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("herd_search_input"),
                        placeholder = { Text("بحث برقم الشريحة (Tag) أو الاسم أو السلالة...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "بحث")
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { onSearchChange("") }) {
                                    Icon(imageVector = Icons.Default.Clear, contentDescription = "مسح")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Health Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(HealthCategory.values()) { category ->
                            FilterChip(
                                selected = selectedFilter == category,
                                onClick = { onFilterChange(category) },
                                label = { Text(category.arabicLabel) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BoviGreenPrimary.copy(alpha = 0.15f),
                                    selectedLabelColor = BoviGreenPrimary
                                ),
                                modifier = Modifier.testTag("filter_chip_${category.name}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "قائمة الأبقار المفحوصة (${cows.size})",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "اضغط على البقرة لعرض السجل",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Cow Cards List
            if (cows.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "لا توجد أبقار مطابقة للبحث",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(cows, key = { it.tagNumber }) { cow ->
                    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                        CowItemCard(
                            cow = cow,
                            onClick = { onSelectCow(cow) }
                        )
                    }
                }
            }
        }

        // Floating Action Buttons: Add Cow & Refresh
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FloatingActionButton(
                onClick = onAddCowClick,
                containerColor = BoviGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_cow")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة بقرة جديدة")
            }

            FloatingActionButton(
                onClick = onRefresh,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BoviGreenPrimary,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_refresh")
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "تحديث البيانات")
            }
        }
    }
}

@Composable
private fun FarmHeroSection(
    summary: HerdSummary,
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
        ) {
            // Hero Image
            Image(
                painter = painterResource(id = R.drawable.bovisens_hero_banner_1792448358334),
                contentDescription = "BoviSens Farm Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Gradient scrim for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.35f),
                                Color.Black.copy(alpha = 0.75f)
                            )
                        )
                    )
            )

            // Content inside hero
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row with title and action buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(BoviMintAccent)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BoviSens • متابعة حية 24/24",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }

                    Row {
                        IconButton(
                            onClick = onOpenSettings,
                            modifier = Modifier
                                .size(34.dp)
                                .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "الإعدادات",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Bottom title & status
                Column {
                    Text(
                        text = "نظام تتبع ورعاية القطيع الذكي",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        ),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "مزرعة الأمل النموذجية • حالة القطيع العامة: ${summary.healthyPercentage}% سليم وطبيعي",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusBanner(
    message: String,
    onDismiss: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = BoviGreenPrimary.copy(alpha = 0.12f),
        border = BorderStroke(1.dp, BoviGreenPrimary.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Sensors,
                    contentDescription = null,
                    tint = BoviGreenPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "إغلاق",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun UrgentAlertCallout(
    estrusCount: Int,
    lamenessCount: Int,
    feverCount: Int,
    onViewAlerts: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("urgent_alerts_callout"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AlertLamenessOrange.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, AlertLamenessOrange.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(AlertLamenessOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.NotificationsActive,
                        contentDescription = null,
                        tint = AlertLamenessOrange,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "يوجد تنبيهات عاجلة تتطلب تدخلك!",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = buildString {
                            if (estrusCount > 0) append("$estrusCount بدء شبق • ")
                            if (lamenessCount > 0) append("$lamenessCount اشتباه عرج • ")
                            if (feverCount > 0) append("$feverCount حرارة")
                        }.trimEnd('•', ' '),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Button(
                onClick = onViewAlerts,
                colors = ButtonDefaults.buttonColors(containerColor = AlertLamenessOrange),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("معاينة", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}
