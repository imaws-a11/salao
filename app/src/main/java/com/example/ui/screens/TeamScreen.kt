package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.model.CustomerFeedback
import com.example.data.model.Professional
import com.example.ui.SalonViewModel
import com.example.ui.components.FeedbackDialog
import com.example.ui.components.ProfessionalDialog
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceSubtle
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.GoldLoyalty
import com.example.ui.theme.WhatsAppBrandGreen
import com.example.util.WhatsAppHelper
import java.util.Locale

@Composable
fun TeamScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val professionals by viewModel.professionals.collectAsStateWithLifecycle()
    val feedbacks by viewModel.allFeedbacks.collectAsStateWithLifecycle()
    val professionalRatings by viewModel.professionalRatings.collectAsStateWithLifecycle()

    val isProfessionalDialogVisible by viewModel.isProfessionalDialogVisible.collectAsStateWithLifecycle()
    val editingProfessional by viewModel.editingProfessional.collectAsStateWithLifecycle()
    val isFeedbackDialogVisible by viewModel.isFeedbackDialogVisible.collectAsStateWithLifecycle()
    val appointmentForFeedback by viewModel.selectedAppointmentForFeedback.collectAsStateWithLifecycle()
    val professionalForFeedback by viewModel.selectedProfessionalForFeedback.collectAsStateWithLifecycle()

    var activeSubTab by remember { mutableStateOf("PROFISSIONAIS") } // "PROFISSIONAIS" or "AVALIACOES"

    val activeCount = professionals.count { it.active }

    // Calculate Salon Overall Average Rating
    val salonAvgRating = if (feedbacks.isNotEmpty()) feedbacks.map { it.rating }.average() else 5.0

    Scaffold(
        containerColor = BentoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (activeSubTab == "PROFISSIONAIS") {
                        viewModel.editingProfessional.value = null
                        viewModel.isProfessionalDialogVisible.value = true
                    } else {
                        viewModel.openFeedbackDialog(appointment = null, professional = null)
                    }
                },
                containerColor = BentoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("fab_team_action")
            ) {
                Icon(
                    imageVector = if (activeSubTab == "PROFISSIONAIS") Icons.Default.Add else Icons.Default.RateReview,
                    contentDescription = if (activeSubTab == "PROFISSIONAIS") "Adicionar Profissional" else "Nova Avaliação"
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Subtab Selector: Equipe vs Avaliações
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BentoSurfaceSubtle)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeSubTab == "PROFISSIONAIS") BentoPrimary else Color.Transparent)
                            .clickable { activeSubTab = "PROFISSIONAIS" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Groups,
                                contentDescription = null,
                                tint = if (activeSubTab == "PROFISSIONAIS") Color.White else BentoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Equipe (${professionals.size})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (activeSubTab == "PROFISSIONAIS") Color.White else BentoTextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (activeSubTab == "AVALIACOES") BentoPrimary else Color.Transparent)
                            .clickable { activeSubTab = "AVALIACOES" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = null,
                                tint = if (activeSubTab == "AVALIACOES") GoldLoyalty else BentoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Avaliações (${feedbacks.size})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (activeSubTab == "AVALIACOES") Color.White else BentoTextSecondary
                            )
                        }
                    }
                }
            }

            if (activeSubTab == "PROFISSIONAIS") {
                // Bento Tile 1: Equipe Ativa Agora
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(BentoSurfaceVariant)
                            .border(1.dp, BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(26.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Row {
                                    professionals.take(3).forEachIndexed { index, prof ->
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(Color(prof.colorHex))
                                                .border(2.dp, BentoSurfaceVariant, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = prof.name.take(1),
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    if (professionals.size > 3) {
                                        Box(
                                            modifier = Modifier
                                                .size(32.dp)
                                                .clip(CircleShape)
                                                .background(BentoPrimary)
                                                .border(2.dp, BentoSurfaceVariant, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "+${professionals.size - 3}",
                                                color = Color.White,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }

                                Column {
                                    Text(
                                        text = "Equipe Ativa Agora",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = BentoTextPrimary
                                    )
                                    Text(
                                        text = "$activeCount de ${professionals.size} disponíveis",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BentoTextSecondary
                                    )
                                }
                            }

                            Surface(
                                color = Color(0xFF22C55E).copy(alpha = 0.15f),
                                shape = CircleShape
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF22C55E))
                                    )
                                    Text(
                                        text = "ONLINE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = Color(0xFF15803D)
                                    )
                                }
                            }
                        }
                    }
                }

                // List of Professionals as Bento Cards
                items(professionals, key = { it.id }) { prof ->
                    val ratingPair = professionalRatings[prof.id]
                    val avgRating = ratingPair?.first ?: 5.0
                    val reviewCount = ratingPair?.second ?: 0

                    BentoProfessionalCard(
                        professional = prof,
                        avgRating = avgRating,
                        reviewCount = reviewCount,
                        onEdit = {
                            viewModel.editingProfessional.value = prof
                            viewModel.isProfessionalDialogVisible.value = true
                        },
                        onToggleActive = { viewModel.toggleProfessionalActive(prof) },
                        onEvaluate = {
                            viewModel.openFeedbackDialog(appointment = null, professional = prof)
                        },
                        onChatWhatsApp = {
                            val msg = "Olá, ${prof.name}! Entrando em contato através do painel GlowUp Studio."
                            WhatsAppHelper.copyToClipboard(context, msg)
                            val cleanPhone = prof.phone.replace("[^0-9]".toRegex(), "")
                            val fullPhone = if (cleanPhone.startsWith("55")) cleanPhone else "55$cleanPhone"
                            val uri = android.net.Uri.parse("https://api.whatsapp.com/send?phone=$fullPhone&text=${java.net.URLEncoder.encode(msg, "UTF-8")}")
                            context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
                        }
                    )
                }
            } else {
                // FEEDBACK & RATINGS TAB
                item {
                    BentoFeedbackHeader(
                        avgRating = salonAvgRating,
                        totalReviews = feedbacks.size,
                        onNewReview = { viewModel.openFeedbackDialog(null, null) }
                    )
                }

                if (feedbacks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(BentoSurface)
                                .border(1.dp, BentoBorder, RoundedCornerShape(20.dp))
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldLoyalty,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Nenhuma avaliação registrada ainda",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "As notas e depoimentos dos clientes aparecerão aqui",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(feedbacks, key = { it.id }) { feedback ->
                        BentoCustomerFeedbackCard(feedback = feedback)
                    }
                }
            }
        }
    }

    if (isProfessionalDialogVisible) {
        ProfessionalDialog(
            professional = editingProfessional,
            onDismiss = {
                viewModel.isProfessionalDialogVisible.value = false
                viewModel.editingProfessional.value = null
            },
            onSave = { id, name, role, phone, commission, days, hours, color ->
                viewModel.saveProfessional(id, name, role, phone, commission, days, hours, color)
            }
        )
    }

    if (isFeedbackDialogVisible) {
        FeedbackDialog(
            appointment = appointmentForFeedback,
            professional = professionalForFeedback,
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
fun BentoFeedbackHeader(
    avgRating: Double,
    totalReviews: Int,
    onNewReview: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_feedback_header"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Satisfação dos Clientes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "Avaliações e depoimentos de serviços e equipe",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                }

                Button(
                    onClick = onNewReview,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                    modifier = Modifier.testTag("btn_add_review_header")
                ) {
                    Icon(Icons.Default.RateReview, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Avaliar", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BentoSurfaceSubtle)
                    .border(1.dp, BentoBorderLight, RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = String.format(Locale.getDefault(), "%.1f", avgRating),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = BentoTextPrimary
                    )

                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            for (i in 1..5) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    tint = GoldLoyalty,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Média geral do salão",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoTextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldSuccess.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "$totalReviews avaliações",
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
fun BentoCustomerFeedbackCard(feedback: CustomerFeedback) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_feedback_${feedback.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Client Name & Star Rating
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BentoPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = feedback.clientName.take(1).uppercase(),
                            color = BentoPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(
                            text = feedback.clientName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = feedback.date,
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoTextSecondary
                        )
                    }
                }

                // Stars
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= feedback.rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (i <= feedback.rating) GoldLoyalty else BentoTextSecondary.copy(alpha = 0.3f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Service and Professional tags
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BentoSurfaceSubtle)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = feedback.serviceName,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoPrimary
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BentoSurfaceSubtle)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "com ${feedback.professionalName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                }
            }

            // Comment text
            if (feedback.comment.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "“${feedback.comment}”",
                    style = MaterialTheme.typography.bodyMedium,
                    color = BentoTextPrimary,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

@Composable
fun BentoProfessionalCard(
    professional: Professional,
    avgRating: Double,
    reviewCount: Int,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onEvaluate: () -> Unit,
    onChatWhatsApp: () -> Unit
) {
    val initials = professional.name.split(" ")
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
            .border(1.dp, BentoBorderLight, RoundedCornerShape(24.dp))
            .padding(16.dp)
            .testTag("prof_card_${professional.id}")
    ) {
        Column {
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
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(professional.colorHex)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = Color.White,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = professional.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                color = BentoSurfaceSubtle,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = professional.role,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoTextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            // Average Rating Badge on Profile
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(GoldLoyalty.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(
                                    Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldLoyalty,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", avgRating),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                                if (reviewCount > 0) {
                                    Text(
                                        text = " ($reviewCount)",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        color = BentoTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Switch(
                        checked = professional.active,
                        onCheckedChange = { onToggleActive() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BentoPrimary,
                            checkedTrackColor = BentoPrimaryContainer
                        ),
                        modifier = Modifier.testTag("switch_prof_${professional.id}")
                    )

                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(16.dp), tint = BentoBorder)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Info row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(BentoSurfaceSubtle)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${professional.workingDays} • ${professional.workingHours}",
                    style = MaterialTheme.typography.labelSmall,
                    color = BentoTextSecondary
                )
                Text(
                    text = "${professional.commissionPercent}% comissão",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons: Avaliar + WhatsApp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEvaluate,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("btn_evaluate_prof_${professional.id}")
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = GoldLoyalty,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Avaliar Atendimento",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextPrimary
                    )
                }

                OutlinedButton(
                    onClick = onChatWhatsApp,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Chat,
                        contentDescription = null,
                        tint = WhatsAppBrandGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("WhatsApp", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
