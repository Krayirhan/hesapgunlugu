package com.hesapgunlugu.app.core.ui.animations

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

/**
 * Slide in from left animation
 */
@Composable
fun SlideInFromLeft(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter =
            slideInHorizontally(
                initialOffsetX = { -it },
                animationSpec = tween(300, easing = EaseOut),
            ) + fadeIn(animationSpec = tween(300)),
        exit =
            slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300, easing = EaseIn),
            ) + fadeOut(animationSpec = tween(300)),
        content = content,
    )
}

/**
 * Slide in from right animation
 */
@Composable
fun SlideInFromRight(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter =
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300, easing = EaseOut),
            ) + fadeIn(animationSpec = tween(300)),
        exit =
            slideOutHorizontally(
                targetOffsetX = { it },
                animationSpec = tween(300, easing = EaseIn),
            ) + fadeOut(animationSpec = tween(300)),
        content = content,
    )
}

/**
 * Fade animation
 */
@Composable
fun FadeAnimation(
    visible: Boolean,
    durationMillis: Int = 300,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(durationMillis)),
        exit = fadeOut(animationSpec = tween(durationMillis)),
        content = content,
    )
}

/**
 * Scale animation
 */
@Composable
fun ScaleAnimation(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter =
            scaleIn(
                initialScale = 0.8f,
                animationSpec = tween(300, easing = EaseOutBack),
            ) + fadeIn(animationSpec = tween(300)),
        exit =
            scaleOut(
                targetScale = 0.8f,
                animationSpec = tween(300, easing = EaseInBack),
            ) + fadeOut(animationSpec = tween(300)),
        content = content,
    )
}

/**
 * Expand vertically animation
 */
@Composable
fun ExpandVertically(
    visible: Boolean,
    content: @Composable AnimatedVisibilityScope.() -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter =
            expandVertically(
                animationSpec = tween(300, easing = EaseOut),
            ) + fadeIn(animationSpec = tween(300)),
        exit =
            shrinkVertically(
                animationSpec = tween(300, easing = EaseIn),
            ) + fadeOut(animationSpec = tween(300)),
        content = content,
    )
}

/**
 * Bounce animation modifier
 */
@Composable
fun Modifier.bounceClick() =
    this.then(
        Modifier.animateScale(),
    )

@Composable
private fun Modifier.animateScale(): Modifier {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.95f else 1f,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "scale",
    )
    return this
}

/**
 * Shimmer animation for loading states
 */
@Composable
fun rememberShimmerAnimation(): Float {
    val transition = rememberInfiniteTransition(label = "shimmer")
    return transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
        label = "shimmer",
    ).value
}

/**
 * Pulse animation
 */
@Composable
fun rememberPulseAnimation(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    return infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec =
            infiniteRepeatable(
                animation = tween(600, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse,
            ),
        label = "pulse",
    ).value
}
