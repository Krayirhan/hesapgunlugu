package com.hesapgunlugu.app.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hesapgunlugu.app.core.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel
    @Inject
    constructor(
        private val settingsRepository: SettingsRepository,
    ) : ViewModel() {
        // Artık DataStore'a bağlı, kapanınca kaybolmaz!
        val isDarkTheme: StateFlow<Boolean> =
            settingsRepository.isDarkThemeFlow
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = false,
                )

        fun toggleTheme(isDark: Boolean) {
            viewModelScope.launch {
                settingsRepository.toggleTheme(isDark)
            }
        }
    }
