package com.hesapgunlugu.app.feature.privacy

import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyResourcesTest {
    @Test
    fun privacySection1Title_exists() {
        assertTrue(R.string.privacy_section_1_title != 0)
    }

    @Test
    fun privacySection2Title_exists() {
        assertTrue(R.string.privacy_section_2_title != 0)
    }

    @Test
    fun privacySection3Title_exists() {
        assertTrue(R.string.privacy_section_3_title != 0)
    }
}
