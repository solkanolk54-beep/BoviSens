package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BoviGreenPrimary

@Composable
fun SettingsDialog(
    currentUrl: String,
    onDismiss: () -> Unit,
    onSaveUrl: (String) -> Unit,
    onSimulateTelemetry: () -> Unit
) {
    var urlInput by remember { mutableStateOf(currentUrl) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("settings_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, tint = BoviGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "إعدادات الربط ومستشعرات BoviSens",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "عنوان خادم الـ Backend (API Server):",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "يمكنك ربط التطبيق بالخادم المحلي أو خادم المزرعة لجلب مسار api/v1/cows الحقيقي.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("Base URL") },
                    placeholder = { Text("http://10.0.2.2:3000/ أو http://localhost:3000/") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_url_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Presets
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { urlInput = "http://10.0.2.2:3000/" },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("محاكي أندرويد (10.0.2.2)")
                    }
                    OutlinedButton(
                        onClick = { urlInput = "http://localhost:3000/" },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("جهاز محلي (localhost)")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Divider()
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "محاكاة البث الحي 24/24 (IoT Telemetry Simulation):",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = "إرسال قراءات مستشعرات افتراضية جديدة للأبقار (الحرارة، الاجترار، خطوات الحركة) لاختبار نظام المراقبة 24/24 في حال عدم تشغيل خادم محلي.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        onSimulateTelemetry()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BoviGreenPrimary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("simulate_telemetry_button")
                ) {
                    Icon(imageVector = Icons.Default.Sensors, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("بث قراءات مستشعرات فورية")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("إلغاء")
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            onSaveUrl(urlInput)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BoviGreenPrimary),
                        modifier = Modifier.testTag("save_settings_button")
                    ) {
                        Text("حفظ الإعدادات")
                    }
                }
            }
        }
    }
}
