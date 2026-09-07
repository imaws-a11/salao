package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Appointment
import com.example.ui.SalonTab
import com.example.ui.SalonViewModel
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.NewAppointmentDialog
import com.example.ui.components.PaymentDialog
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBlueAccent
import com.example.ui.theme.BentoBlueDark
import com.example.ui.theme.BentoBlueOnAccent
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoPinkAccent
import com.example.ui.theme.BentoPinkOnAccent
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceSubtle
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldLoyalty
import com.example.ui.theme.PixTeal
import com.example.ui.theme.WhatsAppBrandGreen
import com.example.util.WhatsAppHelper
import java.util.Locale

@Composable
fun ScheduleScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appointments by viewModel.filteredAppointments.collectAsStateWithLifecycle()
    val allAppointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val filter by viewModel.appointmentDateFilter.collectAsStateWithLifecycle()
    val services by viewModel.services.collectAsStateWithLifecycle()
    val professionals by viewModel.professionals.collectAsStateWithLifecycle()
    val loyaltyClients by viewModel.loyaltyClients.collectAsStateWithLifecycle()
    val firestoreState by viewModel.firestoreSyncState.collectAsStateWithLifecycle()

    val isNewAppointmentVisible by viewModel.isNewAppointmentDialogVisible.collectAsStateWithLifecycle()
    val selectedAppointmentForPayment by viewModel.selectedAppointmentForPayment.collectAsStateWithLifecycle()
    val isFeedbackDialogVisible by viewModel.isFeedbackDialogVisible.collectAsStateWithLifecycle()
    val selectedAppointmentForFeedback by viewModel.selectedAppointmentForFeedback.collectAsStateWithLifecycle()
    val selectedProfessionalForFeedback by viewModel.selectedProfessionalForFeedback.collectAsStateWithLifecycle()

    val totalProjectedRevenue = allAppointments.filter { it.status != "CANCELADO" }.sumOf { it.servicePrice }
    val totalLoyaltyMembers = loyaltyClients.size

    Scaffold(
        containerColor = BentoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.isNewAppointmentDialogVisible.value = true },
                containerColor = BentoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("fab_new_appointment")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Novo Agendamento")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ==================== BENTO GRID HERO TILES ====================
            // Bento Tile 1: Receita Mensal Prevista (Full width / col-span-2)
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(BentoPrimaryContainer)
                        .padding(18.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RECEITA MENSAL",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = BentoOnPrimaryContainer
                            )
                            Surface(
                                color = Color.White,
                                shape = CircleShape
                            ) {
                                Text(
                                    text = "+12%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "R$ ${String.format(Locale.getDefault(), "%.0f", totalProjectedRevenue)}",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = BentoOnPrimaryContainer
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "previsto no mês",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoTextSecondary,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                        }
                    }
                }
            }

            // Bento Tiles 2, 3, 4: Asymmetric Row (Left: Novo Agendamento; Right Stack: Fidelidade & Backup)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Left Bento Tile: Novo Agendamento (col-span-1 row-span-2 equivalent)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(144.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(BentoSurfaceVariant)
                            .border(1.dp, BentoBorder.copy(alpha = 0.6f), RoundedCornerShape(26.dp))
                            .clickable { viewModel.isNewAppointmentDialogVisible.value = true }
                            .padding(14.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(BentoPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Novo",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "Novo Agendamento",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                                Text(
                                    text = "WhatsApp integrado",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }

                    // Right Stack Bento Tiles: Fidelidade + Backup Nuvem
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(144.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Tile 3: Fidelidade
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(BentoSurface)
                                .border(1.dp, BentoBorderLight, RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectTab(SalonTab.LOYALTY_BACKUP) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(BentoPinkAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = BentoPinkOnAccent, modifier = Modifier.size(16.dp))
                                }
                                Column {
                                    Text(
                                        text = "$totalLoyaltyMembers",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                    Text(
                                        text = "FIDELIDADE",
                                        style = MaterialTheme.typography.labelSmall,
                                        letterSpacing = 0.5.sp,
                                        color = BentoTextSecondary,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }

                        // Tile 4: Backup Nuvem
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(BentoBlueAccent)
                                .border(1.dp, Color(0xFFC2D6F6), RoundedCornerShape(20.dp))
                                .clickable { viewModel.selectTab(SalonTab.LOYALTY_BACKUP) }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(BentoBlueOnAccent),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = if (firestoreState.isLiveConnected) "FIRESTORE OK" else "BACKUP OK",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = BentoBlueDark
                                )
                            }
                        }
                    }
                }
            }

            // ==================== FILTER CHIPS ====================
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "HOJE" to "Hoje",
                        "AMANHA" to "Amanhã",
                        "SEMANA" to "Esta Semana",
                        "TODOS" to "Todos"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = filter == key,
                            onClick = { viewModel.setAppointmentDateFilter(key) },
                            label = { Text(label, fontWeight = if (filter == key) FontWeight.Bold else FontWeight.Normal) },
                            shape = RoundedCornerShape(16.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BentoPrimaryContainer,
                                selectedLabelColor = BentoOnPrimaryContainer,
                                containerColor = BentoSurface
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = filter == key,
                                borderColor = BentoBorder.copy(alpha = 0.6f),
                                selectedBorderColor = BentoPrimary
                            ),
                            modifier = Modifier.testTag("filter_chip_$key")
                        )
                    }
                }
            }

            // ==================== PRÓXIMOS HORÁRIOS SECTION HEADER ====================
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF22C55E))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Próximos Horários",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (firestoreState.isLiveConnected) EmeraldSuccess.copy(alpha = 0.12f) else BentoSurfaceSubtle,
                            border = BorderStroke(1.dp, if (firestoreState.isLiveConnected) EmeraldSuccess.copy(alpha = 0.35f) else BentoBorderLight)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = if (firestoreState.isLiveConnected) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = if (firestoreState.isLiveConnected) EmeraldSuccess else BentoTextSecondary,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = if (firestoreState.isLiveConnected) "Firestore" else "Room Local",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (firestoreState.isLiveConnected) EmeraldSuccess else BentoTextSecondary
                                )
                            }
                        }

                        Text(
                            text = "${appointments.size} NO FILTRO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            color = BentoPrimary
                        )
                    }
                }
            }

            // Empty state
            if (appointments.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(BentoSurface)
                            .border(1.dp, BentoBorderLight, RoundedCornerShape(26.dp))
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EventAvailable,
                                contentDescription = null,
                                modifier = Modifier.size(52.dp),
                                tint = BentoBorder
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Nenhum horário neste filtro",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Toque em 'Novo Agendamento' para marcar horários.",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(appointments, key = { it.id }) { appointment ->
                    BentoAppointmentCard(
                        appointment = appointment,
                        onConfirmWhatsApp = { WhatsAppHelper.sendAppointmentConfirmation(context, appointment) },
                        onOpenPayment = { viewModel.openPaymentModal(appointment) },
                        onComplete = { viewModel.completeAppointment(appointment, "PIX") },
                        onReview = { viewModel.openFeedbackDialog(appointment, null) },
                        onCancel = { viewModel.cancelAppointment(appointment) }
                    )
                }
            }
        }
    }

    // New Appointment Dialog
    if (isNewAppointmentVisible) {
        NewAppointmentDialog(
            services = services,
            professionals = professionals,
            onDismiss = { viewModel.isNewAppointmentDialogVisible.value = false },
            onConfirm = { name, phone, service, prof, date, time, notes ->
                viewModel.addAppointment(name, phone, service, prof, date, time, notes)
            }
        )
    }

    // Online Payment Dialog
    selectedAppointmentForPayment?.let { apt ->
        PaymentDialog(
            appointment = apt,
            onDismiss = { viewModel.closePaymentModal() },
            onPaymentSuccess = { method ->
                viewModel.confirmOnlinePayment(apt.id, method)
            }
        )
    }

    // Customer Feedback Dialog
    if (isFeedbackDialogVisible) {
        FeedbackDialog(
            appointment = selectedAppointmentForFeedback,
            professional = selectedProfessionalForFeedback,
            onDismiss = { viewModel.closeFeedbackDialog() },
            onSubmit = { aptId, clientName, clientPhone, profId, profName, servId, servName, rating, comment ->
                viewModel.submitFeedback(
                    aptId,
                    clientName,
                    clientPhone,
                    profId,
                    profName,
                    servId,
                    servName,
                    rating,
                    comment
                )
            }
        )
    }
}

@Composable
fun BentoAppointmentCard(
    appointment: Appointment,
    onConfirmWhatsApp: () -> Unit,
    onOpenPayment: () -> Unit,
    onComplete: () -> Unit,
    onReview: () -> Unit,
    onCancel: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }

    val isPaidOnline = appointment.paymentStatus.startsWith("PAGO")

    val initials = appointment.clientName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first() }
        .joinToString("")
        .uppercase()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(BentoSurface)
            .border(1.dp, BentoBorder.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
            .padding(14.dp)
            .testTag("appointment_item_${appointment.id}")
    ) {
        Column {
            // Row 1: Client details + Time pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BentoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = appointment.clientName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "${appointment.serviceName} • ${appointment.professionalName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoTextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = appointment.appointmentTime,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoPrimary
                    )

                    Box {
                        IconButton(onClick = { menuExpanded = true }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Opções", modifier = Modifier.size(16.dp))
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Cancelar Horário") },
                                onClick = {
                                    menuExpanded = false
                                    onCancel()
                                },
                                leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Price + Payment Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BentoSurfaceSubtle)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "R$ ${String.format(Locale.getDefault(), "%.2f", appointment.servicePrice)}",
                    fontWeight = FontWeight.ExtraBold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = BentoPrimary
                )

                Surface(
                    color = if (isPaidOnline) EmeraldSuccess.copy(alpha = 0.15f) else Color(0xFFF39C12).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isPaidOnline) "✓ Pago Online" else "Pagamento Pendente",
                        color = if (isPaidOnline) EmeraldSuccess else Color(0xFFD68910),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 3: Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // WhatsApp button
                Button(
                    onClick = onConfirmWhatsApp,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppBrandGreen),
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .weight(1.1f)
                        .testTag("whatsapp_button_${appointment.id}")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "WhatsApp",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                // Online Payment button
                if (!isPaidOnline && appointment.status != "CANCELADO") {
                    OutlinedButton(
                        onClick = onOpenPayment,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("pay_button_${appointment.id}")
                    ) {
                        Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(14.dp), tint = PixTeal)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Pix / Cartão",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Complete button
                if (appointment.status != "CONCLUIDO" && appointment.status != "CANCELADO") {
                    FilledTonalButton(
                        onClick = onComplete,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("complete_button_${appointment.id}")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Concluir",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                // Review button if completed
                if (appointment.status == "CONCLUIDO") {
                    OutlinedButton(
                        onClick = onReview,
                        shape = RoundedCornerShape(14.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("review_button_${appointment.id}")
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = GoldLoyalty)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Avaliar",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoTextPrimary
                        )
                    }
                }
            }
        }
    }
}
