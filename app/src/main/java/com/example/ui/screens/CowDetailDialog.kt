package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Cow
import com.example.ui.theme.AlertCriticalRed
import com.example.ui.theme.AlertEstrusPurple
import com.example.ui.theme.AlertLamenessOrange
import com.example.ui.theme.AlertMilkBlue
import com.example.ui.theme.BoviGreenPrimary
import com.example.ui.theme.StatusNormalGreen

@Composable
fun CowDetailDialog(
    cow: Cow,
    onDismiss: () -> Unit,
    onResolveAlert: (tag: String, note: String) -> Unit,
    onDeleteCow: (tag: String) -> Unit
) {
    var showActionDialog by remember { mutableStateOf(false) }
    var actionNote by remember { mutableStateOf("") }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val statusColor = when {
        cow.healthStatus == "طبيعي" -> StatusNormalGreen
        cow.healthStatus.contains("عرج") -> AlertLamenessOrange
        cow.healthStatus.contains("شبق") -> AlertEstrusPurple
        cow.healthStatus.contains("حرارة") -> AlertCriticalRed
        else -> AlertLamenessOrange
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("cow_detail_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top bar with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_detail_button")
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = statusColor.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, statusColor.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = cow.healthStatus,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = statusColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Hero Cow Identity
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(statusColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Pets,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "البقرة #${cow.tagNumber}",
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "${cow.name} • ${cow.breed} • ${cow.barnGroup}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Urgent Alert callout if present
                if (cow.urgentAlertTitle != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = statusColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "تنبيه ذكي عاجل (مستشعر 24/24)",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = statusColor
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = cow.urgentAlertTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { showActionDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = statusColor),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("action_button")
                            ) {
                                Icon(imageVector = Icons.Default.Healing, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("اتخاذ إجراء بيطري / إنهاء التنبيه")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "بيانات المستشعرات الحية (BoviSens Smart Collar)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2x2 Telemetry Grid
                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricBox(
                        title = "إنتاج الحليب اليومي",
                        value = "${cow.dailyMilk} لتر",
                        subtitle = "أيام الحلب: ${cow.daysInMilk} يوم",
                        icon = Icons.Default.LocalDrink,
                        tint = AlertMilkBlue,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    MetricBox(
                        title = "وقت الاجترار اليومي",
                        value = "${cow.ruminationHours} ساعة",
                        subtitle = if (cow.ruminationHours < 6) "تحذير: اجترار منخفض" else "معدل اجترار مثالي",
                        icon = Icons.Default.Pets,
                        tint = if (cow.ruminationHours < 6) AlertCriticalRed else BoviGreenPrimary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    MetricBox(
                        title = "مؤشر النشاط والحركة",
                        value = "${cow.activityIndex}%",
                        subtitle = if (cow.activityIndex > 170) "ارتفاع حاد (علامة شبق)" else if (cow.activityIndex < 60) "خمول (علامة عرج)" else "نشاط طبيعي",
                        icon = Icons.Default.DirectionsRun,
                        tint = if (cow.activityIndex > 170) AlertEstrusPurple else if (cow.activityIndex < 60) AlertLamenessOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    MetricBox(
                        title = "درجة حرارة الجسم",
                        value = "${cow.bodyTemp}°C",
                        subtitle = if (cow.bodyTemp > 39.4) "حمى واشتباه التهاب" else "حرارة طبيعية",
                        icon = Icons.Default.Thermostat,
                        tint = if (cow.bodyTemp > 39.4) AlertCriticalRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Reproductive status card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Favorite, contentDescription = null, tint = AlertEstrusPurple, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "الحالة التناسلية والتلقيح", style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = cow.reproductiveStatus, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Notes
                if (cow.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "ملاحظات السجل البيطري:", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = cow.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(
                        onClick = { showDeleteConfirm = true },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertCriticalRed),
                        modifier = Modifier.testTag("delete_cow_button")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("حذف")
                    }

                    Button(
                        onClick = {
                            actionNote = "فحص روتيني سليم وتسجيل متابعة دورية"
                            showActionDialog = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BoviGreenPrimary),
                        modifier = Modifier.testTag("record_intervention_button")
                    ) {
                        Icon(imageVector = Icons.Default.Healing, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("تسجيل فحص بيطري")
                    }
                }
            }
        }
    }

    // Sub-dialog: record intervention / resolve alert
    if (showActionDialog) {
        AlertDialog(
            onDismissRequest = { showActionDialog = false },
            title = { Text("تسجيل إجراء بيطري - #${cow.tagNumber}") },
            text = {
                Column {
                    Text(
                        text = "أدخل تفاصيل الفحص، الدواء، أو التلقيح الاصطناعي الذي تم تنفيذه للبقرة:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = actionNote,
                        onValueChange = { actionNote = it },
                        label = { Text("ملاحظة الإجراء البيطري") },
                        placeholder = { Text("مثال: تم إعطاء مضاد التهاب وفحص الحافر / تم التلقيح الاصطناعي") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_note_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onResolveAlert(cow.tagNumber, actionNote.ifBlank { "تم اتخاذ الإجراء البيطري المناسب" })
                        showActionDialog = false
                        onDismiss()
                    },
                    modifier = Modifier.testTag("confirm_action_button")
                ) {
                    Text("حفظ وإغلاق التنبيه")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showActionDialog = false }) {
                    Text("إلغاء")
                }
            }
        )
    }

    // Sub-dialog: confirm delete
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("تأكيد الحذف") },
            text = { Text("هل أنت متأكد من حذف البقرة رقم #${cow.tagNumber} من نظام المراقبة؟") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCow(cow.tagNumber)
                        showDeleteConfirm = false
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertCriticalRed),
                    modifier = Modifier.testTag("confirm_delete_button")
                ) {
                    Text("نعم، حذف")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = false }) {
                    Text("إلغاء")
                }
            }
        )
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, style = MaterialTheme.typography.labelSmall, color = tint)
        }
    }
}
