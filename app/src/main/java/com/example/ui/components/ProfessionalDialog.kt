package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Professional

@Composable
fun ProfessionalDialog(
    professional: Professional?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        name: String,
        role: String,
        phone: String,
        commission: Int,
        workingDays: String,
        workingHours: String,
        colorHex: Long
    ) -> Unit
) {
    var name by remember { mutableStateOf(professional?.name ?: "") }
    var role by remember { mutableStateOf(professional?.role ?: "") }
    var phone by remember { mutableStateOf(professional?.phone ?: "") }
    var commissionFloat by remember { mutableFloatStateOf((professional?.commissionPercent ?: 50).toFloat()) }
    var workingDays by remember { mutableStateOf(professional?.workingDays ?: "Terça a Sábado") }
    var workingHours by remember { mutableStateOf(professional?.workingHours ?: "09:00 às 19:00") }
    var selectedColor by remember { mutableLongStateOf(professional?.colorHex ?: 0xFF9E475A) }

    val avatarPalette = listOf(
        0xFF9E475A, 0xFF7A4E82, 0xFF3D6B58, 0xFFB57A2A, 0xFF2A6099, 0xFF9E5D2A
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("professional_modal"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (professional == null) "Novo(a) Especialista da Equipe" else "Editar Especialista",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_prof_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nome Completo") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("prof_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Role
                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Especialidade / Cargo") },
                    placeholder = { Text("Ex: Hair Stylist & Colorista, Manicure, Barbeiro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("prof_role_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Phone
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("WhatsApp de Contato") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    placeholder = { Text("11987654321") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("prof_phone_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Commission Slider
                Text(
                    text = "Comissão: ${commissionFloat.toInt()}% sobre serviços realizados",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = commissionFloat,
                    onValueChange = { commissionFloat = it },
                    valueRange = 10f..90f,
                    steps = 15,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Working Days
                OutlinedTextField(
                    value = workingDays,
                    onValueChange = { workingDays = it },
                    label = { Text("Dias de Atendimento") },
                    placeholder = { Text("Ex: Terça a Sábado, Segunda a Sexta") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Working Hours
                OutlinedTextField(
                    value = workingHours,
                    onValueChange = { workingHours = it },
                    label = { Text("Horário de Disponibilidade") },
                    leadingIcon = { Icon(Icons.Default.Schedule, contentDescription = null) },
                    placeholder = { Text("09:00 às 19:00") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Avatar Color Picker
                Text(
                    text = "Cor de Identificação na Agenda:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    avatarPalette.forEach { hex ->
                        val isSelected = selectedColor == hex
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(hex), CircleShape)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = {
                        if (name.isNotBlank() && role.isNotBlank()) {
                            onSave(
                                professional?.id ?: 0L,
                                name,
                                role,
                                phone,
                                commissionFloat.toInt(),
                                workingDays,
                                workingHours,
                                selectedColor
                            )
                        }
                    },
                    enabled = name.isNotBlank() && role.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().testTag("save_prof_button")
                ) {
                    Text("Salvar Especialista", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
