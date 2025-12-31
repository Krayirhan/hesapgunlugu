package com.hesapgunlugu.app.core.ui.accessibility

import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.testTag
import com.hesapgunlugu.app.core.ui.R
import java.text.NumberFormat
import java.util.Locale

fun Modifier.accessibleDescription(description: String): Modifier =
    this.semantics {
        contentDescription = description
    }

fun Modifier.accessibleHeading(): Modifier =
    this.semantics {
        heading()
    }

fun Modifier.accessibleState(state: String): Modifier =
    this.semantics {
        stateDescription = state
    }

fun Modifier.accessibleTestTag(
    tag: String,
    description: String? = null,
): Modifier =
    this.semantics {
        testTag = tag
        description?.let { contentDescription = it }
    }

fun Modifier.accessibleClickable(
    label: String,
    role: Role? = Role.Button,
    onClickLabel: String? = null,
    onClick: () -> Unit,
) = this.semantics(mergeDescendants = true) {
    contentDescription = label
    onClickLabel?.let { this.onClick(label = it, action = null) }
}.clickable(
    role = role,
    onClick = onClick,
)

@Composable
fun Modifier.accessibleAmount(
    amount: Double,
    currency: String? = null,
    isIncome: Boolean,
): Modifier {
    val typeLabel = stringResource(if (isIncome) R.string.income else R.string.expense)
    val resolvedCurrency = currency ?: stringResource(R.string.currency_code_try)
    val formatter =
        NumberFormat.getNumberInstance(Locale.getDefault()).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
    val formattedAmount = formatter.format(amount)
    val description =
        stringResource(
            R.string.accessibility_amount_description,
            typeLabel,
            formattedAmount,
            resolvedCurrency,
        )
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun Modifier.accessibleDate(dateDescription: String): Modifier {
    val description = stringResource(R.string.accessibility_date_description, dateDescription)
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun Modifier.accessibleProgress(
    current: Float,
    max: Float,
    label: String,
): Modifier {
    val percentage = ((current / max) * 100).toInt()
    val description = stringResource(R.string.accessibility_percentage_description, label, percentage)
    val state = stringResource(R.string.accessibility_progress_state, current, max)
    return this.semantics {
        contentDescription = description
        stateDescription = state
    }
}

@Composable
fun Modifier.transactionAccessibility(
    title: String,
    amount: String,
    type: String,
    category: String,
    amountLabel: String? = null,
    categoryLabel: String? = null,
): Modifier {
    val resolvedAmountLabel = amountLabel ?: stringResource(R.string.amount)
    val resolvedCategoryLabel = categoryLabel ?: stringResource(R.string.category)
    val description =
        stringResource(
            R.string.accessibility_transaction_description,
            type,
            title,
            resolvedAmountLabel,
            amount,
            resolvedCategoryLabel,
            category,
        )
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun Modifier.buttonAccessibility(
    label: String,
    action: String? = null,
): Modifier {
    val description =
        action?.let {
            stringResource(R.string.accessibility_button_action, label, it)
        } ?: label
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun Modifier.dashboardCardAccessibility(
    title: String,
    value: String,
): Modifier {
    val description = stringResource(R.string.accessibility_label_value, title, value)
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun Modifier.balanceCardAccessibility(
    label: String,
    amount: Double,
    isPositive: Boolean,
    positiveState: String? = null,
    negativeState: String? = null,
): Modifier {
    val resolvedPositiveState = positiveState ?: stringResource(R.string.accessibility_positive_state)
    val resolvedNegativeState = negativeState ?: stringResource(R.string.accessibility_negative_state)
    val description = stringResource(R.string.accessibility_label_value, label, amount)
    val state = if (isPositive) resolvedPositiveState else resolvedNegativeState
    return this.semantics {
        contentDescription = description
        stateDescription = state
    }
}

@Composable
fun Modifier.chartAccessibility(
    chartType: String,
    summary: String,
    chartSuffix: String? = null,
): Modifier {
    val resolvedSuffix = chartSuffix ?: stringResource(R.string.accessibility_chart_suffix)
    val description =
        stringResource(
            R.string.accessibility_chart_description,
            chartType,
            resolvedSuffix,
            summary,
        )
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun Modifier.switchAccessibility(
    label: String,
    isChecked: Boolean,
    stateOn: String? = null,
    stateOff: String? = null,
): Modifier {
    val resolvedStateOn = stateOn ?: stringResource(R.string.accessibility_state_on)
    val resolvedStateOff = stateOff ?: stringResource(R.string.accessibility_state_off)
    val state = if (isChecked) resolvedStateOn else resolvedStateOff
    return this.semantics {
        contentDescription = label
        stateDescription = state
    }
}

@Composable
fun Modifier.navItemAccessibility(
    label: String,
    isSelected: Boolean,
    selectedState: String? = null,
    notSelectedState: String? = null,
): Modifier {
    val resolvedSelectedState = selectedState ?: stringResource(R.string.accessibility_selected)
    val resolvedNotSelectedState = notSelectedState ?: stringResource(R.string.accessibility_not_selected)
    val state = if (isSelected) resolvedSelectedState else resolvedNotSelectedState
    return this.semantics {
        contentDescription = label
        stateDescription = state
    }
}

@Composable
fun Modifier.formFieldAccessibility(
    label: String,
    hint: String? = null,
    error: String? = null,
    hintPrefix: String? = null,
    errorPrefix: String? = null,
): Modifier {
    val resolvedHintPrefix = hintPrefix ?: stringResource(R.string.accessibility_hint_prefix)
    val resolvedErrorPrefix = errorPrefix ?: stringResource(R.string.accessibility_error_prefix)
    val description =
        buildString {
            append(label)
            hint?.let { append(", $resolvedHintPrefix: $it") }
            error?.let { append(", $resolvedErrorPrefix: $it") }
        }
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun Modifier.toggleAccessibility(
    label: String,
    isEnabled: Boolean,
    stateOn: String? = null,
    stateOff: String? = null,
): Modifier {
    val resolvedStateOn = stateOn ?: stringResource(R.string.accessibility_state_on)
    val resolvedStateOff = stateOff ?: stringResource(R.string.accessibility_state_off)
    val state = if (isEnabled) resolvedStateOn else resolvedStateOff
    return this.semantics {
        contentDescription = label
        stateDescription = state
    }
}

@Composable
fun Modifier.listItemAccessibility(
    position: Int,
    total: Int,
    itemDescription: String,
): Modifier {
    val description =
        stringResource(
            R.string.accessibility_list_position,
            itemDescription,
            position + 1,
            total,
        )
    return this.semantics {
        contentDescription = description
    }
}

@Composable
fun Modifier.progressAccessibility(
    label: String,
    percentage: Int,
    percentageFormat: String? = null,
    completedFormat: String? = null,
): Modifier {
    val resolvedPercentageFormat = percentageFormat ?: stringResource(R.string.accessibility_percentage_format)
    val resolvedCompletedFormat = completedFormat ?: stringResource(R.string.accessibility_completed_format)
    val description =
        stringResource(
            R.string.accessibility_label_value,
            label,
            resolvedPercentageFormat.format(percentage),
        )
    val state = resolvedCompletedFormat.format(percentage)
    return this.semantics {
        contentDescription = description
        stateDescription = state
    }
}

@Composable
fun Modifier.alertAccessibility(
    type: AlertType,
    message: String,
    successPrefix: String? = null,
    warningPrefix: String? = null,
    errorPrefix: String? = null,
    infoPrefix: String? = null,
): Modifier {
    val resolvedSuccessPrefix = successPrefix ?: stringResource(R.string.accessibility_success_prefix)
    val resolvedWarningPrefix = warningPrefix ?: stringResource(R.string.accessibility_warning_prefix)
    val resolvedErrorPrefix = errorPrefix ?: stringResource(R.string.accessibility_error_prefix)
    val resolvedInfoPrefix = infoPrefix ?: stringResource(R.string.accessibility_info_prefix)
    val prefix =
        when (type) {
            AlertType.SUCCESS -> resolvedSuccessPrefix
            AlertType.WARNING -> resolvedWarningPrefix
            AlertType.ERROR -> resolvedErrorPrefix
            AlertType.INFO -> resolvedInfoPrefix
        }
    val description = stringResource(R.string.accessibility_alert_message, prefix, message)
    return this.semantics {
        contentDescription = description
    }
}

enum class AlertType {
    SUCCESS,
    WARNING,
    ERROR,
    INFO,
}

@Composable
fun Modifier.moneyAccessibility(
    amount: Double,
    currency: String? = null,
    negativePrefix: String? = null,
): Modifier {
    val resolvedCurrency = currency ?: stringResource(R.string.currency_code_try)
    val resolvedNegativePrefix = negativePrefix ?: stringResource(R.string.accessibility_negative_prefix)
    val absAmount = kotlin.math.abs(amount)
    val formattedAmount =
        if (amount < 0) {
            stringResource(R.string.accessibility_negative_amount, resolvedNegativePrefix, absAmount)
        } else {
            absAmount.toString()
        }
    val description =
        stringResource(
            R.string.accessibility_amount_with_currency,
            formattedAmount,
            resolvedCurrency,
        )
    return this.semantics {
        contentDescription = description
    }
}

fun Modifier.dateAccessibility(dateText: String): Modifier =
    this.semantics {
        contentDescription = dateText
    }
