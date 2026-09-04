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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notes
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
import com.example.ui.theme.CrimsonExpense
import com.example.ui.theme.EmeraldSuccess

@Composable
fun StockMovementDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (
        productId: Long,
        type: String,
        quantity: Int,
        reason: String,
        notes: String
    ) -> Unit
) {
    var type by remember { mutableStateOf("ENTRADA") } // "ENTRADA" or "SAIDA"
    var quantityText by remember { mutableStateOf("1") }
    val entradaReasons = listOf("Compra Fornecedor", "Devolução", "Ajuste de Saldo")
    val saidaReasons = listOf("Uso em Procedimento", "Venda Balcão", "Perda / Avaria", "Ajuste de Saldo")
    var selectedReason by remember { mutableStateOf(if (type == "ENTRADA") entradaReasons[0] else saidaReasons[0]) }
    var notes by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                .testTag("dialog_stock_movement"),
            color = BentoSurface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Movimentação de Estoque",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = product.name,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "Saldo Atual: ${product.currentStock} ${product.unit} (Mínimo: ${product.minStockAlert})",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoTextSecondary
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_movement_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = BentoTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Type Switcher (ENTRADA vs SAÍDA)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BentoSurfaceSubtle)
                        .padding(4.dp)
                ) {
                    val isEntrada = type == "ENTRADA"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isEntrada) EmeraldSuccess else Color.Transparent)
                            .clickable {
                                type = "ENTRADA"
                                selectedReason = entradaReasons[0]
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = if (isEntrada) Color.White else BentoTextSecondary,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = "Entrada (+)",
                                fontWeight = FontWeight.Bold,
                                color = if (isEntrada) Color.White else BentoTextSecondary
                            )
                        }
                    }

                    val isSaida = type == "SAIDA"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSaida) CrimsonExpense else Color.Transparent)
                            .clickable {
                                type = "SAIDA"
                                selectedReason = saidaReasons[0]
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ArrowUpward,
                                contentDescription = null,
                                tint = if (isSaida) Color.White else BentoTextSecondary,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = "Saída (-)",
                                fontWeight = FontWeight.Bold,
                                color = if (isSaida) Color.White else BentoTextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Quantity
                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it; errorMessage = null },
                    label = { Text("Quantidade (${product.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_movement_quantity"),
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

                // Reason Selection
                Text(
                    text = "Motivo da Movimentação",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = BentoTextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
                val currentReasons = if (type == "ENTRADA") entradaReasons else saidaReasons
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    currentReasons.forEach { reason ->
                        val isSelected = selectedReason == reason
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) BentoSurfaceVariant else BentoSurfaceSubtle)
                                .border(1.dp, if (isSelected) BentoPrimary else BentoBorderLight, RoundedCornerShape(12.dp))
                                .clickable { selectedReason = reason }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = reason,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) BentoPrimary else BentoTextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Notes / Reference
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Observação ou Referência (Opcional)") },
                    placeholder = { Text("ex: Nota Fiscal 1234, Uso na bancada 2") },
                    leadingIcon = { Icon(Icons.Default.Notes, contentDescription = null, tint = BentoTextSecondary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_movement_notes"),
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

                Spacer(modifier = Modifier.height(18.dp))

                // Confirm Button
                Button(
                    onClick = {
                        val qty = quantityText.toIntOrNull()
                        if (qty == null || qty <= 0) {
                            errorMessage = "Informe uma quantidade válida maior que zero."
                            return@Button
                        }
                        if (type == "SAIDA" && qty > product.currentStock) {
                            errorMessage = "Quantidade de saída ($qty) é maior que o saldo atual (${product.currentStock})."
                            return@Button
                        }

                        onConfirm(
                            product.id,
                            type,
                            qty,
                            selectedReason,
                            notes.trim()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("btn_confirm_stock_movement"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == "ENTRADA") EmeraldSuccess else CrimsonExpense
                    )
                ) {
                    Text(
                        text = if (type == "ENTRADA") "Confirmar Entrada no Estoque" else "Confirmar Saída do Estoque",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
