package com.example.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.data.model.Appointment
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Locale

object WhatsAppHelper {

    fun sendAppointmentConfirmation(context: Context, appointment: Appointment) {
        val cleanPhone = cleanPhoneNumber(appointment.clientPhone)
        val formattedDate = formatDateBr(appointment.appointmentDate)

        val paymentStatusText = if (appointment.paymentStatus.startsWith("PAGO")) {
            "✅ *Pagamento:* Já confirmado online (${appointment.paymentStatus.replace("_", " ")})"
        } else {
            "💳 *Pagamento:* R$ ${String.format(Locale.getDefault(), "%.2f", appointment.servicePrice)} (Pague online via Pix ou no salão)"
        }

        val message = """
            ✨ *Confirmação de Horário - Salão Gestão* ✨
            
            Olá, *${appointment.clientName.trim()}*! Tudo bem?
            
            Passando para confirmar o seu agendamento conosco:
            📅 *Data:* $formattedDate
            ⏰ *Horário:* ${appointment.appointmentTime}
            💇 *Procedimento:* ${appointment.serviceName}
            👤 *Especialista:* ${appointment.professionalName}
            $paymentStatusText
            
            Por favor, responda esta mensagem com *CONFIRMAR* ou nos avise caso precise remarcar.
            
            Estamos preparando tudo com muito carinho para receber você! 🌸💖
        """.trimIndent()

        openWhatsApp(context, cleanPhone, message)
    }

    fun sendReturnReminder(
        context: Context,
        clientName: String,
        clientPhone: String,
        serviceName: String,
        points: Int,
        tier: String
    ) {
        val cleanPhone = cleanPhoneNumber(clientPhone)
        val tierBadge = when (tier) {
            "VIP_DIAMANTE" -> "💎 Cliente Diamante VIP"
            "OURO" -> "🥇 Cliente Ouro"
            "PRATA" -> "🥈 Cliente Prata"
            else -> "🥉 Cliente Especial"
        }

        val message = """
            🌸 *Sentimos sua falta! - Salão Gestão* 🌸
            
            Olá, *${clientName.trim()}*!
            
            Já faz alguns dias desde a sua última visita para *$serviceName* e queremos garantir que você continue se sentindo maravilhosa(o)!
            
            ✨ *Programa de Fidelidade:*
            Você está no nível *$tierBadge* e acumula *$points pontos* disponíveis para resgatar tratamentos e descontos exclusivos!
            
            Que tal reservar um momento de autocuidado esta semana?
            Responda aqui para garantir seu horário preferido com nossa equipe! 💕✂️
        """.trimIndent()

        openWhatsApp(context, cleanPhone, message)
    }

    fun shareMonthlyReport(context: Context, reportText: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Relatório Mensal de Desempenho - Salão Gestão")
            putExtra(Intent.EXTRA_TEXT, reportText)
        }
        val chooser = Intent.createChooser(intent, "Compartilhar Relatório Mensal via WhatsApp")
        context.startActivity(chooser)
    }

    private fun openWhatsApp(context: Context, phone: String, message: String) {
        try {
            val encodedMessage = URLEncoder.encode(message, "UTF-8")
            val fullPhone = if (phone.startsWith("55")) phone else "55$phone"
            val uri = Uri.parse("https://api.whatsapp.com/send?phone=$fullPhone&text=$encodedMessage")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback to standard ACTION_SEND
            try {
                val sendIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, message)
                    `package` = "com.whatsapp"
                }
                context.startActivity(sendIntent)
            } catch (ex: Exception) {
                Toast.makeText(context, "WhatsApp não encontrado. Texto copiado para a área de transferência.", Toast.LENGTH_LONG).show()
                copyToClipboard(context, message)
            }
        }
    }

    private fun cleanPhoneNumber(phone: String): String {
        return phone.replace("[^0-9]".toRegex(), "")
    }

    private fun formatDateBr(dateStr: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val formatter = SimpleDateFormat("dd 'de' MMMM (EEEE)", Locale("pt", "BR"))
            val date = parser.parse(dateStr)
            if (date != null) formatter.format(date) else dateStr
        } catch (e: Exception) {
            dateStr
        }
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
        val clip = android.content.ClipData.newPlainText("Mensagem Salão", text)
        clipboard?.setPrimaryClip(clip)
    }
}
