package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.SalonService
import com.example.ui.SalonTab
import com.example.ui.SalonViewModel
import com.example.ui.components.ServiceDialog
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
import java.util.Locale

@Composable
fun CatalogScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val services by viewModel.filteredServices.collectAsStateWithLifecycle()
    val allServices by viewModel.services.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedServiceCategory.collectAsStateWithLifecycle()
    val isServiceDialogVisible by viewModel.isServiceDialogVisible.collectAsStateWithLifecycle()
    val editingService by viewModel.editingService.collectAsStateWithLifecycle()

    val categories = listOf("TODOS", "Cabelo", "Coloração", "Tratamentos", "Unhas", "Barba", "Estética")

    Scaffold(
        containerColor = BentoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openNewServiceDialog() },
                containerColor = BentoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("fab_new_service")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Serviço")
            }
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bento Summary Header
            item {
                BentoCatalogHeader(
                    totalServices = allServices.size,
                    onAddService = { viewModel.openNewServiceDialog() }
                )
            }

            // Category Filter Scroll
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = selectedCategory.equals(cat, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) BentoPrimary else BentoSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) BentoPrimary else BentoBorderLight,
                                    shape = RoundedCornerShape(14.dp)
                                )
                                .clickable { viewModel.setServiceCategory(cat) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .testTag("chip_category_$cat")
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else BentoTextPrimary
                            )
                        }
                    }
                }
            }

            // Empty state if filtered out
            if (services.isEmpty()) {
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
                                painter = painterResource(id = R.drawable.ic_catalog_spa),
                                contentDescription = null,
                                tint = BentoTextSecondary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Nenhum serviço nesta categoria",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = BentoTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Cadastre um novo procedimento pelo botão abaixo",
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(services, key = { it.id }) { service ->
                    BentoServiceCard(
                        service = service,
                        onSchedule = {
                            viewModel.preSelectedServiceForAppointment.value = service
                            viewModel.isNewAppointmentDialogVisible.value = true
                            viewModel.selectTab(SalonTab.SCHEDULE)
                        },
                        onEdit = { viewModel.openEditServiceDialog(service) },
                        onDelete = { viewModel.deleteService(service) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (isServiceDialogVisible) {
        ServiceDialog(
            service = editingService,
            onDismiss = { viewModel.closeServiceDialog() },
            onSave = { name, category, price, duration, points, desc, iconType ->
                viewModel.saveService(name, category, price, duration, points, desc, iconType)
            }
        )
    }
}

@Composable
fun BentoCatalogHeader(
    totalServices: Int,
    onAddService: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_catalog_header"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Catálogo de Serviços",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$totalServices procedimentos cadastrados com preços e duração",
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary
                )
            }

            Button(
                onClick = onAddService,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                modifier = Modifier.testTag("btn_add_service_header")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Novo", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BentoServiceCard(
    service: SalonService,
    onSchedule: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_service_${service.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Category Badge + Icon + Edit/Delete Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val iconRes = when (service.iconType) {
                        "haircut" -> R.drawable.ic_catalog_haircut
                        "coloring" -> R.drawable.ic_catalog_coloring
                        "treatment" -> R.drawable.ic_catalog_treatment
                        "manicure" -> R.drawable.ic_catalog_manicure
                        "barber" -> R.drawable.ic_catalog_barber
                        else -> R.drawable.ic_catalog_spa
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BentoPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = service.category,
                            tint = BentoPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BentoSurfaceSubtle)
                            .border(1.dp, BentoBorderLight, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = service.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        )
                    }
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_edit_service_${service.id}")
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Editar",
                            tint = BentoTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_delete_service_${service.id}")
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Excluir",
                            tint = BentoTextSecondary.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Service Title
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )

            // Description
            if (service.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = service.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bento Metric Chips: Duration & Points
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Duration
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BentoSurfaceSubtle)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = BentoTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${service.durationMinutes} min",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Medium,
                        color = BentoTextPrimary
                    )
                }

                // Loyalty Points
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(BentoSurfaceSubtle)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Loyalty,
                        contentDescription = null,
                        tint = EmeraldSuccess,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${service.loyaltyPointsEarned} pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = EmeraldSuccess
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Price & Schedule CTA
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
                    onClick = onSchedule,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer),
                    modifier = Modifier.testTag("btn_schedule_service_${service.id}")
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        tint = BentoPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Agendar",
                        fontWeight = FontWeight.Bold,
                        color = BentoPrimary
                    )
                }
            }
        }
    }
}
