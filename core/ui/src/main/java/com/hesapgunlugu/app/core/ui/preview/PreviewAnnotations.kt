package com.hesapgunlugu.app.core.ui.preview

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multi-preview annotation for light and dark themes
 */
@Preview(
    name = "Light Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark Mode",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class ThemePreviews

/**
 * Preview for different screen sizes
 */
@Preview(
    name = "Phone",
    device = "spec:width=411dp,height=891dp",
)
@Preview(
    name = "Tablet",
    device = "spec:width=1280dp,height=800dp,dpi=240",
)
annotation class DevicePreviews

/**
 * Font scale previews
 */
@Preview(
    name = "Normal Font",
    fontScale = 1.0f,
)
@Preview(
    name = "Large Font",
    fontScale = 1.5f,
)
annotation class FontScalePreviews

/**
 * Complete preview set (theme + device + font)
 */
@ThemePreviews
@DevicePreviews
@FontScalePreviews
annotation class CompletePreviews
