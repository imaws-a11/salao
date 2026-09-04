package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.Appointment
import com.example.ui.ClientTab
import com.example.ui.SalonViewModel
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.PaymentDialog
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoOnPrimary
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldLoyalty
import com.example.ui.theme.PixTeal
import com.example.ui.theme.WhatsAppBrandGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ClientAppointmentsScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val clientAppointments by viewModel.clientAppointments.collectAsState()
    val activeClient by viewModel.activeClient.collectAsState()
    val isFeedbackOpen by viewModel.isFeedbackDialogVisible.collectAsState()
    val selectedAppointmentForFeedback by viewModel.selectedAppointmentForFeedback.collectAsState()
    val selectedProfessionalForFeedback by viewModel.targetProfessionalForFeedback.collectAsState()
    val paymentAppointment by viewModel.selectedAppointmentForPayment.collectAsState()

    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Próximos, 1: Histórico

    val todayStr = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }

    val upcomingAppointments = remember(clientAppointments, todayStr) {
        clientAppointments.filter { it.appointmentDate >= todayStr && it.status != "CANCELADO" && it.status != "CONCLUIDO" }
    }

    val pastAppointments = remember(clientAppointments, todayStr) {
        clientAppointments.filter { it.appointmentDate < todayStr || it.status == "CONCLUIDO" || it.status == "CANCELADO" }
    }

    val displayedList = if (selectedSubTab == 0) upcomingAppointments else pastAppointments
    val context = LocalContext.current

    Box(modifier = modifier.fillMaxSize().background(BentoBackground)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
        ) {
            // Header Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .testTag("client_appointments_header"),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BentoBorder.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Meus Compromissos",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = BentoTextPrimary
                            )
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BentoPrimaryContainer
                            ) {
                                Text(
                                    text = "${clientAppointments.size} no total",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Acompanhe seus horários agendados, confirme no WhatsApp ou avalie seus atendimentos realizados.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoTextSecondary
                        )
                    }
                }
            }

            // Sub-Tab Switcher
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BentoSurfaceVariant)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("Próximos (${upcomingAppointments.size})", "Histórico (${pastAppointments.size})").forEachIndexed { index, title ->
                        val isSelected = selectedSubTab == index
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedSubTab = index },
                            color = if (isSelected) BentoPrimary else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) BentoOnPrimary else BentoTextPrimary
                            )
                        }
                    }
                }
            }

            if (displayedList.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = BentoSurface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(BentoPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DateRange,
                                    contentDescription = null,
                                    tint = BentoPrimary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Text(
                                text = if (selectedSubTab == 0) "Nenhum agendamento futuro" else "Nenhum histórico anterior",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            Text(
                                text = "Que tal escolher um novo serviço para realçar sua beleza hoje mesmo?",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoTextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Button(
                                onClick = { viewModel.selectClientTab(ClientTab.BOOKING) },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                            ) {
                                Text("Agendar Novo Horário", fontWeight = FontWeight.Bold, color = BentoOnPrimary)
                            }
                        }
                    }
                }
            } else {
                items(displayedList, key = { it.id }) { apt ->
                    ClientAppointmentCard(
                        appointment = apt,
                        isUpcoming = selectedSubTab == 0,
                        onWhatsAppClick = {
                            val message = "Olá! Gostaria de confirmar meu agendamento de *${apt.serviceName}* no dia *${apt.appointmentDate}* às *${apt.appointmentTime}* com *${apt.professionalName}*. (Cliente: ${apt.clientName})"
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://api.whatsapp.com/send?phone=5511987654321&text=${Uri.encode(message)}")
                            }
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback
                            }
                        },
                        onPayClick = {
                            viewModel.selectedAppointmentForPayment.value = apt
                        },
                        onReviewClick = {
                            viewModel.openFeedbackDialogForAppointment(apt)
                        }
                    )
                }
            }
        }
    }

    // Feedback Dialog for rating
    if (isFeedbackOpen) {
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

    // Online Payment Dialog
    if (paymentAppointment != null) {
        PaymentDialog(
            appointment = paymentAppointment!!,
            onDismiss = { viewModel.selectedAppointmentForPayment.value = null },
            onPaymentSuccess = { method: String ->
                viewModel.confirmOnlinePayment(paymentAppointment!!.id, method)
            }
        )
    }
}

@Composable
fun ClientAppointmentCard(
    appointment: Appointment,
    isUpcoming: Boolean,
    onWhatsAppClick: () -> Unit,
    onPayClick: () -> Unit,
    onReviewClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("client_appointment_card_${appointment.id}"),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BentoBorder.copy(alpha = 0.5f)))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status Pill
                val statusColor = when (appointment.status) {
                    "CONFIRMADO" -> EmeraldSuccess
                    "CONCLUIDO" -> BentoPrimary
                    "CANCELADO" -> AmberWarning
                    else -> BentoPrimary
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = statusColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = appointment.status,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Payment Status
                val isPaid = appointment.paymentStatus.startsWith("PAGO")
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPaid) EmeraldSuccess.copy(alpha = 0.12f) else AmberWarning.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (isPaid) "PAGO ONLINE" else "PAGAMENTO PENDENTE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isPaid) EmeraldSuccess else AmberWarning,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Service and Professional
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = appointment.serviceName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = BentoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Profissional: ${appointment.professionalName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                }
            }

            // Date, Time and Price Bento Box
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = BentoSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = BentoPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Column {
                            Text(
                                text = "${appointment.appointmentDate} às ${appointment.appointmentTime}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            Text(
                                text = "Horário reservado",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = BentoTextSecondary
                            )
                        }
                    }

                    Text(
                        text = "R$ ${String.format(Locale.getDefault(), "%.2f", appointment.servicePrice)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = BentoPrimary
                    )
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isUpcoming) {
                    Button(
                        onClick = onWhatsAppClick,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("whatsapp_confirm_${appointment.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WhatsAppBrandGreen)
                    ) {
                        Text(
                            text = "WhatsApp",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    if (!appointment.paymentStatus.startsWith("PAGO")) {
                        Button(
                            onClick = onPayClick,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("pay_online_${appointment.id}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PixTeal)
                        ) {
                            Text(
                                text = "Pagar Pix",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                } else {
                    // Past appointment - Review Action
                    Button(
                        onClick = onReviewClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("client_review_button_${appointment.id}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldLoyalty)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = BentoTextPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Avaliar Atendimento",
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
