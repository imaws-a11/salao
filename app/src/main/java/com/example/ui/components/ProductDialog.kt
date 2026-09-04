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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Product
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceSubtle
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary

@Composable
fun ProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (
        name: String,
        category: String,
        sku: String,
        currentStock: Int,
        minStockAlert: Int,
        costPrice: Double,
        salePrice: Double,
        unit: String,
        supplier: String
    ) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var selectedCategory by remember { mutableStateOf(product?.category ?: "Capilar") }
    var sku by remember { mutableStateOf(product?.sku ?: "") }
    var currentStockText by remember { mutableStateOf(product?.currentStock?.toString() ?: "5") }
    var minAlertText by remember { mutableStateOf(product?.minStockAlert?.toString() ?: "3") }
    var costPriceText by remember { mutableStateOf(product?.costPrice?.let { "%.2f".format(it) } ?: "") }
    var salePriceText by remember { mutableStateOf(product?.salePrice?.let { "%.2f".format(it) } ?: "") }
    var selectedUnit by remember { mutableStateOf(product?.unit ?: "un") }
    var supplier by remember { mutableStateOf(product?.supplier ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Capilar", "Coloração", "Esmaltes", "Barba", "Pele & Estética", "Descartáveis")
    val units = listOf("un", "ml", "kg", "kit", "cx")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                .testTag("dialog_product"),
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
                            text = if (product == null) "Novo Produto no Estoque" else "Editar Produto",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "Controle de saldo, ponto de reposição e custos",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoTextSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_product_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = BentoTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Product Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Nome do Produto") },
                    placeholder = { Text("ex: Shampoo Reconstrutor 1000ml") },
                    leadingIcon = { Icon(Icons.Default.Inventory2, contentDescription = null, tint = BentoPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_name"),
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
                    text = "Categoria do Produto",
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

                // SKU & Unit
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        label = { Text("Código / SKU") },
                        placeholder = { Text("WEL-SH-01") },
                        leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = BentoPrimary) },
                        modifier = Modifier
                            .weight(1.4f)
                            .testTag("input_product_sku"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedContainerColor = BentoSurfaceVariant,
                            unfocusedContainerColor = BentoSurfaceVariant
                        ),
                        singleLine = true
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Unidade",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoTextSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            units.forEach { u ->
                                val isSelected = selectedUnit == u
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) BentoPrimary else BentoSurfaceSubtle)
                                        .border(1.dp, if (isSelected) BentoPrimary else BentoBorderLight, RoundedCornerShape(8.dp))
                                        .clickable { selectedUnit = u }
                                        .padding(horizontal = 6.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = u,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) Color.White else BentoTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Stock & Reorder Point (Min Alert)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = currentStockText,
                        onValueChange = { currentStockText = it },
                        label = { Text("Estoque Atual") },
                        leadingIcon = { Icon(Icons.Default.Inventory2, contentDescription = null, tint = BentoPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_product_stock"),
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
                        value = minAlertText,
                        onValueChange = { minAlertText = it },
                        label = { Text("Alerta Reposição") },
                        leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, tint = BentoPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_product_min_stock"),
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

                // Cost Price & Sale Price
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = costPriceText,
                        onValueChange = { costPriceText = it },
                        label = { Text("Custo Unitário (R$)") },
                        placeholder = { Text("50.00") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = BentoPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_product_cost"),
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
                        value = salePriceText,
                        onValueChange = { salePriceText = it },
                        label = { Text("Preço Venda (R$)") },
                        placeholder = { Text("0.00 se uso interno") },
                        leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null, tint = BentoPrimary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_product_sale_price"),
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

                // Supplier
                OutlinedTextField(
                    value = supplier,
                    onValueChange = { supplier = it },
                    label = { Text("Fornecedor / Distribuidor") },
                    placeholder = { Text("ex: Wella Distribuidora SP") },
                    leadingIcon = { Icon(Icons.Default.Store, contentDescription = null, tint = BentoPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_supplier"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedContainerColor = BentoSurfaceVariant,
                        unfocusedContainerColor = BentoSurfaceVariant
                    ),
                    singleLine = true
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

                // Save Button
                Button(
                    onClick = {
                        if (name.isBlank()) {
                            errorMessage = "Por favor, insira o nome do produto."
                            return@Button
                        }
                        val stock = currentStockText.toIntOrNull() ?: 0
                        val minAlert = minAlertText.toIntOrNull() ?: 2
                        val cost = costPriceText.replace(",", ".").toDoubleOrNull() ?: 0.0
                        val sale = salePriceText.replace(",", ".").toDoubleOrNull() ?: 0.0

                        onSave(
                            name,
                            selectedCategory,
                            sku,
                            stock,
                            minAlert,
                            cost,
                            sale,
                            selectedUnit,
                            supplier
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_save_product"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                ) {
                    Text(
                        text = if (product == null) "Cadastrar Produto" else "Salvar Alterações",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
