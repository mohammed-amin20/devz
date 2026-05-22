package com.mohamed.devz.feature.core.data.data_source.remote

import com.mohamed.devz.feature.core.data.model.Account
import com.mohamed.devz.feature.core.data.model.Answer
import com.mohamed.devz.feature.core.data.model.LanguageType
import com.mohamed.devz.feature.core.data.model.Notification
import com.mohamed.devz.feature.core.data.model.NotificationType
import com.mohamed.devz.feature.core.data.model.Question
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

class DevZRemoteDataSourceImpl(
    private val db : Postgrest,
    private val storage : Storage
) : DevZRemoteDataSource {
    override val account: DevZRemoteDataSource.AccountTable
        get() = object : DevZRemoteDataSource.AccountTable {
            private val tableName = "Account"
            private val imagesBucketName = "images"

            override suspend fun uploadImage(imageBytes: ByteArray, fileName: String): String {
                val path = "images/$fileName"
                val bucket = storage.from(imagesBucketName)
                bucket.upload(path, imageBytes)
                return bucket.publicUrl(path)
            }

            override suspend fun insertAccount(account: Account): Account {
                return db.from(tableName)
                    .insert(account) {
                        select()
                    }
                    .decodeSingle()
            }

            override suspend fun getAccountById(id: Int): Account {
                return db.from(tableName)
                    .select {
                        filter { eq("id", id) }
                    }
                    .decodeSingle()
            }

            override suspend fun getAccountByUsernameAndPassword(
                username: String,
                password: String,
            ): Account? {
                return db.from(tableName)
                    .select {
                        filter {
                            eq("username", username)
                            eq("password", password)
                        }
                        limit(1)
                    }
                    .decodeSingleOrNull()
            }

            override suspend fun updateAccount(account: Account) {
                db.from(tableName)
                    .update(account) {
                        filter { eq("id", account.id) }
                    }
            }
        }

    override val question: DevZRemoteDataSource.QuestionTable
        get() = object :  DevZRemoteDataSource.QuestionTable {
            private val tableName = "Question"

            override suspend fun insertQuestion(question: Question): Question {
                return db.from(tableName)
                    .insert(question) {
                        select()
                    }
                    .decodeSingle()
            }

            override suspend fun getQuestionById(id: Int): Question {
                return db.from(tableName)
                    .select {
                        filter { eq("id", id) }
                    }
                    .decodeSingle()
            }

            override suspend fun getQuestionsByAccountId(accountId: Int): List<Question> {
                return db.from(tableName)
                    .select {
                        filter { eq("account_id", accountId) }
                    }
                    .decodeList()
            }

            override suspend fun getQuestionsByTag(tag: String): List<Question> {
                return db.from(tableName)
                    .select {
                        filter { like("tags", "%$tag%") }
                    }
                    .decodeList()
            }

            override suspend fun updateQuestion(question: Question) {
                db.from(tableName)
                    .update(question) {
                        filter { eq("id", question.id) }
                    }
            }

            override suspend fun deleteQuestion(id: Int) {
                db.from(tableName)
                    .delete {
                        filter { eq("id", id) }
                    }
            }
        }

    override val languageType: DevZRemoteDataSource.LanguageTypeTable
        get() = object : DevZRemoteDataSource.LanguageTypeTable {
            private val tableName = "LanguageType"

            override suspend fun getAllLanguageTypes(): List<LanguageType> {
                return db.from(tableName)
                    .select()
                    .decodeList()
            }
        }

    override val answer: DevZRemoteDataSource.AnswerTable
        get() = object : DevZRemoteDataSource.AnswerTable {
            private val tableName = "Answer"

            override suspend fun insertAnswer(answer: Answer): Answer {
                return db.from(tableName)
                    .insert(answer) {
                        select()
                    }
                    .decodeSingle()
            }

            override suspend fun getAnswerById(id: Int): Answer {
                return db.from(tableName)
                    .select {
                        filter { eq("id", id) }
                    }
                    .decodeSingle()
            }

            override suspend fun getAnswersByQuestionId(questionId: Int): List<Answer> {
                return db.from(tableName)
                    .select {
                        filter { eq("question_id", questionId) }
                    }
                    .decodeList()
            }

            override suspend fun getAnswersByAccountId(accountId: Int): List<Answer> {
                return db.from(tableName)
                    .select {
                        filter { eq("account_id", accountId) }
                    }
                    .decodeList()
            }

            override suspend fun updateAnswer(answer: Answer) {
                db.from(tableName)
                    .update(answer) {
                        filter { eq("id", answer.id) }
                    }
            }

            override suspend fun deleteAnswer(answer: Answer) {
                db.from(tableName)
                    .delete {
                        filter { eq("id", answer.id) }
                    }
            }
        }

    override val notification: DevZRemoteDataSource.NotificationTable
        get() = object : DevZRemoteDataSource.NotificationTable {
            private val tableName = "Notification"

            override suspend fun insertNotification(notification: Notification): Notification {
                return db.from(tableName)
                    .insert(notification) {
                        select()
                    }
                    .decodeSingle()
            }

            override suspend fun getAllNotificationsByAccountId(accountId: Int): List<Notification> {
                return db.from(tableName)
                    .select {
                        filter { eq("account_id", accountId) }
                    }
                    .decodeList()
            }

            override suspend fun updateNotification(notification: Notification) {
                db.from(tableName)
                    .update(notification) {
                        filter { eq("id", notification.id) }
                    }
            }
        }

    override val notificationType: DevZRemoteDataSource.NotificationTypeTable
        get() = object : DevZRemoteDataSource.NotificationTypeTable {
            private val tableName = "NotificationType"

            override suspend fun getAllNotificationTypes(): List<NotificationType> {
                return db.from(tableName)
                    .select()
                    .decodeList()
            }
        }

}