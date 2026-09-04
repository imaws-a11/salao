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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ShoppingCart
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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.data.model.Product
import com.example.ui.CartItem
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
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientShopScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val cartTotal by viewModel.cartTotal.collectAsState()
    val cartItemCount by viewModel.cartItemCount.collectAsState()
    val activeClient by viewModel.activeClient.collectAsState()

    var selectedCategory by remember { mutableStateOf("TODOS") }
    var searchQuery by remember { mutableStateOf("") }
    var isCartOpen by remember { mutableStateOf(false) }

    // Only sell products with positive sale price and positive stock
    val sellableProducts = remember(allProducts) {
        allProducts.filter { it.salePrice > 0 }
    }

    val categories = listOf("TODOS", "Capilar", "Coloração", "Esmaltes", "Pele & Estética")

    val filteredProducts = sellableProducts.filter { prod ->
        val matchesCategory = (selectedCategory == "TODOS" || prod.category.equals(selectedCategory, ignoreCase = true))
        val matchesSearch = searchQuery.isBlank() ||
                prod.name.contains(searchQuery, ignoreCase = true) ||
                prod.supplier.contains(searchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    Box(modifier = modifier.fillMaxSize().background(BentoBackground)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 100.dp)
        ) {
            // Header Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .testTag("client_shop_banner"),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BentoBorder.copy(alpha = 0.5f)))
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = BentoPrimaryContainer
                            ) {
                                Text(
                                    text = "LINHA PROFISSIONAL EXCLUSIVA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldLoyalty.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Gere pontos a cada compra",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Loja de Cosméticos do Salão",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "Leve os melhores tratamentos para a manutenção perfeita da sua beleza em casa.",
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
                        .testTag("client_shop_search_input"),
                    placeholder = { Text("Buscar shampoo, máscara, óleo, esmalte...", style = MaterialTheme.typography.bodyMedium) },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BentoSurface,
                        unfocusedContainerColor = BentoSurface,
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder.copy(alpha = 0.6f)
                    ),
                    singleLine = true
                )
            }

            // Category Chips
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

            // Products Count
            item {
                Text(
                    text = "Produtos Disponíveis (${filteredProducts.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
            }

            // Product Cards
            items(filteredProducts, key = { it.id }) { product ->
                val inCartItem = cartItems.firstOrNull { it.product.id == product.id }
                ClientProductBentoCard(
                    product = product,
                    cartQuantity = inCartItem?.quantity ?: 0,
                    onAddToCart = { viewModel.addToCart(product) }
                )
            }
        }

        // Floating Cart Indicator at the bottom
        AnimatedVisibility(
            visible = cartItemCount > 0,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .clickable { isCartOpen = true }
                    .testTag("floating_cart_bar"),
                color = BentoPrimary,
                shadowElevation = 8.dp,
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(BentoOnPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = BentoOnPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "$cartItemCount item(ns) na sacola",
                                style = MaterialTheme.typography.labelMedium,
                                color = BentoOnPrimary.copy(alpha = 0.85f)
                            )
                            Text(
                                text = "R$ ${String.format(Locale.getDefault(), "%.2f", cartTotal)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = BentoOnPrimary
                            )
                        }
                    }

                    Button(
                        onClick = { isCartOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = BentoSurface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Ver Sacola",
                            fontWeight = FontWeight.Bold,
                            color = BentoPrimary
                        )
                    }
                }
            }
        }
    }

    // Cart Bottom Sheet
    if (isCartOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { isCartOpen = false },
            sheetState = sheetState,
            containerColor = BentoSurface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            ClientCartSheetContent(
                cartItems = cartItems,
                cartTotal = cartTotal,
                onUpdateQuantity = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                onRemoveItem = { id -> viewModel.removeFromCart(id) },
                onCheckout = { paymentMethod, isPickup, notes ->
                    viewModel.checkoutCart(paymentMethod, isPickup, notes)
                    isCartOpen = false
                },
                onClose = { isCartOpen = false }
            )
        }
    }
}

@Composable
fun ClientProductBentoCard(
    product: Product,
    cartQuantity: Int,
    onAddToCart: () -> Unit
) {
    val pointsToEarn = (product.salePrice / 10).toInt().coerceAtLeast(1)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("client_product_card_${product.id}"),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BentoBorder.copy(alpha = 0.5f)))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bento Product Icon Placeholder
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BentoSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = product.name.take(2).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary
                )
            }

            // Info Column
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BentoSurfaceVariant
                    ) {
                        Text(
                            text = product.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (product.currentStock > 0 && product.currentStock <= 3) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AmberWarning.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Últimas ${product.currentStock} un",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberWarning,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    } else if (product.currentStock > 0) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = EmeraldSuccess.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "Em estoque",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldSuccess,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (product.supplier.isNotBlank()) {
                    Text(
                        text = product.supplier,
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "R$ ${String.format(Locale.getDefault(), "%.2f", product.salePrice)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = BentoPrimary
                        )
                        Text(
                            text = "+$pointsToEarn pts fidelidade",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = GoldLoyalty,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = onAddToCart,
                        enabled = product.currentStock > 0,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                        modifier = Modifier.testTag("add_product_button_${product.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = if (cartQuantity > 0) "Na Sacola ($cartQuantity)" else "Adicionar",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ClientCartSheetContent(
    cartItems: List<CartItem>,
    cartTotal: Double,
    onUpdateQuantity: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onCheckout: (String, Boolean, String) -> Unit,
    onClose: () -> Unit
) {
    var paymentMethod by remember { mutableStateOf("PIX") } // PIX, CARTAO
    var isPickup by remember { mutableStateOf(true) }
    var addressOrNotes by remember { mutableStateOf("") }
    val pointsGained = (cartTotal / 10).toInt().coerceAtLeast(1)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Sua Sacola de Compras",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
                Text(
                    text = "${cartItems.sumOf { it.quantity }} item(ns) selecionado(s)",
                    style = MaterialTheme.typography.labelSmall,
                    color = BentoTextSecondary
                )
            }
            IconButton(onClick = onClose) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Fechar")
            }
        }

        // Cart items list
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height((cartItems.size * 70).coerceAtMost(220).dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(cartItems, key = { it.product.id }) { item ->
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BentoSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = BentoTextPrimary
                            )
                            Text(
                                text = "R$ ${String.format(Locale.getDefault(), "%.2f", item.product.salePrice * item.quantity)}",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = BentoPrimary
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = { onUpdateQuantity(item.product.id, item.quantity - 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("-", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BentoPrimary)
                            }
                            Text(
                                text = "${item.quantity}",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = BentoTextPrimary
                            )
                            IconButton(
                                onClick = { onUpdateQuantity(item.product.id, item.quantity + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Text("+", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = BentoPrimary)
                            }
                            IconButton(
                                onClick = { onRemoveItem(item.product.id) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remover",
                                    tint = BentoTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Delivery choice
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Entrega / Retirada",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isPickup = true },
                    color = if (isPickup) BentoPrimaryContainer else BentoSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Retirar no Salão",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "Pronto em 1 hora (Grátis)",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = BentoTextSecondary
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { isPickup = false },
                    color = if (!isPickup) BentoPrimaryContainer else BentoSurfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Entrega Expressa",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "Motoboy no mesmo dia",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = BentoTextSecondary
                        )
                    }
                }
            }
        }

        // Payment Method
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "Pagamento",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { paymentMethod = "PIX" },
                    color = if (paymentMethod == "PIX") PixTeal.copy(alpha = 0.15f) else BentoSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (paymentMethod == "PIX") PixTeal else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Pix Online",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = BentoTextPrimary
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { paymentMethod = "CARTAO" },
                    color = if (paymentMethod == "CARTAO") BentoPrimaryContainer else BentoSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (paymentMethod == "CARTAO") BentoPrimary else Color.Transparent
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Cartão de Crédito",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = BentoTextPrimary
                        )
                    }
                }
            }
        }

        // Points Bonus Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = GoldLoyalty.copy(alpha = 0.15f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldLoyalty)
                Text(
                    text = "Você acumulará +$pointsGained pontos de fidelidade!",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )
            }
        }

        // Total and Finalize Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Total a Pagar",
                    style = MaterialTheme.typography.labelSmall,
                    color = BentoTextSecondary
                )
                Text(
                    text = "R$ ${String.format(Locale.getDefault(), "%.2f", cartTotal)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = BentoPrimary
                )
            }

            Button(
                onClick = { onCheckout(paymentMethod, isPickup, addressOrNotes) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                modifier = Modifier
                    .height(50.dp)
                    .testTag("checkout_order_button")
            ) {
                Text(
                    text = "Finalizar Pedido",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoOnPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
