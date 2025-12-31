package com.hesapgunlugu.app.core.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes =
    Shapes(
        small = RoundedCornerShape(8.dp),
        // Butonlar, TextField'lar
        medium = RoundedCornerShape(12.dp),
        // Kartlar, Dialog'lar
        large = RoundedCornerShape(16.dp),
        // BottomSheet'ler, BÃ¼yÃ¼k kartlar
    )

// Ekstra shape'ler
val CardShape = RoundedCornerShape(12.dp)
val ButtonShape = RoundedCornerShape(8.dp)
val ChipShape = RoundedCornerShape(8.dp)
