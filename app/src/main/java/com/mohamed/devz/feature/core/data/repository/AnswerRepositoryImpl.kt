package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.Answer
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import jakarta.inject.Inject
import java.io.IOException

class AnswerRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : AnswerRepository {

    override suspend fun getById(id: Int): Result<Answer, Error> {
        return try {
            val answer = remoteDataSource.answer.getAnswerById(id)
            Result.Success(answer.toDomain())
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound("Answer not found"))
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getByQuestionId(questionId: Int): Result<List<Answer>, Error> {
        return try {
            val answers = remoteDataSource.answer.getAnswersByQuestionId(questionId)
            Result.Success(answers.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getByAccountId(accountId: Int): Result<List<Answer>, Error> {
        return try {
            val answers = remoteDataSource.answer.getAnswersByAccountId(accountId)
            Result.Success(answers.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun insert(answer: Answer): Result<Answer, Error> {
        return try {
            val inserted = remoteDataSource.answer.insertAnswer(answer.toData())
            Result.Success(inserted.toDomain())
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                409 -> Result.Error(Error.Conflict(e.message ?: "Conflict"))
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun update(answer: Answer): Result<Unit, Error> {
        return try {
            remoteDataSource.answer.updateAnswer(answer.toData())
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound("Answer not found"))
                409 -> Result.Error(Error.Conflict(e.message ?: "Conflict"))
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun delete(answer: Answer): Result<Unit, Error> {
        return try {
            remoteDataSource.answer.deleteAnswer(answer.toData())
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound("Answer not found"))
                409 -> Result.Error(Error.Conflict(e.message ?: "Conflict"))
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }
}
