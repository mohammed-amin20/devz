package com.mohamed.devz.feature.core.domain.repository

import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result

interface AnswerRepository {
    suspend fun getById(id: Int): Result<Answer, Error>
    suspend fun getByQuestionId(questionId: Int): Result<List<Answer>, Error>
    suspend fun getByAccountId(accountId: Int): Result<List<Answer>, Error>
    suspend fun insert(answer: Answer): Result<Answer, Error>
    suspend fun update(answer: Answer): Result<Unit, Error>
    suspend fun delete(answer: Answer): Result<Unit, Error>
}
