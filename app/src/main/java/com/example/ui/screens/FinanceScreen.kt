package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FinancialTransaction
import com.example.ui.SalonViewModel
import com.example.ui.components.NewTransactionDialog
import com.example.ui.theme.BentoBackground
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
import java.util.Locale

@Composable
fun FinanceScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val filter by viewModel.financeMonthFilter.collectAsStateWithLifecycle()
    val isNewTransactionVisible by viewModel.isNewTransactionDialogVisible.collectAsStateWithLifecycle()

    val totalRevenue = transactions.filter { it.type == "RECEITA" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "DESPESA" }.sumOf { it.amount }
    val netBalance = totalRevenue - totalExpense
    val margin = if (totalRevenue > 0) (netBalance / totalRevenue) * 100 else 0.0

    Scaffold(
        containerColor = BentoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.isNewTransactionDialogVisible.value = true },
                containerColor = BentoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("fab_new_transaction")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nova Transação")
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
            // Bento Tile 1: Saldo Líquido Operacional
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(26.dp))
                        .background(BentoPrimaryContainer)
                        .padding(18.dp)
                ) {
                    Column(verticalArrangement = Arrangement.SpaceBetween) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SALDO LÍQUIDO",
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
                                    text = "${String.format(Locale.getDefault(), "%.0f", margin)}% margem",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "R$ ${String.format(Locale.getDefault(), "%.2f", netBalance)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = BentoOnPrimaryContainer
                        )
                    }
                }
            }

            // Bento Tiles 2 & 3: Receitas vs Despesas (2 columns)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Receitas Tile
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(BentoSurface)
                            .border(1.dp, BentoBorderLight, RoundedCornerShape(22.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldSuccess.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ArrowUpward, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = "RECEITAS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = EmeraldSuccess
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "R$ ${String.format(Locale.getDefault(), "%.2f", totalRevenue)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = BentoTextPrimary
                            )
                        }
                    }

                    // Despesas Tile
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(22.dp))
                            .background(BentoSurfaceVariant)
                            .border(1.dp, BentoBorder.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.error.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.ArrowDownward, contentDescription = null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = "DESPESAS",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "R$ ${String.format(Locale.getDefault(), "%.2f", totalExpense)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = BentoTextPrimary
                            )
                        }
                    }
                }
            }

            // Period Filter Chips
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "ESTE_MES" to "Este Mês",
                        "MES_ANTERIOR" to "Mês Anterior",
                        "TODOS" to "Todos"
                    ).forEach { (key, label) ->
                        FilterChip(
                            selected = filter == key,
                            onClick = { viewModel.setFinanceMonthFilter(key) },
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
                            modifier = Modifier.testTag("finance_filter_$key")
                        )
                    }
                }
            }

            // Header
            item {
                Text(
                    text = "Lançamentos (${transactions.size})",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
            }

            // Transactions
            if (transactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(BentoSurface)
                            .border(1.dp, BentoBorderLight, RoundedCornerShape(24.dp))
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.MoneyOff, contentDescription = null, modifier = Modifier.size(44.dp), tint = BentoBorder)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Nenhum lançamento no período", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                items(transactions, key = { it.id }) { tx ->
                    BentoTransactionItem(
                        transaction = tx,
                        onDelete = { viewModel.deleteTransaction(tx) }
                    )
                }
            }
        }
    }

    if (isNewTransactionVisible) {
        NewTransactionDialog(
            onDismiss = { viewModel.isNewTransactionDialogVisible.value = false },
            onConfirm = { desc, amount, type, cat, method, date ->
                viewModel.addTransaction(desc, amount, type, cat, method, date)
            }
        )
    }
}

@Composable
fun BentoTransactionItem(
    transaction: FinancialTransaction,
    onDelete: () -> Unit
) {
    val isRevenue = transaction.type == "RECEITA"
    val accentColor = if (isRevenue) EmeraldSuccess else MaterialTheme.colorScheme.error

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(BentoSurface)
            .border(1.dp, BentoBorderLight, RoundedCornerShape(20.dp))
            .padding(14.dp)
            .testTag("transaction_item_${transaction.id}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isRevenue) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaction.description,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = BentoSurfaceSubtle,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = transaction.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoTextSecondary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "• ${transaction.date}",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoTextSecondary
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${if (isRevenue) "+" else "-"} R$ ${String.format(Locale.getDefault(), "%.2f", transaction.amount)}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = accentColor
                )

                IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = BentoBorder,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
