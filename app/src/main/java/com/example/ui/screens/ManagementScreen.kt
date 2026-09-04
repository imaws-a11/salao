package com.example.ui.screens

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Loyalty
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.SalonViewModel
import com.example.ui.theme.BentoBackground
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceSubtle
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary

@Composable
fun ManagementScreen(
    viewModel: SalonViewModel,
    initialSubTab: String = "FINANCEIRO",
    modifier: Modifier = Modifier
) {
    var subTab by remember { mutableStateOf(initialSubTab) } // "FINANCEIRO", "RELATORIOS", "FIDELIDADE"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BentoBackground)
    ) {
        // Bento Subtab Switcher
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(BentoSurfaceVariant)
                    .border(1.dp, BentoBorderLight, RoundedCornerShape(16.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Financeiro
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (subTab == "FINANCEIRO") BentoPrimary else Color.Transparent)
                        .clickable { subTab = "FINANCEIRO" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_management_finance"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = if (subTab == "FINANCEIRO") Color.White else BentoTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Finanças",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (subTab == "FINANCEIRO") FontWeight.Bold else FontWeight.Medium,
                            color = if (subTab == "FINANCEIRO") Color.White else BentoTextSecondary
                        )
                    }
                }

                // Relatórios
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (subTab == "RELATORIOS") BentoPrimary else Color.Transparent)
                        .clickable { subTab = "RELATORIOS" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_management_reports"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Insights,
                            contentDescription = null,
                            tint = if (subTab == "RELATORIOS") Color.White else BentoTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Relatórios",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (subTab == "RELATORIOS") FontWeight.Bold else FontWeight.Medium,
                            color = if (subTab == "RELATORIOS") Color.White else BentoTextSecondary
                        )
                    }
                }

                // Fidelidade & Backup
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (subTab == "FIDELIDADE") BentoPrimary else Color.Transparent)
                        .clickable { subTab = "FIDELIDADE" }
                        .padding(vertical = 10.dp)
                        .testTag("tab_management_loyalty"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Loyalty,
                            contentDescription = null,
                            tint = if (subTab == "FIDELIDADE") Color.White else BentoTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Fidelidade & Nuvem",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (subTab == "FIDELIDADE") FontWeight.Bold else FontWeight.Medium,
                            color = if (subTab == "FIDELIDADE") Color.White else BentoTextSecondary,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Active Management Screen Content
        Box(modifier = Modifier.fillMaxSize()) {
            Crossfade(targetState = subTab, label = "ManagementSubTabCrossfade") { currentSubTab ->
                when (currentSubTab) {
                    "FINANCEIRO" -> FinanceScreen(viewModel = viewModel)
                    "RELATORIOS" -> ReportsScreen(viewModel = viewModel)
                    else -> LoyaltyAndBackupScreen(viewModel = viewModel)
                }
            }
        }
    }
}
