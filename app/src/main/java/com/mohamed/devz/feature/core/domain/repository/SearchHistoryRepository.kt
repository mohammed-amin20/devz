package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.SearchHistory
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface SearchHistoryRepository {
    suspend fun getRecentByAccountId(accountId: Int, limit: Int = 10): Result<List<SearchHistory>, Error>
    suspend fun insert(query: String, accountId: Int): Result<SearchHistory, Error>
    suspend fun clearAll(accountId: Int): Result<Unit, Error>
}
