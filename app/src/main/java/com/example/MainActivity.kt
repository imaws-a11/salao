package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.AppRole
import com.example.ui.ClientTab
import com.example.ui.SalonTab
import com.example.ui.SalonViewModel
import com.example.ui.dialogs.ClientProfileDialog
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.ClientAppointmentsScreen
import com.example.ui.screens.ClientAuthScreen
import com.example.ui.screens.ClientBookingScreen
import com.example.ui.screens.ClientLoyaltyScreen
import com.example.ui.screens.ClientShopScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.ManagementScreen
import com.example.ui.screens.ScheduleScreen
import com.example.ui.screens.TeamScreen
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoOnPrimary
import com.example.ui.theme.BentoOnPrimaryContainer
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoPrimaryContainer
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.GoldLoyalty
import com.example.ui.theme.MyApplicationTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                SalonAppRoot()
            }
        }
    }
}

@Composable
fun SalonAppRoot(
    viewModel: SalonViewModel = viewModel()
) {
    val currentRole by viewModel.currentRole.collectAsStateWithLifecycle()
    val currentAdminTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val currentClientTab by viewModel.currentClientTab.collectAsStateWithLifecycle()
    val activeClient by viewModel.activeClient.collectAsStateWithLifecycle()
    val cartItemCount by viewModel.cartItemCount.collectAsStateWithLifecycle()
    val isClientAuthenticated by viewModel.isClientAuthenticated.collectAsStateWithLifecycle()
    val clientAuthUser by viewModel.clientAuthUser.collectAsStateWithLifecycle()

    var showProfileDialog by remember { mutableStateOf(false) }

    val formattedDate = SimpleDateFormat("EEEE, dd MMM", Locale("pt", "BR"))
        .format(Date())
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

    Scaffold(
        topBar = {
            if (isClientAuthenticated) {
                // Bento Grid Styled Header
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BentoBackground)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "GlowUp Studio",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BentoPrimary
                                )
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (currentRole == AppRole.ADMIN) BentoPrimaryContainer else GoldLoyalty.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = if (currentRole == AppRole.ADMIN) "ADMIN" else "CLIENTE",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentRole == AppRole.ADMIN) BentoPrimary else BentoTextPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = when {
                                    currentRole == AppRole.ADMIN -> "Laura Ivini • $formattedDate"
                                    else -> "Olá, ${clientAuthUser?.displayName?.split(" ")?.firstOrNull() ?: activeClient.clientName.split(" ").firstOrNull() ?: "Cliente"} • ${activeClient.totalPoints} pts"
                                },
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoTextSecondary
                            )
                        }

                        // Right side: Avatar & Logout Button (NO role switch button)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Avatar Pill
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (currentRole == AppRole.ADMIN) BentoPrimaryContainer else GoldLoyalty.copy(alpha = 0.25f)
                                    )
                                    .clickable {
                                        if (currentRole == AppRole.CLIENT) {
                                            showProfileDialog = true
                                        }
                                    }
                                    .testTag("top_avatar_pill"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = when {
                                        currentRole == AppRole.ADMIN -> "LI"
                                        else -> (clientAuthUser?.displayName?.take(2) ?: activeClient.clientName.take(2)).uppercase()
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (currentRole == AppRole.ADMIN) BentoOnPrimaryContainer else BentoTextPrimary
                                )
                            }

                            // Logout Button
                            Surface(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .clickable {
                                        viewModel.logoutClient()
                                    }
                                    .testTag("top_logout_button"),
                                color = BentoSurfaceVariant,
                                shape = CircleShape
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Encerrar Sessão",
                                    tint = BentoTextSecondary,
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            if (isClientAuthenticated) {
                // Bento Grid Styled Navigation Bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(width = 1.dp, color = BentoBorder.copy(alpha = 0.5f))
                ) {
                if (currentRole == AppRole.ADMIN) {
                    // ==========================================
                    // ADMIN NAVIGATION BAR
                    // ==========================================
                    NavigationBar(
                        containerColor = BentoSurfaceVariant,
                        tonalElevation = 0.dp,
                        modifier = Modifier.testTag("admin_bottom_navigation")
                    ) {
                        NavigationBarItem(
                            selected = currentAdminTab == SalonTab.SCHEDULE,
                            onClick = { viewModel.selectTab(SalonTab.SCHEDULE) },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Agenda") },
                            label = { Text("Agenda", style = MaterialTheme.typography.labelSmall, fontWeight = if (currentAdminTab == SalonTab.SCHEDULE) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_schedule")
                        )

                        NavigationBarItem(
                            selected = currentAdminTab == SalonTab.SERVICES,
                            onClick = { viewModel.selectTab(SalonTab.SERVICES) },
                            icon = { Icon(Icons.Default.ContentCut, contentDescription = "Catálogo") },
                            label = { Text("Catálogo", style = MaterialTheme.typography.labelSmall, fontWeight = if (currentAdminTab == SalonTab.SERVICES) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_services")
                        )

                        NavigationBarItem(
                            selected = currentAdminTab == SalonTab.INVENTORY,
                            onClick = { viewModel.selectTab(SalonTab.INVENTORY) },
                            icon = { Icon(Icons.Default.Inventory2, contentDescription = "Estoque") },
                            label = { Text("Estoque", style = MaterialTheme.typography.labelSmall, fontWeight = if (currentAdminTab == SalonTab.INVENTORY) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_inventory")
                        )

                        NavigationBarItem(
                            selected = currentAdminTab == SalonTab.TEAM,
                            onClick = { viewModel.selectTab(SalonTab.TEAM) },
                            icon = { Icon(Icons.Default.Groups, contentDescription = "Equipe") },
                            label = { Text("Equipe", style = MaterialTheme.typography.labelSmall, fontWeight = if (currentAdminTab == SalonTab.TEAM) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_team")
                        )

                        val isManagementSelected = currentAdminTab == SalonTab.MANAGEMENT ||
                                currentAdminTab == SalonTab.FINANCE ||
                                currentAdminTab == SalonTab.REPORTS ||
                                currentAdminTab == SalonTab.LOYALTY_BACKUP

                        NavigationBarItem(
                            selected = isManagementSelected,
                            onClick = { viewModel.selectTab(SalonTab.MANAGEMENT) },
                            icon = { Icon(Icons.Default.Tune, contentDescription = "Gestão") },
                            label = { Text("Gestão", style = MaterialTheme.typography.labelSmall, fontWeight = if (isManagementSelected) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("nav_tab_management")
                        )
                    }
                } else {
                    // ==========================================
                    // CLIENT NAVIGATION BAR
                    // ==========================================
                    NavigationBar(
                        containerColor = BentoSurfaceVariant,
                        tonalElevation = 0.dp,
                        modifier = Modifier.testTag("client_bottom_navigation")
                    ) {
                        NavigationBarItem(
                            selected = currentClientTab == ClientTab.BOOKING,
                            onClick = { viewModel.selectClientTab(ClientTab.BOOKING) },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Agendar") },
                            label = { Text("Agendar", style = MaterialTheme.typography.labelSmall, fontWeight = if (currentClientTab == ClientTab.BOOKING) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("client_nav_booking")
                        )

                        NavigationBarItem(
                            selected = currentClientTab == ClientTab.SHOP,
                            onClick = { viewModel.selectClientTab(ClientTab.SHOP) },
                            icon = {
                                if (cartItemCount > 0) {
                                    BadgedBox(
                                        badge = {
                                            Badge(containerColor = BentoPrimary) {
                                                Text("$cartItemCount", color = BentoOnPrimary)
                                            }
                                        }
                                    ) {
                                        Icon(Icons.Default.ShoppingBag, contentDescription = "Loja")
                                    }
                                } else {
                                    Icon(Icons.Default.ShoppingBag, contentDescription = "Loja")
                                }
                            },
                            label = { Text("Loja", style = MaterialTheme.typography.labelSmall, fontWeight = if (currentClientTab == ClientTab.SHOP) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("client_nav_shop")
                        )

                        NavigationBarItem(
                            selected = currentClientTab == ClientTab.MY_APPOINTMENTS,
                            onClick = { viewModel.selectClientTab(ClientTab.MY_APPOINTMENTS) },
                            icon = { Icon(Icons.Default.EventNote, contentDescription = "Meus Horários") },
                            label = { Text("Meus Horários", style = MaterialTheme.typography.labelSmall, fontWeight = if (currentClientTab == ClientTab.MY_APPOINTMENTS) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("client_nav_appointments")
                        )

                        NavigationBarItem(
                            selected = currentClientTab == ClientTab.LOYALTY,
                            onClick = { viewModel.selectClientTab(ClientTab.LOYALTY) },
                            icon = { Icon(Icons.Default.Star, contentDescription = "Fidelidade") },
                            label = { Text("Fidelidade", style = MaterialTheme.typography.labelSmall, fontWeight = if (currentClientTab == ClientTab.LOYALTY) FontWeight.Bold else FontWeight.Normal) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = BentoOnPrimaryContainer,
                                selectedTextColor = BentoOnPrimaryContainer,
                                indicatorColor = BentoPrimaryContainer,
                                unselectedIconColor = BentoTextSecondary,
                                unselectedTextColor = BentoTextSecondary
                            ),
                            modifier = Modifier.testTag("client_nav_loyalty")
                        )
                    }
                }
            }
            }
        },
        containerColor = BentoBackground,
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
            ) {
                if (!isClientAuthenticated) {
                    ClientAuthScreen(viewModel = viewModel)
                } else if (currentRole == AppRole.ADMIN) {
                    Crossfade(targetState = currentAdminTab, label = "AdminTabTransition") { tab ->
                        when (tab) {
                            SalonTab.SCHEDULE -> ScheduleScreen(viewModel = viewModel)
                            SalonTab.SERVICES -> CatalogScreen(viewModel = viewModel)
                            SalonTab.INVENTORY -> InventoryScreen(viewModel = viewModel)
                            SalonTab.TEAM -> TeamScreen(viewModel = viewModel)
                            SalonTab.MANAGEMENT -> ManagementScreen(viewModel = viewModel)
                            SalonTab.FINANCE -> ManagementScreen(viewModel = viewModel, initialSubTab = "FINANCEIRO")
                            SalonTab.REPORTS -> ManagementScreen(viewModel = viewModel, initialSubTab = "RELATORIOS")
                            SalonTab.LOYALTY_BACKUP -> ManagementScreen(viewModel = viewModel, initialSubTab = "FIDELIDADE")
                        }
                    }
                } else {
                    Crossfade(targetState = currentClientTab, label = "ClientTabTransition") { tab ->
                        when (tab) {
                            ClientTab.BOOKING -> ClientBookingScreen(
                                viewModel = viewModel,
                                onOpenProfileSwitcher = { showProfileDialog = true }
                            )
                            ClientTab.SHOP -> ClientShopScreen(viewModel = viewModel)
                            ClientTab.MY_APPOINTMENTS -> ClientAppointmentsScreen(viewModel = viewModel)
                            ClientTab.LOYALTY -> ClientLoyaltyScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }

    // Client Profile Switcher Dialog
    if (showProfileDialog) {
        ClientProfileDialog(
            viewModel = viewModel,
            onDismiss = { showProfileDialog = false }
        )
    }
}
