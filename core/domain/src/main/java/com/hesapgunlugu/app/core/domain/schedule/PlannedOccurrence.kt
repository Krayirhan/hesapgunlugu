package com.hesapgunlugu.app.core.domain.schedule

import com.hesapgunlugu.app.core.domain.model.ScheduledPayment
import java.util.Date

data class PlannedOccurrence(
    val payment: ScheduledPayment,
    val date: Date,
)
