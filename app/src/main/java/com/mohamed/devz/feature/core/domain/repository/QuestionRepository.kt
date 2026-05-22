package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface QuestionRepository {
    suspend fun getById(id: Int): Result<Question, Error>
    suspend fun getByAccountId(accountId: Int): Result<List<Question>, Error>
    suspend fun getByTag(tag: String): Result<List<Question>, Error>
    suspend fun insert(question: Question): Result<Question, Error>
    suspend fun update(question: Question): Result<Unit, Error>
    suspend fun delete(id: Int): Result<Unit, Error>
}
