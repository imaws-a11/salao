package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoyaltyReward
import com.example.ui.SalonViewModel
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

@Composable
fun ClientLoyaltyScreen(
    viewModel: SalonViewModel,
    modifier: Modifier = Modifier
) {
    val activeClient by viewModel.activeClient.collectAsState()
    val rewards by viewModel.rewards.collectAsState()

    var redeemedRewardVoucher by remember { mutableStateOf<Pair<LoyaltyReward, String>?>(null) }
    val context = LocalContext.current

    val nextTierTarget = when (activeClient.tier) {
        "BRONZE" -> 50
        "PRATA" -> 150
        "OURO" -> 300
        else -> 500
    }
    val progress = (activeClient.totalPoints.toFloat() / nextTierTarget).coerceIn(0f, 1f)

    Box(modifier = modifier.fillMaxSize().background(BentoBackground)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 90.dp)
        ) {
            // Digital VIP Loyalty Card (Bento Style)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .testTag("client_vip_loyalty_card"),
                    colors = CardDefaults.cardColors(containerColor = BentoPrimary),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "GLOWUP CLUB VIP",
                                    style = MaterialTheme.typography.labelSmall,
                                    letterSpacing = 1.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BentoOnPrimary.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = activeClient.clientName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoOnPrimary
                                )
                            }

                            // Tier Pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldLoyalty
                            ) {
                                Text(
                                    text = "NÍVEL ${activeClient.tier}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = BentoTextPrimary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }

                        // Points Balance Display
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text(
                                    text = "Saldo Disponível",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BentoOnPrimary.copy(alpha = 0.85f)
                                )
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = null,
                                        tint = GoldLoyalty,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Text(
                                        text = "${activeClient.totalPoints}",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = BentoOnPrimary
                                    )
                                    Text(
                                        text = "pontos",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = BentoOnPrimary.copy(alpha = 0.9f)
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "${activeClient.totalVisits} visitas",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = BentoOnPrimary.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = "R$ ${String.format(Locale.getDefault(), "%.2f", activeClient.totalSpent)} investidos",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoOnPrimary.copy(alpha = 0.75f)
                                )
                            }
                        }

                        // Tier Progress Bar
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progresso de Nível",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BentoOnPrimary.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "${activeClient.totalPoints} / $nextTierTarget pts",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = BentoOnPrimary
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = GoldLoyalty,
                                trackColor = BentoOnPrimary.copy(alpha = 0.25f)
                            )
                        }
                    }
                }
            }

            // Loyalty Rules Bento Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BentoSurface),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BentoBorder.copy(alpha = 0.5f))),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Como acumular e aproveitar seus pontos",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "• A cada R$ 10 gastos em serviços ou na loja de produtos, você ganha 1 ponto.\n• Troque seus pontos por descontos reais ou procedimentos cortesias.\n• Clientes Ouro e Diamante recebem mimos exclusivos e prioridade de agenda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoTextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            // Rewards Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Catálogo de Recompensas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                    Text(
                        text = "${rewards.size} disponíveis",
                        style = MaterialTheme.typography.labelSmall,
                        color = BentoTextSecondary
                    )
                }
            }

            // Rewards Items
            items(rewards, key = { it.id }) { reward ->
                val canRedeem = activeClient.totalPoints >= reward.pointsRequired
                ClientRewardBentoCard(
                    reward = reward,
                    canRedeem = canRedeem,
                    onRedeem = {
                        viewModel.redeemReward(reward)
                        val code = "GLOW-${(1000..9999).random()}"
                        redeemedRewardVoucher = Pair(reward, code)
                    }
                )
            }
        }
    }

    // Voucher Dialog
    if (redeemedRewardVoucher != null) {
        val (reward, code) = redeemedRewardVoucher!!
        AlertDialog(
            onDismissRequest = { redeemedRewardVoucher = null },
            title = {
                Text(
                    text = "🎉 Recompensa Resgatada!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary
                )
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Apresente este código na recepção do salão no momento do atendimento:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = BentoTextSecondary
                    )
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BentoPrimaryContainer,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = code,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = BentoPrimary,
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .align(Alignment.CenterHorizontally),
                            letterSpacing = 2.sp
                        )
                    }
                    Text(
                        text = "${reward.title}\n(${reward.pointsRequired} pontos debitados)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = BentoTextPrimary
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val msg = "Olá! Acabei de resgatar o voucher *$code* (${reward.title}) no aplicativo do salão. Meu nome é ${activeClient.clientName}."
                        val intent = Intent(Intent.ACTION_VIEW).apply {
                            data = Uri.parse("https://api.whatsapp.com/send?phone=5511987654321&text=${Uri.encode(msg)}")
                        }
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback
                        }
                        redeemedRewardVoucher = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WhatsAppBrandGreen)
                ) {
                    Text("Enviar no WhatsApp", fontWeight = FontWeight.Bold, color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { redeemedRewardVoucher = null },
                    colors = ButtonDefaults.buttonColors(containerColor = BentoSurfaceVariant)
                ) {
                    Text("Concluir", color = BentoTextPrimary)
                }
            }
        )
    }
}

@Composable
fun ClientRewardBentoCard(
    reward: LoyaltyReward,
    canRedeem: Boolean,
    onRedeem: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .testTag("client_reward_card_${reward.id}"),
        colors = CardDefaults.cardColors(containerColor = BentoSurface),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(BentoBorder.copy(alpha = 0.5f)))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (canRedeem) EmeraldSuccess.copy(alpha = 0.12f) else BentoSurfaceVariant
                ) {
                    Text(
                        text = if (canRedeem) "DISPONÍVEL" else "BLOQUEADO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (canRedeem) EmeraldSuccess else BentoTextSecondary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = GoldLoyalty, modifier = Modifier.size(16.dp))
                    Text(
                        text = "${reward.pointsRequired} pts",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = BentoTextPrimary
                    )
                }
            }

            Text(
                text = reward.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = BentoTextPrimary
            )

            if (reward.description.isNotBlank()) {
                Text(
                    text = reward.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = BentoTextSecondary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Economia de R$ ${String.format(Locale.getDefault(), "%.2f", reward.discountValue)}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = BentoPrimary
                )

                Button(
                    onClick = onRedeem,
                    enabled = canRedeem,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BentoPrimary,
                        disabledContainerColor = BentoSurfaceVariant
                    ),
                    modifier = Modifier.testTag("redeem_reward_button_${reward.id}")
                ) {
                    Text(
                        text = if (canRedeem) "Resgatar" else "Faltam pontos",
                        fontWeight = FontWeight.Bold,
                        color = if (canRedeem) BentoOnPrimary else BentoTextSecondary
                    )
                }
            }
        }
    }
}
