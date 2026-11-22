package com.example.dsy1105_005d_2026.ui.solicitud

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatoFecha(ms: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("es", "CL")).format(Date(ms))

fun generarQr(texto: String, tam: Int = 420): Bitmap {
    val matriz = QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, tam, tam)
    val bmp = Bitmap.createBitmap(tam, tam, Bitmap.Config.ARGB_8888)
    for (x in 0 until tam) {
        for (y in 0 until tam) {
            bmp.setPixel(x, y, if (matriz[x, y]) Color.BLACK else Color.WHITE)
        }
    }
    return bmp
}
