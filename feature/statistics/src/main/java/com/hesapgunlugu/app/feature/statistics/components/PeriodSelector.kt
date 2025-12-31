package com.hesapgunlugu.app.feature.statistics.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hesapgunlugu.app.core.ui.theme.PrimaryBlue
import com.hesapgunlugu.app.feature.statistics.R

@Composable
fun PeriodSelector(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val periods =
        listOf(
            stringResource(R.string.weekly),
            stringResource(R.string.monthly),
            stringResource(R.string.yearly),
        )

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        periods.forEach { period ->
            val isSelected = period == selectedPeriod
            TextButton(
                onClick = { onPeriodSelected(period) },
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isSelected) PrimaryBlue else Color.Transparent,
                        ),
                colors =
                    ButtonDefaults.textButtonColors(
                        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                    ),
            ) {
                Text(
                    text = period,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}
