package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Appointment
import com.example.ui.screens.BentoAppointmentCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

    @get:Rule val composeTestRule = createComposeRule()

    @Test
    fun greeting_screenshot() {
        val sampleAppointment = Appointment(
            id = 1L,
            clientName = "Juliana Silveira",
            clientPhone = "11999887766",
            serviceId = 1L,
            serviceName = "Corte Feminino & Escova Modelada",
            servicePrice = 140.0,
            professionalId = 1L,
            professionalName = "Ana Clara (Hair Stylist)",
            appointmentDate = "2026-09-04",
            appointmentTime = "10:00",
            status = "CONFIRMADO",
            paymentStatus = "PAGO_PIX"
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                BentoAppointmentCard(
                    appointment = sampleAppointment,
                    onConfirmWhatsApp = {},
                    onOpenPayment = {},
                    onComplete = {},
                    onReview = {},
                    onCancel = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/appointment_card.png")
    }
}
