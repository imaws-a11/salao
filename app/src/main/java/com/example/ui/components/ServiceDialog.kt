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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.data.model.SalonService
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceSubtle
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary

@Composable
fun ServiceDialog(
    service: SalonService?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        category: String,
        price: Double,
        durationMinutes: Int,
        points: Int,
        description: String,
        iconType: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(service?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(service?.category ?: "Cabelo") }
    var priceText by remember { mutableStateOf(service?.price?.let { "%.2f".format(it) } ?: "") }
    var durationMinutes by remember { mutableIntStateOf(service?.durationMinutes ?: 60) }
    var pointsText by remember { mutableStateOf(service?.loyaltyPointsEarned?.toString() ?: "10") }
    var description by remember { mutableStateOf(service?.description ?: "") }
    var selectedIconType by remember { mutableStateOf(service?.iconType ?: "haircut") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Cabelo", "Coloração", "Tratamentos", "Unhas", "Barba", "Estética")
    val iconTypes = listOf(
        Pair("haircut", "Corte"),
        Pair("coloring", "Color"),
        Pair("treatment", "Tratamento"),
        Pair("manicure", "Unhas"),
        Pair("barber", "Barba"),
        Pair("spa", "Estética")
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                .testTag("dialog_service"),
            color = BentoSurface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (service == null) "Novo Serviço" else "Editar Serviço",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "Cadastre no catálogo com foto e detalhes",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoTextSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_service_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = BentoTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Service Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Nome do Serviço") },
                    placeholder = { Text("ex: Corte & Escova Modelada") },
                    leadingIcon = { Icon(Icons.Default.ContentCut, contentDescription = null, tint = BentoPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_service_name"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedContainerColor = BentoSurfaceVariant,
                        unfocusedContainerColor = BentoSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Category Selection
                Text(
                    text = "Categoria",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) BentoPrimary else BentoSurfaceSubtle)
                                .border(1.dp, if (isSelected) BentoPrimary else BentoBorderLight, RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else BentoTextPrimary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.drop(3).forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) BentoPrimary else BentoSurfaceSubtle)
                                .border(1.dp, if (isSelected) BentoPrimary else BentoBorderLight, RoundedCornerShape(12.dp))
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else BentoTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Icon / Illustration Selection
                Text(
                    text = "Ícone Visual Ilustrativo",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    iconTypes.forEach { (typeKey, label) ->
                        val isSelected = selectedIconType == typeKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isSelected) BentoPrimaryContainer else BentoSurfaceSubtle)
                                .border(1.dp, if (isSelected) BentoPrimary else BentoBorderLight, RoundedCornerShape(14.dp))
                                .clickable { selectedIconType = typeKey }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val iconRes = when (typeKey) {
                                    "haircut" -> R.drawable.ic_catalog_haircut
                                    "coloring" -> R.drawable.ic_catalog_coloring
                                    "treatment" -> R.drawable.ic_catalog_treatment
                                    "manicure" -> R.drawable.ic_catalog_manicure
                                    "barber" -> R.drawable.ic_catalog_barber
                                    else -> R.drawable.ic_catalog_spa
                                }
                                Icon(
                                    painter = painterResource(id = iconRes),
                                    contentDescription = label,
                                    modifier = Modifier.size(24.dp),
                                    tint = if (isSelected) BentoPrimary else BentoTextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 10.sp,
                                    color = if (isSelected) BentoPrimary else BentoTextSecondary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price & Duration
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text("Preço (R$)") },
                        placeholder = { Text("120.00") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = BentoPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_service_price"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedContainerColor = BentoSurfaceVariant,
                            unfocusedContainerColor = BentoSurfaceVariant
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = durationMinutes.toString(),
                        onValueChange = { durationMinutes = it.toIntOrNull() ?: 30 },
                        label = { Text("Duração (min)") },
                        leadingIcon = { Icon(Icons.Default.AccessTime, contentDescription = null, tint = BentoPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_service_duration"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedContainerColor = BentoSurfaceVariant,
                            unfocusedContainerColor = BentoSurfaceVariant
                        ),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Loyalty Points Earned
                OutlinedTextField(
                    value = pointsText,
                    onValueChange = { pointsText = it },
                    label = { Text("Pontos de Fidelidade Concedidos") },
                    placeholder = { Text("10") },
                    leadingIcon = { Icon(Icons.Default.Loyalty, contentDescription = null, tint = BentoPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_service_points"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedContainerColor = BentoSurfaceVariant,
                        unfocusedContainerColor = BentoSurfaceVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Detailed Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição Detalhada do Serviço") },
                    placeholder = { Text("Explique o que inclui, benefícios e diferenciais do procedimento...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("input_service_description"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedContainerColor = BentoSurfaceVariant,
                        unfocusedContainerColor = BentoSurfaceVariant
                    ),
                    maxLines = 4
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Por favor, insira o nome do serviço."
                            return@Button
                        }
                        val priceClean = priceText.replace(",", ".").toDoubleOrNull()
                        if (priceClean == null || priceClean <= 0.0) {
                            errorMessage = "Por favor, insira um preço válido maior que zero."
                            return@Button
                        }
                        val points = pointsText.toIntOrNull() ?: (priceClean / 10).toInt().coerceAtLeast(5)

                        onSave(
                            name,
                            selectedCategory,
                            priceClean,
                            durationMinutes,
                            points,
                            description,
                            selectedIconType
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_save_service"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                ) {
                    Text(
                        text = if (service == null) "Cadastrar no Catálogo" else "Salvar Alterações",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
