package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.AlertMilkBlue
import com.example.ui.theme.BoviGreenPrimary
import com.example.ui.theme.StatusNormalGreen

@Composable
fun CowItemCard(
    cow: Cow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isNormal = cow.healthStatus == "طبيعي"
    val isLameness = cow.healthStatus.contains("عرج")
    val isEstrus = cow.healthStatus.contains("شبق")
    val isFever = cow.healthStatus.contains("حرارة") || cow.healthStatus.contains("التهاب")

    val statusColor = when {
        isNormal -> StatusNormalGreen
        isLameness -> AlertLamenessOrange
        isEstrus -> AlertEstrusPurple
        isFever -> AlertCriticalRed
        else -> AlertLamenessOrange
    }

    val statusIcon = when {
        isNormal -> Icons.Default.CheckCircle
        isLameness -> Icons.Default.Warning
        isEstrus -> Icons.Default.Favorite
        isFever -> Icons.Default.ErrorOutline
        else -> Icons.Default.Warning
    }

    val hasAlert = cow.urgentAlertTitle != null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("cow_card_${cow.tagNumber}")
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (hasAlert) 3.dp else 1.5.dp),
        border = if (hasAlert) BorderStroke(1.5.dp, statusColor.copy(alpha = 0.5f)) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Tag number + Name + Health Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "البقرة #${cow.tagNumber}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${cow.name} • ${cow.breed}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Health status badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = statusColor.copy(alpha = 0.14f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.35f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = cow.healthStatus,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = statusColor
                        )
                    }
                }
            }

            // Urgent alert banner if exists
            if (hasAlert) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = statusColor.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, statusColor.copy(alpha = 0.25f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = cow.urgentAlertTitle ?: "",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = statusColor,
                            maxLines = 2
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Telemetry indicators bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Milk yield
                TelemetryItem(
                    icon = Icons.Default.LocalDrink,
                    label = "الحليب",
                    value = "${cow.dailyMilk} لتر",
                    tint = AlertMilkBlue
                )

                // Rumination
                TelemetryItem(
                    icon = Icons.Default.Pets,
                    label = "الاجترار",
                    value = "${cow.ruminationHours} س",
                    tint = if (cow.ruminationHours < 6.0) AlertCriticalRed else BoviGreenPrimary
                )

                // Activity Index
                TelemetryItem(
                    icon = Icons.Default.DirectionsRun,
                    label = "النشاط",
                    value = "${cow.activityIndex}%",
                    tint = if (cow.activityIndex > 160) AlertEstrusPurple else if (cow.activityIndex < 70) AlertLamenessOrange else MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Body Temp
                TelemetryItem(
                    icon = Icons.Default.Thermostat,
                    label = "الحرارة",
                    value = "${cow.bodyTemp}°C",
                    tint = if (cow.bodyTemp > 39.5) AlertCriticalRed else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TelemetryItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = value,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}
