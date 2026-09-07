package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ClientLoyalty
import com.example.data.model.LoyaltyReward
import com.example.ui.SalonViewModel
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBlueAccent
import com.example.ui.theme.BentoBlueDark
import com.example.ui.theme.BentoBlueOnAccent
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
import com.example.ui.theme.WhatsAppBrandGreen
import com.example.util.WhatsAppHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoyaltyAndBackupScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }

    val loyaltyClients by viewModel.loyaltyClients.collectAsStateWithLifecycle()
    val rewards by viewModel.rewards.collectAsStateWithLifecycle()

    val isSyncing by viewModel.isCloudSyncing.collectAsStateWithLifecycle()
    val lastBackupTime by viewModel.lastCloudBackupTime.collectAsStateWithLifecycle()
    val isAutoSync by viewModel.isAutoSyncEnabled.collectAsStateWithLifecycle()
    val firestoreState by viewModel.firestoreSyncState.collectAsStateWithLifecycle()
    val appointments by viewModel.allAppointments.collectAsStateWithLifecycle()
    val transactions by viewModel.filteredTransactions.collectAsStateWithLifecycle()

    var redeemingReward by remember { mutableStateOf<LoyaltyReward?>(null) }

    Scaffold(
        containerColor = BentoBackground,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Bento Sub-Tabs Row
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = BentoSurfaceVariant,
                contentColor = BentoPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = BentoPrimary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.dp, BentoBorder.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Loyalty, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Fidelidade", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.CloudSync, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    text = { Text("Backup Nuvem", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium) }
                )
            }

            if (selectedTab == 0) {
                // ==================== FIDELIDADE SECTION ====================
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Bento Hero: Regra de Fidelidade
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(26.dp))
                                .background(BentoPrimaryContainer)
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "PROGRAMA DE RETENÇÃO",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp,
                                        color = BentoOnPrimaryContainer
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(BentoPinkAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = BentoPinkOnAccent, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "R$ 10 = 1 Ponto",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BentoOnPrimaryContainer
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Acumule pontos automaticamente a cada agendamento concluído ou pagamento online.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = BentoTextSecondary
                                )
                            }
                        }
                    }

                    // Recompensas Header
                    item {
                        Text(
                            text = "Catálogo de Recompensas (${rewards.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    }

                    // Rewards Bento Horizontal Row
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(rewards, key = { it.id }) { reward ->
                                BentoRewardCard(
                                    reward = reward,
                                    onRedeemClick = { redeemingReward = reward }
                                )
                            }
                        }
                    }

                    // Clientes Header
                    item {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Clientes & Lembretes WhatsApp (${loyaltyClients.size})",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                    }

                    items(loyaltyClients, key = { it.id }) { client ->
                        BentoClientLoyaltyCard(
                            client = client,
                            onSendReturnReminder = {
                                WhatsAppHelper.sendReturnReminder(
                                    context = context,
                                    clientName = client.clientName,
                                    clientPhone = client.clientPhone,
                                    serviceName = "cuidados capilares",
                                    points = client.totalPoints,
                                    tier = client.tier
                                )
                            }
                        )
                    }
                }
            } else {
                // ==================== CLOUD BACKUP SECTION ====================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ==================== BENTO TILE: FIREBASE FIRESTORE ====================
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(if (firestoreState.isLiveConnected) Color(0xFFF0FDF4) else BentoSurface)
                            .border(
                                1.dp,
                                if (firestoreState.isLiveConnected) EmeraldSuccess.copy(alpha = 0.4f) else BentoBorderLight,
                                RoundedCornerShape(26.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
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
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(if (firestoreState.isLiveConnected) EmeraldSuccess else BentoPrimaryContainer),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.CloudSync,
                                            contentDescription = null,
                                            tint = if (firestoreState.isLiveConnected) Color.White else BentoPrimary,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = "FIREBASE FIRESTORE",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = if (firestoreState.isLiveConnected) EmeraldSuccess else BentoPrimary
                                        )
                                        Text(
                                            text = "Sincronização em Tempo Real",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = BentoTextPrimary
                                        )
                                    }
                                }

                                Surface(
                                    shape = CircleShape,
                                    color = if (firestoreState.isLiveConnected) EmeraldSuccess.copy(alpha = 0.15f) else BentoSurfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(if (firestoreState.isLiveConnected) EmeraldSuccess else Color(0xFFF59E0B))
                                        )
                                        Text(
                                            text = if (firestoreState.isLiveConnected) "Online" else "Room Local",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (firestoreState.isLiveConnected) EmeraldSuccess else BentoTextPrimary
                                        )
                                    }
                                }
                            }

                            Text(
                                text = firestoreState.statusMessage,
                                style = MaterialTheme.typography.bodySmall,
                                color = BentoTextSecondary
                            )

                            // 2-Column Metrics for Scheduling & Finance
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = BentoSurfaceSubtle,
                                    border = BorderStroke(1.dp, BentoBorderLight),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "AGENDAMENTOS",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = BentoTextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${appointments.size} na base",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BentoTextPrimary
                                        )
                                        Text(
                                            text = "Sincronia automática",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = BentoTextSecondary
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = BentoSurfaceSubtle,
                                    border = BorderStroke(1.dp, BentoBorderLight),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "FINANCEIRO",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp,
                                            color = BentoTextSecondary
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "${transactions.size} lançamentos",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = BentoTextPrimary
                                        )
                                        Text(
                                            text = "Receitas & Despesas",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontSize = 9.sp,
                                            color = BentoTextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Bento Tile: Backup Nuvem Hero (styled directly from Bento Blue Accent spec)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(BentoBlueAccent)
                            .border(1.dp, Color(0xFFC2D6F6), RoundedCornerShape(26.dp))
                            .padding(20.dp)
                    ) {
                        Column {
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
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(BentoBlueOnAccent),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                    Column {
                                        Text(
                                            text = "BACKUP OK",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp,
                                            color = BentoBlueDark
                                        )
                                        Text(
                                            text = "Cofre Criptografado AES-256",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = BentoBlueOnAccent
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "ID: ${viewModel.cloudVaultId}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = BentoBlueDark
                            )

                            val lastDateStr = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault()).format(Date(lastBackupTime))
                            Text(
                                text = "Última sincronização: $lastDateStr",
                                style = MaterialTheme.typography.labelSmall,
                                color = BentoBlueDark.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Bento Card: Auto-sync Switch
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(BentoSurface)
                            .border(1.dp, BentoBorderLight, RoundedCornerShape(22.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Backup Automático",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoTextPrimary
                                )
                                Text(
                                    text = "Sincroniza automaticamente a cada alteração.",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoTextSecondary
                                )
                            }
                            Switch(
                                checked = isAutoSync,
                                onCheckedChange = { viewModel.toggleAutoSync(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = BentoPrimary,
                                    checkedTrackColor = BentoPrimaryContainer
                                ),
                                modifier = Modifier.testTag("switch_auto_sync")
                            )
                        }
                    }

                    // Cloud Action 1: Sincronizar Agora
                    Button(
                        onClick = { viewModel.syncCloudBackupNow() },
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("sync_cloud_button")
                    ) {
                        if (isSyncing) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Sincronizando...", fontWeight = FontWeight.Bold)
                        } else {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Fazer Backup na Nuvem Agora", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Cloud Action 2: Restaurar
                    OutlinedButton(
                        onClick = { viewModel.restoreFromCloudBackup() },
                        enabled = !isSyncing,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("restore_cloud_button")
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Restaurar Dados da Nuvem", fontWeight = FontWeight.Bold)
                    }

                    // Cloud Action 3: Exportar Arquivo JSON
                    FilledTonalButton(
                        onClick = { viewModel.exportBackupFile() },
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("export_backup_button")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Exportar Backup (.json)", fontWeight = FontWeight.SemiBold)
                    }

                    Spacer(modifier = Modifier.height(72.dp))
                }
            }
        }
    }

    // Redeem Reward Dialog
    redeemingReward?.let { reward ->
        RedeemRewardDialog(
            reward = reward,
            clients = loyaltyClients,
            onDismiss = { redeemingReward = null },
            onConfirm = { clientPhone ->
                viewModel.redeemReward(clientPhone, reward)
                redeemingReward = null
            }
        )
    }
}

@Composable
fun BentoRewardCard(
    reward: LoyaltyReward,
    onRedeemClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(BentoSurface)
            .border(1.dp, BentoBorderLight, RoundedCornerShape(22.dp))
            .padding(14.dp)
            .testTag("reward_card_${reward.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BentoPinkAccent,
                    shape = CircleShape
                ) {
                    Text(
                        text = "${reward.pointsRequired} pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = BentoPinkOnAccent,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
                Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = BentoPrimary, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = reward.title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = reward.description,
                style = MaterialTheme.typography.labelSmall,
                color = BentoTextSecondary,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onRedeemClick,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resgatar", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BentoClientLoyaltyCard(
    client: ClientLoyalty,
    onSendReturnReminder: () -> Unit
) {
    val tierLabel = when (client.tier) {
        "VIP_DIAMANTE" -> "💎 Diamante"
        "OURO" -> "🥇 Ouro"
        "PRATA" -> "🥈 Prata"
        else -> "🥉 Bronze"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(BentoSurface)
            .border(1.dp, BentoBorderLight, RoundedCornerShape(22.dp))
            .padding(14.dp)
            .testTag("loyalty_client_${client.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = client.clientName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "${client.totalVisits} visitas • R$ ${String.format(Locale.getDefault(), "%.2f", client.totalSpent)} total",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                }

                Surface(
                    color = BentoSurfaceSubtle,
                    shape = CircleShape
                ) {
                    Text(
                        text = tierLabel,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(BentoPinkAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = BentoPinkOnAccent, modifier = Modifier.size(14.dp))
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${client.totalPoints} Pontos",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = BentoPrimary
                    )
                }

                Button(
                    onClick = onSendReturnReminder,
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppBrandGreen),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("return_reminder_button_${client.id}")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Lembrete WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RedeemRewardDialog(
    reward: LoyaltyReward,
    clients: List<ClientLoyalty>,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedClient by remember { mutableStateOf<ClientLoyalty?>(clients.firstOrNull()) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(26.dp))
                .background(BentoSurface)
                .border(1.dp, BentoBorderLight, RoundedCornerShape(26.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Resgatar Recompensa",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Surface(
                        color = BentoPinkAccent,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "${reward.pointsRequired} pts",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoPinkOnAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = reward.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary
                )

                Text(
                    text = reward.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Selecione o Cliente:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoTextPrimary
                )

                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = selectedClient?.let { "${it.clientName} (${it.totalPoints} pts)" } ?: "Selecione",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        clients.forEach { client ->
                            val canAfford = client.totalPoints >= reward.pointsRequired
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = "${client.clientName} (${client.totalPoints} pts)",
                                            fontWeight = if (canAfford) FontWeight.Bold else FontWeight.Normal,
                                            color = if (canAfford) BentoTextPrimary else BentoTextSecondary
                                        )
                                        if (!canAfford) {
                                            Text(
                                                text = "Pontos insuficientes (necessário: ${reward.pointsRequired})",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedClient = client
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                val hasEnoughPoints = (selectedClient?.totalPoints ?: 0) >= reward.pointsRequired

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            selectedClient?.let { onConfirm(it.clientPhone) }
                        },
                        enabled = hasEnoughPoints && selectedClient != null,
                        colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Confirmar")
                    }
                }
            }
        }
    }
}
