package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Professional
import com.example.data.model.SalonService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewAppointmentDialog(
    services: List<SalonService>,
    professionals: List<Professional>,
    onDismiss: () -> Unit,
    onConfirm: (
        clientName: String,
        clientPhone: String,
        service: SalonService,
        professional: Professional,
        date: String,
        time: String,
        notes: String
    ) -> Unit
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val cal = Calendar.getInstance()
    val today = dateFormat.format(cal.time)
    cal.add(Calendar.DAY_OF_YEAR, 1)
    val tomorrow = dateFormat.format(cal.time)

    var clientName by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    var selectedService by remember { mutableStateOf(services.firstOrNull()) }
    var selectedProfessional by remember { mutableStateOf(professionals.firstOrNull()) }
    var appointmentDate by remember { mutableStateOf(today) }
    var appointmentTime by remember { mutableStateOf("10:00") }
    var notes by remember { mutableStateOf("") }

    var serviceDropdownExpanded by remember { mutableStateOf(false) }
    var professionalDropdownExpanded by remember { mutableStateOf(false) }

    val commonTimeSlots = listOf("09:00", "10:00", "11:30", "13:30", "14:30", "16:00", "17:30", "19:00")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("new_appointment_modal"),
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
                        text = "Novo Agendamento Online",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_new_appointment_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Client Name
                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Nome da Cliente / Cliente") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Client Phone
                OutlinedTextField(
                    value = clientPhone,
                    onValueChange = { clientPhone = it },
                    label = { Text("WhatsApp / Celular com DDD") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                    placeholder = { Text("Ex: 11987654321") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("client_phone_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Service Dropdown
                ExposedDropdownMenuBox(
                    expanded = serviceDropdownExpanded,
                    onExpandedChange = { serviceDropdownExpanded = !serviceDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedService?.let { "${it.name} - R$ ${String.format(Locale.getDefault(), "%.2f", it.price)}" } ?: "Selecione o Serviço",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Procedimento / Serviço") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = serviceDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = serviceDropdownExpanded,
                        onDismissRequest = { serviceDropdownExpanded = false }
                    ) {
                        services.forEach { service ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(service.name, fontWeight = FontWeight.SemiBold)
                                        Text("R$ ${String.format(Locale.getDefault(), "%.2f", service.price)} • ${service.durationMinutes} min", style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedService = service
                                    serviceDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Professional Dropdown
                ExposedDropdownMenuBox(
                    expanded = professionalDropdownExpanded,
                    onExpandedChange = { professionalDropdownExpanded = !professionalDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedProfessional?.let { "${it.name} (${it.role})" } ?: "Selecione o(a) Profissional",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Profissional Responsável") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = professionalDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = professionalDropdownExpanded,
                        onDismissRequest = { professionalDropdownExpanded = false }
                    ) {
                        professionals.forEach { prof ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(prof.name, fontWeight = FontWeight.SemiBold)
                                        Text(prof.role, style = MaterialTheme.typography.bodySmall)
                                    }
                                },
                                onClick = {
                                    selectedProfessional = prof
                                    professionalDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Date selection chips
                Text("Data do Horário:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = appointmentDate == today,
                        onClick = { appointmentDate = today },
                        label = { Text("Hoje") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                    FilterChip(
                        selected = appointmentDate == tomorrow,
                        onClick = { appointmentDate = tomorrow },
                        label = { Text("Amanhã") }
                    )
                }

                OutlinedTextField(
                    value = appointmentDate,
                    onValueChange = { appointmentDate = it },
                    label = { Text("Data (AAAA-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Time Slots
                Text("Horário de Início:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(commonTimeSlots) { slot ->
                        FilterChip(
                            selected = appointmentTime == slot,
                            onClick = { appointmentTime = slot },
                            label = { Text(slot) },
                            leadingIcon = if (appointmentTime == slot) {
                                { Icon(Icons.Default.AccessTime, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Notes
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observações (preferência de cor, formato, etc.)") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val serv = selectedService ?: return@Button
                        val prof = selectedProfessional ?: return@Button
                        if (clientName.isNotBlank() && clientPhone.isNotBlank()) {
                            onConfirm(clientName, clientPhone, serv, prof, appointmentDate, appointmentTime, notes)
                        }
                    },
                    enabled = clientName.isNotBlank() && clientPhone.isNotBlank() && selectedService != null && selectedProfessional != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_appointment_button")
                ) {
                    Text("Confirmar Agendamento", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
