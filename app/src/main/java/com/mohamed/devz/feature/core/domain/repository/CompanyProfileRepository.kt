package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.CompanyProfile
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface CompanyProfileRepository {
    suspend fun getByAccountId(accountId: Int): Result<CompanyProfile?, Error>
    suspend fun insert(profile: CompanyProfile): Result<CompanyProfile, Error>
    suspend fun update(profile: CompanyProfile): Result<Unit, Error>
    suspend fun getAll(): Result<List<CompanyProfile>, Error>
}
