package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.EmeraldSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTransactionDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        description: String,
        amount: Double,
        type: String,
        category: String,
        paymentMethod: String,
        date: String
    ) -> Unit
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var type by remember { mutableStateOf("DESPESA") } // RECEITA or DESPESA
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("PRODUTOS") }
    var paymentMethod by remember { mutableStateOf("PIX") }
    var date by remember { mutableStateOf(today) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var paymentMethodDropdownExpanded by remember { mutableStateOf(false) }

    val categories = if (type == "RECEITA") {
        listOf("SERVICO", "PRODUTOS", "CURSOS", "OUTROS")
    } else {
        listOf("PRODUTOS", "ALUGUEL", "COMISSAO", "CONTAS", "FORNECEDOR", "MARKETING", "OUTROS")
    }

    val paymentMethods = listOf("PIX", "CARTAO_CREDITO", "CARTAO_DEBITO", "DINHEIRO")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("new_transaction_modal"),
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
                        text = "Novo Lançamento Financeiro",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_transaction_button")) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Type selection (Receita / Despesa)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilterChip(
                        selected = type == "DESPESA",
                        onClick = {
                            type = "DESPESA"
                            category = "PRODUTOS"
                        },
                        label = { Text("Despesa / Saída", fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.errorContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onErrorContainer
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    FilterChip(
                        selected = type == "RECEITA",
                        onClick = {
                            type = "RECEITA"
                            category = "SERVICO"
                        },
                        label = { Text("Receita / Entrada", fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldSuccess.copy(alpha = 0.2f),
                            selectedLabelColor = EmeraldSuccess
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Amount
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valor (R$)") },
                    leadingIcon = { Icon(Icons.Default.AttachMoney, contentDescription = null) },
                    placeholder = { Text("0,00") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("amount_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descrição do Lançamento") },
                    placeholder = { Text(if (type == "RECEITA") "Ex: Atendimento Vip, Venda Kit" else "Ex: Tinturas Wella, Conta de Luz") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("description_input")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Dropdown
                ExposedDropdownMenuBox(
                    expanded = paymentMethodDropdownExpanded,
                    onExpandedChange = { paymentMethodDropdownExpanded = !paymentMethodDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = paymentMethod.replace("_", " "),
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Forma de Pagamento") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentMethodDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = paymentMethodDropdownExpanded,
                        onDismissRequest = { paymentMethodDropdownExpanded = false }
                    ) {
                        paymentMethods.forEach { meth ->
                            DropdownMenuItem(
                                text = { Text(meth.replace("_", " ")) },
                                onClick = {
                                    paymentMethod = meth
                                    paymentMethodDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Date
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Data do Lançamento (AAAA-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                val parsedAmount = amountText.replace(",", ".").toDoubleOrNull() ?: 0.0

                Button(
                    onClick = {
                        if (parsedAmount > 0 && description.isNotBlank()) {
                            onConfirm(description, parsedAmount, type, category, paymentMethod, date)
                        }
                    },
                    enabled = parsedAmount > 0 && description.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (type == "RECEITA") EmeraldSuccess else MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_transaction_button")
                ) {
                    Text("Salvar Lançamento", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
