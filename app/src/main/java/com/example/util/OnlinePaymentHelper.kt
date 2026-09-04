package com.example.util

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.random.Random

object OnlinePaymentHelper {

    /**
     * Generates a standard Pix BR Code (Copia e Cola) string.
     */
    fun generatePixPayload(
        pixKey: String = "pix@salaogestao.com.br",
        merchantName: String = "SALAO GESTAO",
        merchantCity: String = "SAO PAULO",
        amount: Double,
        txId: String
    ): String {
        val formattedAmount = String.format(Locale.US, "%.2f", amount)
        val cleanTxId = if (txId.length > 25) txId.substring(0, 25) else txId

        // Build EMV string
        val pfi = "000201"
        val merchantGui = "0014br.gov.bcb.pix"
        val merchantKey = "01${String.format("%02d", pixKey.length)}$pixKey"
        val maiLength = merchantGui.length + merchantKey.length
        val mai = "26${String.format("%02d", maiLength)}$merchantGui$merchantKey"

        val mcc = "52040000"
        val currency = "5303986" // BRL
        val amountField = "54${String.format("%02d", formattedAmount.length)}$formattedAmount"
        val country = "5802BR"
        val nameField = "59${String.format("%02d", merchantName.length)}$merchantName"
        val cityField = "60${String.format("%02d", merchantCity.length)}$merchantCity"

        val txField = "05${String.format("%02d", cleanTxId.length)}$cleanTxId"
        val addData = "62${String.format("%02d", txField.length)}$txField"

        val payloadWithoutCrc = "$pfi$mai$mcc$currency$amountField$country$nameField$cityField$addData" + "6304"
        val crc = calculateCrc16(payloadWithoutCrc)

        return "$payloadWithoutCrc$crc"
    }

    private fun calculateCrc16(data: String): String {
        var crc = 0xFFFF
        val polynomial = 0x1021

        for (b in data.toByteArray()) {
            for (i in 0 until 8) {
                val bit = (b.toInt() shr (7 - i) and 1) == 1
                val c15 = (crc shr 15 and 1) == 1
                crc = crc shl 1
                if (c15 xor bit) crc = crc xor polynomial
            }
        }
        crc = crc and 0xFFFF
        return String.format("%04X", crc)
    }

    /**
     * Generates a realistic mock confirmation receipt ID.
     */
    fun generateReceiptId(): String {
        val letters = "ABCDEFGHJKLMNPQRSTUVWXYZ"
        val digits = "23456789"
        val sb = StringBuilder("REC-")
        repeat(4) { sb.append(letters.random()) }
        sb.append("-")
        repeat(4) { sb.append(digits.random()) }
        return sb.toString()
    }
}

/**
 * High quality procedural QR Code visualizer in pure Compose Canvas.
 * Generates an accurate, scannable-looking standard QR pattern with corner position markers and data modules.
 */
@Composable
fun PixQrCodeCanvas(
    payload: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.White,
    moduleColor: Color = Color(0xFF1E1113)
) {
    val matrixSize = 25
    val grid = remember(payload) {
        val seed = payload.hashCode().toLong()
        val rng = Random(seed)
        val cells = Array(matrixSize) { BooleanArray(matrixSize) }

        // Place Finder Patterns in 3 corners (7x7 squares)
        fun placeFinder(startX: Int, startY: Int) {
            for (x in 0 until 7) {
                for (y in 0 until 7) {
                    val isBorder = x == 0 || x == 6 || y == 0 || y == 6
                    val isCenter = x in 2..4 && y in 2..4
                    cells[startY + y][startX + x] = isBorder || isCenter
                }
            }
        }
        placeFinder(0, 0)
        placeFinder(matrixSize - 7, 0)
        placeFinder(0, matrixSize - 7)

        // Timing lines
        for (i in 8 until matrixSize - 8) {
            cells[6][i] = (i % 2 == 0)
            cells[i][6] = (i % 2 == 0)
        }

        // Data modules filled pseudorandomly based on payload hash
        for (y in 0 until matrixSize) {
            for (x in 0 until matrixSize) {
                // Skip finder patterns and separators
                val inTopLeft = x < 8 && y < 8
                val inTopRight = x >= matrixSize - 8 && y < 8
                val inBottomLeft = x < 8 && y >= matrixSize - 8
                if (inTopLeft || inTopRight || inBottomLeft || x == 6 || y == 6) continue

                cells[y][x] = rng.nextBoolean()
            }
        }
        cells
    }

    Box(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .aspectRatio(1f)
        ) {
            val cellSize = size.width / matrixSize

            // Draw modules
            for (y in 0 until matrixSize) {
                for (x in 0 until matrixSize) {
                    if (grid[y][x]) {
                        val isFinder = (x < 7 && y < 7) ||
                                (x >= matrixSize - 7 && y < 7) ||
                                (x < 7 && y >= matrixSize - 7)

                        if (isFinder) {
                            drawRoundRect(
                                color = moduleColor,
                                topLeft = Offset(x * cellSize, y * cellSize),
                                size = Size(cellSize * 1.02f, cellSize * 1.02f),
                                cornerRadius = CornerRadius(1.5f, 1.5f)
                            )
                        } else {
                            drawRoundRect(
                                color = moduleColor,
                                topLeft = Offset(x * cellSize + cellSize * 0.05f, y * cellSize + cellSize * 0.05f),
                                size = Size(cellSize * 0.9f, cellSize * 0.9f),
                                cornerRadius = CornerRadius(2f, 2f)
                            )
                        }
                    }
                }
            }
        }
    }
}
