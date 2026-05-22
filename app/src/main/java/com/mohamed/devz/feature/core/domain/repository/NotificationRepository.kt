package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.Notification
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface NotificationRepository {
    suspend fun insert(notification: Notification): Result<Notification, Error>
    suspend fun getAllByAccountId(accountId: Int): Result<List<Notification>, Error>
    suspend fun update(notification: Notification): Result<Unit, Error>
}
