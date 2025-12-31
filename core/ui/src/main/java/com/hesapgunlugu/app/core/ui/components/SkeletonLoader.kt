package com.hesapgunlugu.app.core.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

fun Modifier.shimmerEffect(): Modifier =
    composed {
        val transition = rememberInfiniteTransition(label = "shimmer")
        val translateAnim by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1000f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 1200,
                            easing = LinearEasing,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "shimmer_translate",
        )

        val base = MaterialTheme.colorScheme.surfaceVariant
        val shimmerColors =
            listOf(
                base.copy(alpha = 0.55f),
                base.copy(alpha = 0.2f),
                base.copy(alpha = 0.55f),
            )

        val brush =
            Brush.linearGradient(
                colors = shimmerColors,
                start = Offset(translateAnim, translateAnim),
                end = Offset(translateAnim + 200f, translateAnim + 200f),
            )

        this.background(brush)
    }

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(8.dp),
) {
    Box(
        modifier =
            modifier
                .clip(shape)
                .shimmerEffect(),
    )
}

@Composable
fun TransactionItemSkeleton() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Emoji placeholder
        SkeletonBox(
            modifier = Modifier.size(48.dp),
            shape = RoundedCornerShape(24.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Title placeholder
            SkeletonBox(
                modifier =
                    Modifier
                        .fillMaxWidth(0.6f)
                        .height(16.dp),
            )

            // Category placeholder
            SkeletonBox(
                modifier =
                    Modifier
                        .fillMaxWidth(0.3f)
                        .height(12.dp),
            )
        }

        // Amount placeholder
        SkeletonBox(
            modifier =
                Modifier
                    .width(80.dp)
                    .height(20.dp),
        )
    }
}

@Composable
fun DashboardCardSkeleton() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Title placeholder
        SkeletonBox(
            modifier =
                Modifier
                    .fillMaxWidth(0.4f)
                    .height(20.dp),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Balance card 1
            SkeletonBox(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(100.dp),
                shape = RoundedCornerShape(16.dp),
            )

            // Balance card 2
            SkeletonBox(
                modifier =
                    Modifier
                        .weight(1f)
                        .height(100.dp),
                shape = RoundedCornerShape(16.dp),
            )
        }
    }
}

@Composable
fun ChartSkeleton() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Chart title
        SkeletonBox(
            modifier =
                Modifier
                    .fillMaxWidth(0.5f)
                    .height(24.dp),
        )

        // Chart area
        SkeletonBox(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(200.dp),
            shape = RoundedCornerShape(12.dp),
        )

        // Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            repeat(3) {
                SkeletonBox(
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(40.dp),
                )
            }
        }
    }
}

@Composable
fun TransactionListSkeleton(itemCount: Int = 5) {
    Column(modifier = Modifier.fillMaxWidth()) {
        repeat(itemCount) {
            TransactionItemSkeleton()
            if (it < itemCount - 1) {
                Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)))
            }
        }
    }
}

@Composable
fun SettingsItemSkeleton() {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Icon placeholder
        SkeletonBox(
            modifier = Modifier.size(24.dp),
            shape = RoundedCornerShape(4.dp),
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            SkeletonBox(
                modifier =
                    Modifier
                        .fillMaxWidth(0.4f)
                        .height(16.dp),
            )

            SkeletonBox(
                modifier =
                    Modifier
                        .fillMaxWidth(0.7f)
                        .height(12.dp),
            )
        }

        // Trailing icon
        SkeletonBox(
            modifier = Modifier.size(16.dp),
            shape = RoundedCornerShape(2.dp),
        )
    }
}
