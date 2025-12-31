package com.hesapgunlugu.app.feature.settings

import android.content.Context
import android.os.Build
import android.text.format.Formatter
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import com.hesapgunlugu.app.core.backup.BackupEvent
import com.hesapgunlugu.app.core.backup.BackupPasswordValidator
import com.hesapgunlugu.app.core.backup.BackupViewModel
import com.hesapgunlugu.app.core.cloud.BackupFile
import com.hesapgunlugu.app.core.navigation.Route
import com.hesapgunlugu.app.core.security.SecurityViewModel
import com.hesapgunlugu.app.core.ui.theme.*
import com.hesapgunlugu.app.feature.settings.sections.AboutSettingsSection
import com.hesapgunlugu.app.feature.settings.sections.DataManagementSection
import com.hesapgunlugu.app.feature.settings.sections.GeneralSettingsSection
import com.hesapgunlugu.app.feature.settings.sections.SecuritySection
import com.hesapgunlugu.app.feature.settings.sections.SettingsHeader
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    navController: NavController,
    themeViewModel: ThemeViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = viewModel,
    securityViewModel: SecurityViewModel = hiltViewModel(),
    backupViewModel: BackupViewModel = hiltViewModel(),
    cloudBackupViewModel: CloudBackupViewModel = hiltViewModel(),
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsStateWithLifecycle()
    val settingsState by settingsViewModel.state.collectAsStateWithLifecycle()
    val securityState by securityViewModel.state.collectAsStateWithLifecycle()
    val backupState by backupViewModel.state.collectAsStateWithLifecycle()
    val cloudState by cloudBackupViewModel.state.collectAsStateWithLifecycle()

    var showLimitDialog by remember { mutableStateOf(false) }
    var showPinSetupDialog by remember { mutableStateOf(false) }
    var showNameDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var pendingRestore by remember { mutableStateOf<BackupFile?>(null) }
    var pendingDelete by remember { mutableStateOf<BackupFile?>(null) }
    var showExportPasswordDialog by remember { mutableStateOf(false) }
    var exportPassword by remember { mutableStateOf("") }
    var exportPasswordConfirm by remember { mutableStateOf("") }
    var pendingExportPassword by remember { mutableStateOf<String?>(null) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // small helper to get current language code without depending on core util
    fun getCurrentLanguageCode(ctx: Context): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ctx.resources.configuration.locales[0].language
            } else {
                @Suppress("DEPRECATION")
                ctx.resources.configuration.locale.language
            }
        } catch (e: Exception) {
            Locale.getDefault().language
        }
    }

    fun formatBackupDate(
        ctx: Context,
        millis: Long,
    ): String {
        val locale =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                ctx.resources.configuration.locales[0]
            } else {
                @Suppress("DEPRECATION")
                ctx.resources.configuration.locale
            }
        val formatter =
            DateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.SHORT,
                locale,
            )
        return formatter.format(Date(millis))
    }

    fun formatBackupSize(
        ctx: Context,
        bytes: Long,
    ): String {
        return Formatter.formatShortFileSize(ctx, bytes)
    }

    val languageCodes = stringArrayResource(R.array.language_codes)
    val languageNames = stringArrayResource(R.array.language_names)
    val supportedLanguages =
        remember(languageCodes, languageNames) {
            languageCodes.zip(languageNames)
        }

    // Export launcher
    val exportLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("application/json"),
        ) { uri ->
            val password = pendingExportPassword
            pendingExportPassword = null
            if (uri != null && !password.isNullOrBlank()) {
                backupViewModel.exportData(uri, password)
            } else if (uri != null) {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = context.getString(R.string.backup_password_required),
                    )
                }
            }
        }

    // Import launcher
    val importLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let { backupViewModel.onImportFileSelected(it) }
        }

    val cloudSignInLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            cloudBackupViewModel.onSignInResult(result.data)
        }

    // Handle backup events
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            backupViewModel.event.collect { event ->
                when (event) {
                    is BackupEvent.Success -> {
                        val message =
                            if (event.args.isEmpty()) {
                                context.getString(event.messageRes)
                            } else {
                                context.getString(event.messageRes, *event.args.toTypedArray())
                            }
                        snackbarHostState.showSnackbar(message)
                    }
                    is BackupEvent.Error -> {
                        val message =
                            if (event.args.isEmpty()) {
                                context.getString(event.messageRes)
                            } else {
                                context.getString(event.messageRes, *event.args.toTypedArray())
                            }
                        snackbarHostState.showSnackbar(message)
                    }
                    is BackupEvent.RequestExportLocation -> {
                        exportLauncher.launch(backupViewModel.getBackupFileName())
                    }
                    is BackupEvent.RequestImportFile -> {
                        importLauncher.launch(arrayOf("application/json"))
                    }
                    is BackupEvent.RequestPasswordChange -> {
                        // Handle password change request if needed
                    }
                }
            }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            cloudBackupViewModel.event.collect { event ->
                when (event) {
                    is CloudBackupEvent.LaunchSignIn -> {
                        cloudSignInLauncher.launch(event.intent)
                    }
                    is CloudBackupEvent.Message -> {
                        val message =
                            if (event.args.isEmpty()) {
                                context.getString(event.messageRes)
                            } else {
                                context.getString(event.messageRes, *event.args.toTypedArray())
                            }
                        snackbarHostState.showSnackbar(message)
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0.dp),
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValues ->

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(bottom = paddingValues.calculateBottomPadding())
                    .verticalScroll(scrollState),
        ) {
            SettingsHeader(
                userName = settingsState.userName,
                isDarkTheme = isDarkTheme,
                onClick = { showNameDialog = true },
            )

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)) {
                val currentLanguageLabel =
                    run {
                        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        val code =
                            prefs.getString("language_code", getCurrentLanguageCode(context))
                                ?: getCurrentLanguageCode(context)
                        supportedLanguages.firstOrNull { it.first == code }?.second
                            ?: Locale(code).getDisplayLanguage(Locale(code))
                                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                    }

                GeneralSettingsSection(
                    settingsState = settingsState,
                    isDarkTheme = isDarkTheme,
                    currentLanguageLabel = currentLanguageLabel,
                    onLimitClick = { showLimitDialog = true },
                    onCurrencyClick = { showCurrencyDialog = true },
                    onLanguageClick = { showLanguageDialog = true },
                    onCategoryClick = { navController.navigate(Route.CategoryManagement) },
                    onThemeChange = { isDark -> themeViewModel.toggleTheme(isDark) },
                    notificationsEnabled = settingsState.notificationsEnabled,
                    onNotificationsToggle = { },
                )

                Spacer(modifier = Modifier.height(28.dp))

                SecuritySection(
                    securityState = securityState,
                    isDarkTheme = isDarkTheme,
                    canUseBiometric = securityViewModel.canUseBiometric(),
                    onAppLockToggle = { enabled -> securityViewModel.setAppLockEnabled(enabled) },
                    onBiometricToggle = { enabled -> securityViewModel.setBiometricEnabled(enabled) },
                    onChangePinClick = { showPinSetupDialog = true },
                    onSetupPinClick = { showPinSetupDialog = true },
                )

                Spacer(modifier = Modifier.height(28.dp))

                DataManagementSection(
                    isDarkTheme = isDarkTheme,
                    isLoading = backupState.isLoading || cloudState.isLoading,
                    onExportClick = { showExportPasswordDialog = true },
                    onImportClick = { backupViewModel.requestImport() },
                    onDeleteClick = { navController.navigate(Route.DataDeletion) },
                )

                Spacer(modifier = Modifier.height(28.dp))

                AboutSettingsSection(
                    isDarkTheme = isDarkTheme,
                    onNotificationsClick = { navController.navigate(Route.Notifications) },
                    onPrivacyClick = { navController.navigate(Route.Privacy) },
                    onAboutClick = { },
                    onHelpClick = { },
                )
            }
        }
    }

    // --- LİMİT DİALOGU ---
    if (showLimitDialog) {
        var tempLimit by remember { mutableStateOf(settingsState.monthlyLimit.toInt().toString()) }
        AlertDialog(
            onDismissRequest = { showLimitDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.set_limit),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column {
                    Text(
                        stringResource(R.string.new_monthly_limit),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    OutlinedTextField(
                        value = tempLimit,
                        onValueChange = { if (it.all { char -> char.isDigit() }) tempLimit = it },
                        label = { Text(stringResource(R.string.amount_tl)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue,
                                unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                cursorColor = PrimaryBlue,
                            ),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newLimit = tempLimit.toDoubleOrNull() ?: settingsState.monthlyLimit
                        settingsViewModel.updateMonthlyLimit(newLimit)
                        showLimitDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                ) { Text(stringResource(R.string.save), color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showLimitDialog = false }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    // --- DİL SEÇİM DİALOGU ---
    if (showLanguageDialog) {
        val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val current = prefs.getString("language_code", getCurrentLanguageCode(context)) ?: getCurrentLanguageCode(context)

        AlertDialog(
            onDismissRequest = { showLanguageDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.language_selection),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column {
                    supportedLanguages.forEach { pair: Pair<String, String> ->
                        val code = pair.first
                        val name = pair.second
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        prefs.edit().putString("language_code", code).apply()
                                        // Update configuration for current context
                                        // Apply locale immediately for resources
                                        val locale = Locale(code)
                                        Locale.setDefault(locale)
                                        val res = context.resources
                                        val config = res.configuration
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                            config.setLocales(android.os.LocaleList(locale))
                                        } else {
                                            @Suppress("DEPRECATION")
                                            config.locale = locale
                                        }
                                        @Suppress("DEPRECATION")
                                        res.updateConfiguration(config, res.displayMetrics)

                                        showLanguageDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = current == code,
                                onClick = {
                                    prefs.edit().putString("language_code", code).apply()
                                    val locale = Locale(code)
                                    Locale.setDefault(locale)
                                    val res = context.resources
                                    val config = res.configuration
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        config.setLocales(android.os.LocaleList(locale))
                                    } else {
                                        @Suppress("DEPRECATION")
                                        config.locale = locale
                                    }
                                    @Suppress("DEPRECATION")
                                    res.updateConfiguration(config, res.displayMetrics)
                                    showLanguageDialog = false
                                },
                                colors =
                                    RadioButtonDefaults.colors(
                                        selectedColor = PrimaryBlue,
                                    ),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showLanguageDialog = false }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
        )
    }

    // --- PIN SETUP DIALOGU ---
    if (showPinSetupDialog) {
        var pin by remember { mutableStateOf("") }
        var confirmPin by remember { mutableStateOf("") }
        var step by remember { mutableIntStateOf(1) } // 1: PIN gir, 2: PIN onayla
        var error by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showPinSetupDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    text = if (step == 1) stringResource(R.string.pin_setup) else stringResource(R.string.pin_confirm),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (step == 1) stringResource(R.string.enter_pin_4_digit) else stringResource(R.string.reenter_pin),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    OutlinedTextField(
                        value = if (step == 1) pin else confirmPin,
                        onValueChange = {
                            if (it.length <= 4 && it.all { char -> char.isDigit() }) {
                                if (step == 1) pin = it else confirmPin = it
                                error = null
                            }
                        },
                        label = { Text(stringResource(R.string.pin_label)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        isError = error != null,
                        supportingText = error?.let { { Text(it, color = ExpenseRed) } },
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue,
                                cursorColor = PrimaryBlue,
                            ),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (step == 1) {
                            if (pin.length == 4) {
                                step = 2
                            } else {
                                error = context.getString(R.string.pin_must_be_4_digits)
                            }
                        } else {
                            if (pin == confirmPin) {
                                securityViewModel.setPin(pin)
                                showPinSetupDialog = false
                            } else {
                                error = context.getString(R.string.pins_do_not_match)
                                confirmPin = ""
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(if (step == 1) stringResource(R.string.next) else stringResource(R.string.save), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    if (step == 2) {
                        step = 1
                        confirmPin = ""
                        error = null
                    } else {
                        showPinSetupDialog = false
                    }
                }) {
                    Text(
                        if (step == 2) stringResource(R.string.back) else stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            },
        )
    }

    // --- İMPORT ONAY DİALOGU ---

    if (showExportPasswordDialog) {
        AlertDialog(
            onDismissRequest = {
                showExportPasswordDialog = false
                exportPassword = ""
                exportPasswordConfirm = ""
            },
            title = { Text(text = stringResource(R.string.backup_password_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = exportPassword,
                        onValueChange = {
                            exportPassword = it
                            backupViewModel.validateBackupPassword(it)
                        },
                        label = { Text(stringResource(R.string.backup_password)) },
                        singleLine = true,
                    )
                    OutlinedTextField(
                        value = exportPasswordConfirm,
                        onValueChange = { exportPasswordConfirm = it },
                        label = { Text(stringResource(R.string.backup_password_confirm)) },
                        singleLine = true,
                    )

                    backupState.passwordValidation?.let { validation ->
                        val strengthRes = BackupPasswordValidator.getStrengthMeterText(validation.score)
                        Text(
                            text = stringResource(strengthRes),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }

                    if (exportPassword.isNotBlank() && exportPasswordConfirm.isNotBlank() && exportPassword != exportPasswordConfirm) {
                        Text(
                            text = stringResource(R.string.backup_password_mismatch),
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            },
            confirmButton = {
                val isMatch = exportPassword == exportPasswordConfirm
                val isValid = backupState.isPasswordValid && isMatch
                TextButton(
                    enabled = isValid,
                    onClick = {
                        pendingExportPassword = exportPassword
                        showExportPasswordDialog = false
                        exportPassword = ""
                        exportPasswordConfirm = ""
                        backupViewModel.requestExport()
                    },
                ) {
                    Text(stringResource(R.string.export_data))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showExportPasswordDialog = false
                        exportPassword = ""
                        exportPasswordConfirm = ""
                    },
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
        )
    }

    if (backupState.showImportConfirmDialog) {
        AlertDialog(
            onDismissRequest = { backupViewModel.cancelImport() },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.import_data_title),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column {
                    Text(
                        stringResource(R.string.import_data_question),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Seçenek: Mevcut verilere ekle
                    OutlinedButton(
                        onClick = { backupViewModel.confirmImport(replaceExisting = false) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.outlinedButtonColors(
                                contentColor = PrimaryBlue,
                            ),
                    ) {
                        Icon(Icons.Outlined.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.add_to_existing))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Seçenek: Mevcut verileri sil ve değiştir
                    Button(
                        onClick = { backupViewModel.confirmImport(replaceExisting = true) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = ExpenseRed,
                            ),
                    ) {
                        Icon(Icons.Outlined.SwapHoriz, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(stringResource(R.string.replace_all_data), color = Color.White)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { backupViewModel.cancelImport() }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
        )
    }

    if (pendingRestore != null) {
        val backup = pendingRestore
        AlertDialog(
            onDismissRequest = { pendingRestore = null },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.cloud_backup_restore_title),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Text(
                    stringResource(R.string.cloud_backup_restore_question, backup?.name.orEmpty()),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Column {
                    OutlinedButton(
                        onClick = {
                            backup?.let { cloudBackupViewModel.restoreBackup(it.id, replaceExisting = false) }
                            pendingRestore = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryBlue),
                    ) {
                        Text(stringResource(R.string.add_to_existing))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                            backup?.let { cloudBackupViewModel.restoreBackup(it.id, replaceExisting = true) }
                            pendingRestore = null
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                    ) {
                        Text(stringResource(R.string.replace_all_data), color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRestore = null }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
        )
    }

    if (pendingDelete != null) {
        val backup = pendingDelete
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.cloud_backup_delete_title),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Text(
                    stringResource(R.string.cloud_backup_delete_confirm, backup?.name.orEmpty()),
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        backup?.let { cloudBackupViewModel.deleteBackup(it.id) }
                        pendingDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                    shape = RoundedCornerShape(10.dp),
                ) {
                    Text(stringResource(R.string.delete), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
        )
    }

    // --- İSİM DÜZENLEME DİALOGU ---
    if (showNameDialog) {
        var tempName by remember { mutableStateOf(settingsState.userName) }
        AlertDialog(
            onDismissRequest = { showNameDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.change_name),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column {
                    Text(
                        stringResource(R.string.enter_display_name),
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text(stringResource(R.string.name)) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors =
                            OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryBlue,
                                focusedLabelColor = PrimaryBlue,
                                cursorColor = PrimaryBlue,
                            ),
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            settingsViewModel.updateUserName(tempName.trim())
                            showNameDialog = false
                            scope.launch { snackbarHostState.showSnackbar(context.getString(R.string.name_updated)) }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                ) { Text(stringResource(R.string.save), color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showNameDialog = false }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
                }
            },
        )
    }

    // --- PARA BİRİMİ SEÇME DİALOGU ---
    if (showCurrencyDialog) {
        val currencies =
            listOf(
                "TRY" to stringResource(R.string.currency_try),
                "USD" to stringResource(R.string.currency_usd),
                "EUR" to stringResource(R.string.currency_eur),
                "GBP" to stringResource(R.string.currency_gbp),
            )

        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp),
            title = {
                Text(
                    stringResource(R.string.currency),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            text = {
                Column {
                    currencies.forEach { (code, name) ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        settingsViewModel.updateCurrency(code)
                                        showCurrencyDialog = false
                                    }
                                    .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RadioButton(
                                selected = settingsState.currencyCode == code,
                                onClick = {
                                    settingsViewModel.updateCurrency(code)
                                    showCurrencyDialog = false
                                },
                                colors =
                                    RadioButtonDefaults.colors(
                                        selectedColor = PrimaryBlue,
                                    ),
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
        )
    }
}
