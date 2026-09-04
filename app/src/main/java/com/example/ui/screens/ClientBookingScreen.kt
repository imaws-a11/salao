package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Professional
import com.example.data.model.SalonService
import com.example.ui.ClientTab
import com.example.ui.SalonViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoOnPrimary
import com.example.ui.theme.BentoPinkAccent
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
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientBookingScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier,
    onOpenProfileSwitcher: () -> Unit = {}
) {
    val activeClient by viewModel.activeClient.collectAsState()
    val services by viewModel.services.collectAsState()
    val professionals by viewModel.professionals.collectAsState()
    val feedbacks by viewModel.allFeedbacks.collectAsState()

    var selectedCategory by remember { mutableStateOf("TODOS") }
    var searchQuery by remember { mutableStateOf("") }
    var serviceToBook by remember { mutableStateOf<SalonService?>(null) }

    val categories = listOf("TODOS", "Cortes", "Coloração", "Tratamentos", "Manicure", "Barba", "Estética")

    val filteredServices = services.filter { service ->
        val matchesCategory = (selectedCategory == "TODOS" || service.category.equals(selectedCategory, ignoreCase = true))
        val matchesSearch = searchQuery.isBlank() ||
                service.name.contains(searchQuery, ignoreCase = true) ||
                service.description.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Client Welcome Bento Header
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .clickable { onOpenProfileSwitcher() }
                    .testTag("client_header_card"),
                colors = CardDefaults.cardColors(containerColor = BentoSurface),
                border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BentoBorder.copy(alpha = 0.5f)))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(BentoPrimaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = activeClient.clientName.take(2).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary,
                                    fontSize = 16.sp
                                )
                            }
                            Column {
                                Text(
                                    text = "Olá, ${activeClient.clientName.split(" ").firstOrNull() ?: activeClient.clientName}! ✨",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                                Text(
                                    text = "Toque para alterar perfil",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoPrimary
                                )
                            }
                        }

                        // Loyalty Points Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = GoldLoyalty.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, GoldLoyalty.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldLoyalty,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "${activeClient.totalPoints} pts",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Agende seu Momento de Beleza",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "Escolha o procedimento, o profissional ideal e garanta seu horário instantaneamente.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BentoTextSecondary
                    )
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("client_service_search_input"),
                placeholder = { Text("Buscar corte, mechas, manicure, barba...", style = MaterialTheme.typography.bodyMedium) },
                shape = RoundedCornerShape(16.dp),
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    focusedContainerColor = BentoSurface,
                    unfocusedContainerColor = BentoSurface,
                    focusedBorderColor = BentoPrimary,
                    unfocusedBorderColor = BentoBorder.copy(alpha = 0.6f)
                ),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { category ->
                    val isSelected = selectedCategory == category
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = category },
                        color = if (isSelected) BentoPrimary else BentoSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) BentoPrimary else BentoBorder.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = category,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BentoOnPrimary else BentoTextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Services Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Serviços Disponíveis (${filteredServices.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
            }
        }

        // Services List
        items(filteredServices, key = { it.id }) { service ->
            ClientServiceBentoCard(
                service = service,
                onBookClick = { serviceToBook = service }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    // Client Booking Flow Bottom Sheet
    if (serviceToBook != null) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { serviceToBook = null },
            sheetState = sheetState,
            containerColor = BentoSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ClientBookingSheetContent(
                service = serviceToBook!!,
                professionals = professionals,
                feedbacks = feedbacks,
                onConfirm = { professional, date, time, notes, paymentMethod ->
                    viewModel.clientBookAppointment(
                        service = serviceToBook!!,
                        professional = professional,
                        date = date,
                        time = time,
                        notes = notes,
                        paymentMethod = paymentMethod
                    )
                    serviceToBook = null
                    // Switch to My Appointments tab so user sees their new booking!
                    viewModel.selectClientTab(ClientTab.MY_APPOINTMENTS)
                },
                onDismiss = { serviceToBook = null }
            )
        }
    }
}

@Composable
fun ClientServiceBentoCard(
    service: SalonService,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("client_service_card_${service.id}"),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BentoBorder.copy(alpha = 0.5f)))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BentoSurfaceVariant
                ) {
                    Text(
                        text = service.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BentoPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = GoldLoyalty.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = "+${service.loyaltyPointsEarned} pts",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "${service.durationMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                }
            }

            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )

            if (service.description.isNotBlank()) {
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Valor",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                    Text(
                        text = "R$ ${String.format(Locale.getDefault(), "%.2f", service.price)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = BentoPrimary
                    )
                }

                Button(
                    onClick = onBookClick,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                    modifier = Modifier.testTag("book_service_button_${service.id}")
                ) {
                    Text(
                        text = "Agendar Horário",
                        fontWeight = FontWeight.Bold,
                        color = BentoOnPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun ClientBookingSheetContent(
    service: SalonService,
    professionals: List<Professional>,
    feedbacks: List<com.example.data.model.CustomerFeedback>,
    onConfirm: (Professional, String, String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val activeProfessionals = remember(professionals) { professionals.filter { it.active } }

    var selectedProf by remember {
        mutableStateOf(activeProfessionals.firstOrNull())
    }

    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) }
    val displayFormat = remember { SimpleDateFormat("EEE, dd MMM", Locale("pt", "BR")) }

    val dateOptions = remember {
        val list = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        for (i in 0..6) {
            val dateStr = dateFormat.format(cal.time)
            val label = if (i == 0) "Hoje" else if (i == 1) "Amanhã" else displayFormat.format(cal.time)
            list.add(Pair(dateStr, label))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    var selectedDate by remember { mutableStateOf(dateOptions.first().first) }

    val timeSlots = listOf("09:00", "10:00", "11:30", "13:30", "14:30", "15:30", "16:30", "17:30", "18:30")
    var selectedTime by remember { mutableStateOf(timeSlots[1]) }

    var paymentMethod by remember { mutableStateOf("SALAO") } // SALAO, PIX, CARTAO
    var clientNotes by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header with Close
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Confirmar Agendamento",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Text(
                    text = "${service.name} • R$ ${String.format(Locale.getDefault(), "%.2f", service.price)}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoPrimary
                )
            }
            IconButton(onClick = onDismiss) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar", tint = BentoTextSecondary)
            }
        }

        // 1. Select Professional
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "1. Escolha o Profissional",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                activeProfessionals.forEach { prof ->
                    val isSelected = selectedProf?.id == prof.id
                    val profFeedbacks = feedbacks.filter { it.professionalId == prof.id }
                    val avgRating = if (profFeedbacks.isNotEmpty()) profFeedbacks.map { it.rating }.average() else 5.0

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { selectedProf = prof },
                        color = if (isSelected) BentoPrimaryContainer else BentoSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 2.dp else 1.dp,
                            if (isSelected) BentoPrimary else BentoBorder.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(prof.colorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = prof.name.take(2).uppercase(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = prof.name.split(" ").firstOrNull() ?: prof.name,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = GoldLoyalty,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    text = String.format(Locale.getDefault(), "%.1f", avgRating),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Select Date
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "2. Escolha o Dia",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dateOptions.forEach { (dateStr, label) ->
                    val isSelected = selectedDate == dateStr
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedDate = dateStr },
                        color = if (isSelected) BentoPrimary else BentoSurfaceVariant,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BentoOnPrimary else BentoTextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // 3. Select Time
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "3. Horário Disponível",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                timeSlots.forEach { time ->
                    val isSelected = selectedTime == time
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { selectedTime = time },
                        color = if (isSelected) BentoPrimary else BentoSurfaceVariant,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = time,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) BentoOnPrimary else BentoTextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // 4. Payment Option
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "4. Forma de Pagamento",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("SALAO", "No Salão", "Pague após o serviço"),
                    Triple("PIX", "Pix Online", "Aprovação instantânea"),
                    Triple("CARTAO", "Cartão", "Crédito ou Débito")
                ).forEach { (key, title, subtitle) ->
                    val isSelected = paymentMethod == key
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { paymentMethod = key },
                        color = if (isSelected) BentoPrimaryContainer else BentoSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            if (isSelected) 1.5.dp else 0.5.dp,
                            if (isSelected) BentoPrimary else BentoBorder.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                color = BentoTextSecondary,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Optional Notes
        OutlinedTextField(
            value = clientNotes,
            onValueChange = { clientNotes = it },
            placeholder = { Text("Alguma preferência especial? (opcional)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            maxLines = 2,
            singleLine = true
        )

        // Confirm Button
        Button(
            onClick = {
                val prof = selectedProf ?: activeProfessionals.first()
                onConfirm(prof, selectedDate, selectedTime, clientNotes, paymentMethod)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("confirm_client_booking_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
        ) {
            Text(
                text = "Confirmar Agendamento",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BentoOnPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
