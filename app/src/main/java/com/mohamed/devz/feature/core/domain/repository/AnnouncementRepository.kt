package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.Announcement
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface AnnouncementRepository {
    suspend fun getAll(): Result<List<Announcement>, Error>
    suspend fun insert(announcement: Announcement): Result<Announcement, Error>
    suspend fun delete(id: Int): Result<Unit, Error>
}
