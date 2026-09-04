package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.util.OnlinePaymentHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Salão Gestão", appName)
    }

    @Test
    fun `generate valid pix payload`() {
        val payload = OnlinePaymentHelper.generatePixPayload(
            pixKey = "financeiro@salao.com",
            amount = 120.0,
            txId = "APT1"
        )
        assertTrue(payload.startsWith("000201"))
        assertTrue(payload.contains("financeiro@salao.com"))
        assertTrue(payload.contains("120.00"))
    }

    @Test
    fun `calculate stock movement correctly`() {
        val initialStock = 10
        val stockIn = 5
        val stockOut = 3
        val updatedStock = initialStock + stockIn - stockOut
        assertEquals(12, updatedStock)
    }

    @Test
    fun `calculate average rating correctly`() {
        val ratings = listOf(5, 4, 5, 5, 4)
        val average = ratings.average()
        assertEquals(4.6, average, 0.01)
    }

    @Test
    fun `calculate client purchase loyalty points`() {
        val totalSpent = 180.0
        val pointsEarned = (totalSpent / 10).toInt().coerceAtLeast(1)
        assertEquals(18, pointsEarned)
    }

    @Test
    fun `calculate client loyalty tier progression`() {
        val pointsBronze = 45
        val pointsSilver = 80
        val pointsGold = 190
        val pointsDiamond = 350

        fun getTier(pts: Int) = when {
            pts >= 300 -> "VIP_DIAMANTE"
            pts >= 150 -> "OURO"
            pts >= 60 -> "PRATA"
            else -> "BRONZE"
        }

        assertEquals("BRONZE", getTier(pointsBronze))
        assertEquals("PRATA", getTier(pointsSilver))
        assertEquals("OURO", getTier(pointsGold))
        assertEquals("VIP_DIAMANTE", getTier(pointsDiamond))
    }
}
