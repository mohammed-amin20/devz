package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.NotificationType
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface NotificationTypeRepository {
    suspend fun getAll(): Result<List<NotificationType>, Error>
}
