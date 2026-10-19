package com.example.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.BoviGreenPrimary

@Composable
fun AddCowDialog(
    onDismiss: () -> Unit,
    onAddCow: (
        tagNumber: String,
        name: String,
        breed: String,
        barnGroup: String,
        dailyMilk: Double,
        healthStatus: String,
        notes: String
    ) -> Unit
) {
    var tagNumber by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("هولشتاين") }
    var barnGroup by remember { mutableStateOf("الحظيرة A") }
    var dailyMilkStr by remember { mutableStateOf("25.0") }
    var selectedHealthStatus by remember { mutableStateOf("طبيعي") }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val healthOptions = listOf("طبيعي", "عرج", "بدء شبق", "حرارة", "تحت الملاحظة")
    val breedOptions = listOf("هولشتاين", "مونتبليارد", "سويسرية بنية", "أخرى")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .testTag("add_cow_dialog"),
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
                    Text(
                        text = "تسجيل بقرة جديدة في القطيع",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "إلغاء")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tag number
                OutlinedTextField(
                    value = tagNumber,
                    onValueChange = {
                        tagNumber = it
                        errorMessage = null
                    },
                    label = { Text("رقم القرط / الشريحة الإلكترونية (Tag Number) *") },
                    placeholder = { Text("مثال: DZ-502") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_tag_number"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("اسم البقرة أو العلامة المميزة") },
                    placeholder = { Text("مثال: مبروكة") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_cow_name"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Breed selection
                Text(
                    text = "السلالة:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    breedOptions.forEach { opt ->
                        FilterChip(
                            selected = breed == opt,
                            onClick = { breed = opt },
                            label = { Text(opt) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Barn Group
                OutlinedTextField(
                    value = barnGroup,
                    onValueChange = { barnGroup = it },
                    label = { Text("الحظيرة أو المجموعة") },
                    placeholder = { Text("الحظيرة A / الحظيرة B") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Daily milk
                OutlinedTextField(
                    value = dailyMilkStr,
                    onValueChange = { dailyMilkStr = it },
                    label = { Text("إنتاج الحليب المقدر (لتر/يوم)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_daily_milk"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Health status
                Text(
                    text = "الحالة الصحية الأولية:",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    healthOptions.forEach { status ->
                        FilterChip(
                            selected = selectedHealthStatus == status,
                            onClick = { selectedHealthStatus = status },
                            label = { Text(status) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("ملاحظات بيطرية أو سياق التغذية") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

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
                            if (tagNumber.isBlank()) {
                                errorMessage = "يرجى إدخال رقم القرط / الشريحة الإلكترونية"
                                return@Button
                            }
                            val milkVal = dailyMilkStr.toDoubleOrNull() ?: 0.0
                            onAddCow(
                                tagNumber,
                                name,
                                breed,
                                barnGroup,
                                milkVal,
                                selectedHealthStatus,
                                notes
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BoviGreenPrimary),
                        modifier = Modifier.testTag("save_new_cow_button")
                    ) {
                        Text("حفظ وتسجيل")
                    }
                }
            }
        }
    }
}
