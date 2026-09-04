package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Appointment
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.PixTeal
import com.example.util.OnlinePaymentHelper
import com.example.util.PixQrCodeCanvas
import com.example.util.WhatsAppHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun PaymentDialog(
    appointment: Appointment,
    onDismiss: () -> Unit,
    onPaymentSuccess: (method: String) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var selectedMethodTab by remember { mutableIntStateOf(0) } // 0 = Pix, 1 = Cartão
    var isProcessing by remember { mutableStateOf(false) }
    var isSuccessReceipt by remember { mutableStateOf(false) }
    var receiptCode by remember { mutableStateOf("") }

    // Pix Payload
    val pixPayload = remember(appointment) {
        OnlinePaymentHelper.generatePixPayload(
            pixKey = "financeiro@salaogestao.com.br",
            amount = appointment.servicePrice,
            txId = "APT${appointment.id}"
        )
    }

    // Card Fields
    var cardNumber by remember { mutableStateOf("4532 •••• •••• 8892") }
    var cardHolder by remember { mutableStateOf(appointment.clientName) }
    var cardExpiry by remember { mutableStateOf("11/29") }
    var cardCvv by remember { mutableStateOf("842") }
    var installments by remember { mutableStateOf("1x de R$ ${String.format(Locale.getDefault(), "%.2f", appointment.servicePrice)} (à vista)") }

    Dialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("payment_modal"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
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
                            text = "Checkout Online Integrado",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ambiente Seguro com Criptografia SSL",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldSuccess
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        enabled = !isProcessing,
                        modifier = Modifier.testTag("close_payment_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Appointment Summary Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = appointment.serviceName,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "Cliente: ${appointment.clientName} • Prof: ${appointment.professionalName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "R$ ${String.format(Locale.getDefault(), "%.2f", appointment.servicePrice)}",
                            fontWeight = FontWeight.ExtraBold,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isSuccessReceipt) {
                    // Success Receipt View
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(EmeraldSuccess.copy(alpha = 0.15f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = "Sucesso",
                                tint = EmeraldSuccess,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Pagamento Confirmado!",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium,
                            color = EmeraldSuccess
                        )
                        Text(
                            text = "Comprovante: $receiptCode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "+ ${(appointment.servicePrice / 10).toInt()} Pontos de Fidelidade creditados ao cliente!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                val method = if (selectedMethodTab == 0) "PIX" else "CARTAO"
                                onPaymentSuccess(method)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("receipt_done_button")
                        ) {
                            Text("Concluir e Voltar")
                        }
                    }
                } else {
                    // Payment Method Tabs
                    TabRow(
                        selectedTabIndex = selectedMethodTab,
                        modifier = Modifier.clip(RoundedCornerShape(12.dp))
                    ) {
                        Tab(
                            selected = selectedMethodTab == 0,
                            onClick = { selectedMethodTab = 0 },
                            icon = { Icon(Icons.Default.QrCode2, contentDescription = "Pix", tint = PixTeal) },
                            text = { Text("Pix Instantâneo", fontWeight = FontWeight.SemiBold) }
                        )
                        Tab(
                            selected = selectedMethodTab == 1,
                            onClick = { selectedMethodTab = 1 },
                            icon = { Icon(Icons.Default.CreditCard, contentDescription = "Cartão") },
                            text = { Text("Cartão de Crédito", fontWeight = FontWeight.SemiBold) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (selectedMethodTab == 0) {
                        // PIX TAB
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Escaneie o QR Code com o app do seu banco:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Responsive QR Code Canvas
                            PixQrCodeCanvas(
                                payload = pixPayload,
                                modifier = Modifier
                                    .size(200.dp)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedButton(
                                onClick = {
                                    WhatsAppHelper.copyToClipboard(context, pixPayload)
                                    android.widget.Toast.makeText(context, "Código Pix Copia e Cola copiado!", android.widget.Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("copy_pix_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Copiar Código Pix (Copia e Cola)")
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isProcessing = true
                                        delay(1000)
                                        receiptCode = OnlinePaymentHelper.generateReceiptId()
                                        isProcessing = false
                                        isSuccessReceipt = true
                                    }
                                },
                                enabled = !isProcessing,
                                colors = ButtonDefaults.buttonColors(containerColor = PixTeal),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("confirm_pix_button")
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Validando no Banco Central...")
                                } else {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Simular Confirmação Pix Online")
                                }
                            }
                        }
                    } else {
                        // CARD TAB
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = cardNumber,
                                onValueChange = { cardNumber = it },
                                label = { Text("Número do Cartão") },
                                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = cardHolder,
                                onValueChange = { cardHolder = it },
                                label = { Text("Nome impresso no Cartão") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = cardExpiry,
                                    onValueChange = { cardExpiry = it },
                                    label = { Text("Validade") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = cardCvv,
                                    onValueChange = { cardCvv = it },
                                    label = { Text("CVV") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            OutlinedTextField(
                                value = installments,
                                onValueChange = { installments = it },
                                label = { Text("Parcelamento") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isProcessing = true
                                        delay(1200)
                                        receiptCode = OnlinePaymentHelper.generateReceiptId()
                                        isProcessing = false
                                        isSuccessReceipt = true
                                    }
                                },
                                enabled = !isProcessing && cardNumber.isNotBlank() && cardHolder.isNotBlank(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("pay_card_button")
                            ) {
                                if (isProcessing) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Processando Pagamento...")
                                } else {
                                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Pagar R$ ${String.format(Locale.getDefault(), "%.2f", appointment.servicePrice)} no Cartão")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
