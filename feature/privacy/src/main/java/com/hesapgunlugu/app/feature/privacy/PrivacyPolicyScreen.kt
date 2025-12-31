package com.hesapgunlugu.app.feature.privacy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Privacy Policy Screen
 * GDPR and KVKK compliant privacy policy
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(onBackClick: () -> Unit = {}) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.privacy_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.privacy_back),
                        )
                    }
                },
            )
        },
    ) { padding ->
        val lastUpdated =
            remember {
                val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
                LocalDate.now().format(formatter)
            }
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
        ) {
            PrivacySection(
                title = stringResource(R.string.privacy_section_1_title),
                content = stringResource(R.string.privacy_section_1_content),
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_2_title),
                content = stringResource(R.string.privacy_section_2_content),
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_3_title),
                content = stringResource(R.string.privacy_section_3_content),
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_4_title),
                content = stringResource(R.string.privacy_section_4_content),
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_5_title),
                content = stringResource(R.string.privacy_section_5_content),
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_6_title),
                content = stringResource(R.string.privacy_section_6_content),
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_7_title),
                content = stringResource(R.string.privacy_section_7_content),
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_8_title),
                content = stringResource(R.string.privacy_section_8_content, lastUpdated),
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrivacySection(
                title = stringResource(R.string.privacy_section_9_title),
                content = stringResource(R.string.privacy_section_9_content),
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun PrivacySection(
    title: String,
    content: String,
) {
    Column {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
