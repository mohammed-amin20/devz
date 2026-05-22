package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.LanguageType
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface LanguageTypeRepository {
    suspend fun getAll(): Result<List<LanguageType>, Error>
}
