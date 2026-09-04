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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.Product
import com.example.data.model.StockMovement
import com.example.ui.SalonViewModel
import com.example.ui.components.ProductDialog
import com.example.ui.components.StockMovementDialog
import com.example.ui.theme.AmberWarning
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
import com.example.ui.theme.CrimsonExpense
import com.example.ui.theme.EmeraldSuccess
import java.util.Locale

@Composable
fun InventoryScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.filteredProducts.collectAsStateWithLifecycle()
    val allProducts by viewModel.allProducts.collectAsStateWithLifecycle()
    val movements by viewModel.allStockMovements.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedProductCategory.collectAsStateWithLifecycle()
    val stockFilter by viewModel.inventoryStockFilter.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val totalInventoryValue by viewModel.totalInventoryValue.collectAsStateWithLifecycle()

    val isProductDialogVisible by viewModel.isProductDialogVisible.collectAsStateWithLifecycle()
    val editingProduct by viewModel.editingProduct.collectAsStateWithLifecycle()
    val isMovementDialogVisible by viewModel.isStockMovementDialogVisible.collectAsStateWithLifecycle()
    val selectedProductForMovement by viewModel.selectedProductForMovement.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("PRODUTOS") } // "PRODUTOS" or "MOVIMENTACOES"

    val categories = listOf("TODOS", "Capilar", "Coloração", "Esmaltes", "Barba", "Pele & Estética", "Descartáveis")

    Scaffold(
        containerColor = BentoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openNewProductDialog() },
                containerColor = BentoPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.testTag("fab_new_product")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Produto")
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
            // Bento Metrics Header
            item {
                BentoInventoryMetrics(
                    totalProducts = allProducts.size,
                    totalValue = totalInventoryValue,
                    lowStockCount = lowStockCount,
                    onShowLowStock = {
                        viewModel.setInventoryStockFilter(
                            if (stockFilter == "BAIXO_ESTOQUE") "TODOS" else "BAIXO_ESTOQUE"
                        )
                    },
                    isLowStockFilterActive = stockFilter == "BAIXO_ESTOQUE",
                    onAddProduct = { viewModel.openNewProductDialog() }
                )
            }

            // View Switcher (Produtos vs Movimentações)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(BentoSurfaceSubtle)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == "PRODUTOS") BentoPrimary else Color.Transparent)
                            .clickable { activeTab = "PRODUTOS" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Inventory2,
                                contentDescription = null,
                                tint = if (activeTab == "PRODUTOS") Color.White else BentoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Itens em Estoque (${allProducts.size})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (activeTab == "PRODUTOS") Color.White else BentoTextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (activeTab == "MOVIMENTACOES") BentoPrimary else Color.Transparent)
                            .clickable { activeTab = "MOVIMENTACOES" }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.History,
                                contentDescription = null,
                                tint = if (activeTab == "MOVIMENTACOES") Color.White else BentoTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Movimentações (${movements.size})",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (activeTab == "MOVIMENTACOES") Color.White else BentoTextSecondary
                            )
                        }
                    }
                }
            }

            if (activeTab == "PRODUTOS") {
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
                                    .clickable { viewModel.setProductCategory(cat) }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .testTag("chip_product_cat_$cat")
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

                // Products List
                if (products.isEmpty()) {
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
                                    painter = painterResource(id = R.drawable.ic_inventory_stock),
                                    contentDescription = null,
                                    tint = BentoTextSecondary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (stockFilter == "BAIXO_ESTOQUE") "Nenhum produto abaixo do estoque mínimo!" else "Nenhum produto encontrado",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BentoTextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (stockFilter == "BAIXO_ESTOQUE") "Seu estoque está em níveis seguros e saudáveis." else "Cadastre os produtos e insumos do seu salão",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }
                } else {
                    items(products, key = { it.id }) { product ->
                        BentoProductCard(
                            product = product,
                            onMovement = { viewModel.openStockMovementDialog(product) },
                            onEdit = { viewModel.openEditProductDialog(product) },
                            onDelete = { viewModel.deleteProduct(product) }
                        )
                    }
                }
            } else {
                // Stock Movements History View
                if (movements.isEmpty()) {
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
                            Text(
                                text = "Nenhuma movimentação registrada até o momento.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BentoTextSecondary
                            )
                        }
                    }
                } else {
                    items(movements, key = { it.id }) { movement ->
                        BentoStockMovementCard(movement = movement)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }

    if (isProductDialogVisible) {
        ProductDialog(
            product = editingProduct,
            onDismiss = { viewModel.closeProductDialog() },
            onSave = { name, category, sku, currentStock, minStockAlert, costPrice, salePrice, unit, supplier ->
                viewModel.saveProduct(
                    name,
                    category,
                    sku,
                    currentStock,
                    minStockAlert,
                    costPrice,
                    salePrice,
                    unit,
                    supplier
                )
            }
        )
    }

    if (isMovementDialogVisible && selectedProductForMovement != null) {
        StockMovementDialog(
            product = selectedProductForMovement!!,
            onDismiss = { viewModel.closeStockMovementDialog() },
            onConfirm = { productId, type, quantity, reason, notes ->
                viewModel.recordStockMovement(productId, type, quantity, reason, notes)
            }
        )
    }
}

@Composable
fun BentoInventoryMetrics(
    totalProducts: Int,
    totalValue: Double,
    lowStockCount: Int,
    onShowLowStock: () -> Unit,
    isLowStockFilterActive: Boolean,
    onAddProduct: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Main Bento Card: Value & Total
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_inventory_metrics"),
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
                            text = "Controle de Estoque & Insumos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "$totalProducts itens controlados em tempo real",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoTextSecondary
                        )
                    }

                    Button(
                        onClick = onAddProduct,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                        modifier = Modifier.testTag("btn_add_product_header")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Novo Item", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Valor em Estoque
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BentoSurfaceSubtle)
                            .border(1.dp, BentoBorderLight, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "Valor em Estoque",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "R$ ${String.format(Locale.getDefault(), "%.2f", totalValue)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimary
                            )
                        }
                    }

                    // Total Itens
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BentoSurfaceSubtle)
                            .border(1.dp, BentoBorderLight, RoundedCornerShape(16.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = "Total de SKUs",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$totalProducts produtos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Low Stock Alert Banner (Bento Reorder Alert)
        if (lowStockCount > 0) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isLowStockFilterActive) AmberWarning.copy(alpha = 0.25f) else AmberWarning.copy(alpha = 0.12f))
                    .border(
                        1.dp,
                        if (isLowStockFilterActive) AmberWarning else AmberWarning.copy(alpha = 0.4f),
                        RoundedCornerShape(18.dp)
                    )
                    .clickable { onShowLowStock() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .testTag("banner_low_stock_alert")
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
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AmberWarning),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Alerta de Reposição ($lowStockCount produtos)",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            Text(
                                text = "Itens atingiram ou estão abaixo do estoque mínimo",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoTextSecondary
                            )
                        }
                    }

                    Text(
                        text = if (isLowStockFilterActive) "Ver Todos" else "Filtrar",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun BentoProductCard(
    product: Product,
    onMovement: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isLow = product.isLowStock

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_product_${product.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isLow) AmberWarning.copy(alpha = 0.6f) else BentoBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Top Row: Category + Stock Status Badge + Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(BentoPrimaryContainer)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        )
                    }

                    // Stock Alert Badge
                    if (isLow) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AmberWarning.copy(alpha = 0.2f))
                                .border(1.dp, AmberWarning, RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "REPOSIÇÃO NECESSÁRIA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = AmberWarning
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(EmeraldSuccess.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "EM DIA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess
                            )
                        }
                    }
                }

                Row {
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_edit_product_${product.id}")
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
                            .testTag("btn_delete_product_${product.id}")
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

            Spacer(modifier = Modifier.height(10.dp))

            // Product Name & SKU / Supplier
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )

            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (product.sku.isNotBlank()) {
                    Text(
                        text = "SKU: ${product.sku}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                }
                if (product.supplier.isNotBlank()) {
                    Text(
                        text = "• ${product.supplier}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stock Details Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BentoSurfaceSubtle)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Saldo Atual",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                    Text(
                        text = "${product.currentStock} ${product.unit}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isLow) AmberWarning else EmeraldSuccess
                    )
                }

                Column {
                    Text(
                        text = "Ponto Reposição",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                    Text(
                        text = "${product.minStockAlert} ${product.unit}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextPrimary
                    )
                }

                Column {
                    Text(
                        text = "Custo Unitário",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                    Text(
                        text = "R$ ${String.format(Locale.getDefault(), "%.2f", product.costPrice)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = BentoTextPrimary
                    )
                }

                if (product.salePrice > 0.0) {
                    Column {
                        Text(
                            text = "Preço Venda",
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoTextSecondary
                        )
                        Text(
                            text = "R$ ${String.format(Locale.getDefault(), "%.2f", product.salePrice)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Button: Registrar Movimentação
            Button(
                onClick = onMovement,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimaryContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_movement_product_${product.id}")
            ) {
                Icon(
                    Icons.Default.SwapVert,
                    contentDescription = null,
                    tint = BentoPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Registrar Entrada / Saída de Estoque",
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary
                )
            }
        }
    }
}

@Composable
fun BentoStockMovementCard(movement: StockMovement) {
    val isEntrada = movement.type == "ENTRADA"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("card_movement_${movement.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BentoBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isEntrada) EmeraldSuccess.copy(alpha = 0.15f) else CrimsonExpense.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isEntrada) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (isEntrada) EmeraldSuccess else CrimsonExpense,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = movement.productName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "${movement.reason} • ${movement.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                    if (movement.notes.isNotBlank()) {
                        Text(
                            text = movement.notes,
                            style = MaterialTheme.typography.labelSmall,
                            color = BentoTextSecondary.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isEntrada) EmeraldSuccess else CrimsonExpense)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "${if (isEntrada) "+" else "-"}${movement.quantity}",
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
