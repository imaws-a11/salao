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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Appointment
import com.example.data.model.Professional
import com.example.ui.theme.BentoBorder
import com.example.ui.theme.BentoBorderLight
import com.example.ui.theme.BentoPrimary
import com.example.ui.theme.BentoSurface
import com.example.ui.theme.BentoSurfaceSubtle
import com.example.ui.theme.BentoSurfaceVariant
import com.example.ui.theme.BentoTextPrimary
import com.example.ui.theme.BentoTextSecondary
import com.example.ui.theme.GoldLoyalty

@Composable
fun FeedbackDialog(
    appointment: Appointment?,
    professional: Professional?,
    onDismiss: () -> Unit,
    onSubmit: (
        appointmentId: Long,
        clientName: String,
        clientPhone: String,
        professionalId: Long,
        professionalName: String,
        serviceId: Long,
        serviceName: String,
        rating: Int,
        comment: String
    ) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var clientName by remember { mutableStateOf(appointment?.clientName ?: "") }
    var clientPhone by remember { mutableStateOf(appointment?.clientPhone ?: "") }
    var comment by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val profName = appointment?.professionalName ?: professional?.name ?: "Profissional"
    val profId = appointment?.professionalId ?: professional?.id ?: 1L
    val servName = appointment?.serviceName ?: "Atendimento no Salão"
    val servId = appointment?.serviceId ?: 1L
    val aptId = appointment?.id ?: 0L

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, BentoBorder, RoundedCornerShape(24.dp))
                .testTag("dialog_feedback"),
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
                            text = "Avaliação do Cliente",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = BentoTextPrimary
                        )
                        Text(
                            text = "$servName com $profName",
                            style = MaterialTheme.typography.bodySmall,
                            color = BentoPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_feedback_dialog")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Fechar", tint = BentoTextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Star Rating Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(BentoSurfaceSubtle)
                        .border(1.dp, BentoBorderLight, RoundedCornerShape(16.dp))
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Como foi a experiência?",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = BentoTextPrimary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            for (star in 1..5) {
                                val isFilled = star <= rating
                                Icon(
                                    imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.Star,
                                    contentDescription = "Nota $star",
                                    tint = if (isFilled) GoldLoyalty else BentoTextSecondary.copy(alpha = 0.4f),
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clickable { rating = star }
                                        .testTag("star_rating_$star")
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val ratingLabel = when (rating) {
                            5 -> "Excelente! Superou expectativas ⭐⭐⭐⭐⭐"
                            4 -> "Muito Bom! Ótimo atendimento ⭐⭐⭐⭐"
                            3 -> "Bom, dentro do esperado ⭐⭐⭐"
                            2 -> "Regular, pode melhorar ⭐⭐"
                            else -> "Insatisfeito ⭐"
                        }
                        Text(
                            text = ratingLabel,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = if (rating >= 4) BentoPrimary else BentoTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Client Name (if not fixed)
                if (appointment == null) {
                    OutlinedTextField(
                        value = clientName,
                        onValueChange = { clientName = it; errorMessage = null },
                        label = { Text("Nome do Cliente") },
                        placeholder = { Text("ex: Juliana Costa") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = BentoPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_feedback_client_name"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BentoPrimary,
                            unfocusedBorderColor = BentoBorder,
                            focusedContainerColor = BentoSurfaceVariant,
                            unfocusedContainerColor = BentoSurfaceVariant
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Comment / Review
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it; errorMessage = null },
                    label = { Text("Depoimento / Comentário") },
                    placeholder = { Text("O que o cliente achou do resultado, pontualidade e simpatia?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("input_feedback_comment"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BentoPrimary,
                        unfocusedBorderColor = BentoBorder,
                        focusedContainerColor = BentoSurfaceVariant,
                        unfocusedContainerColor = BentoSurfaceVariant
                    ),
                    maxLines = 4
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

                // Submit Button
                Button(
                    onClick = {
                        val nameToUse = clientName.ifBlank { "Cliente Satisfeito" }
                        if (comment.isBlank()) {
                            errorMessage = "Por favor, escreva um breve comentário ou depoimento."
                            return@Button
                        }

                        onSubmit(
                            aptId,
                            nameToUse,
                            clientPhone,
                            profId,
                            profName,
                            servId,
                            servName,
                            rating,
                            comment
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_submit_feedback"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BentoPrimary)
                ) {
                    Text(
                        text = "Publicar Avaliação",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}
