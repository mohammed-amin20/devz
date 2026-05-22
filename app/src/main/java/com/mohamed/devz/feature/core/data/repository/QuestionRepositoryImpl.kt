package com.mohamed.devz.feature.core.data.repository

import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.mapper.toData
import com.mohamed.devz.feature.core.data.mapper.toDomain
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.util.Error
import com.mohamed.devz.feature.core.domain.util.Result
import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import jakarta.inject.Inject
import java.io.IOException

class QuestionRepositoryImpl @Inject constructor(
    private val remoteDataSource: DevZRemoteDataSource,
) : QuestionRepository {

    override suspend fun getById(id: Int): Result<Question, Error> {
        return try {
            val question = remoteDataSource.question.getQuestionById(id)
            Result.Success(question.toDomain())
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound("Question not found"))
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getByAccountId(accountId: Int): Result<List<Question>, Error> {
        return try {
            val questions = remoteDataSource.question.getQuestionsByAccountId(accountId)
            Result.Success(questions.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun getByTag(tag: String): Result<List<Question>, Error> {
        return try {
            val questions = remoteDataSource.question.getQuestionsByTag(tag)
            Result.Success(questions.map { it.toDomain() })
        } catch (e: PostgrestRestException) {
            Result.Error(Error.Unknown(e.message ?: "Database error"))
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun insert(question: Question): Result<Question, Error> {
        return try {
            val inserted = remoteDataSource.question.insertQuestion(question.toData())
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

    override suspend fun update(question: Question): Result<Unit, Error> {
        return try {
            remoteDataSource.question.updateQuestion(question.toData())
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound("Question not found"))
                409 -> Result.Error(Error.Conflict(e.message ?: "Conflict"))
                else -> Result.Error(Error.Unknown(e.message ?: "Database error"))
            }
        } catch (e: IOException) {
            Result.Error(Error.Network("Network error: ${e.message}"))
        } catch (e: Exception) {
            Result.Error(Error.Unknown(e.message ?: "Unknown error"))
        }
    }

    override suspend fun delete(id: Int): Result<Unit, Error> {
        return try {
            remoteDataSource.question.deleteQuestion(id)
            Result.Success(Unit)
        } catch (e: PostgrestRestException) {
            when (e.statusCode) {
                404 -> Result.Error(Error.NotFound("Question not found"))
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
