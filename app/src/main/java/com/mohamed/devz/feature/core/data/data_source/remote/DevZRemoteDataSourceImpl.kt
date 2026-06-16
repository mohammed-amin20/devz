package com.mohamed.devz.feature.core.data.data_source.remote

import com.mohamed.devz.feature.core.data.model.Account
import com.mohamed.devz.feature.core.data.model.Answer
import com.mohamed.devz.feature.core.data.model.LanguageType
import com.mohamed.devz.feature.core.data.model.Notification
import com.mohamed.devz.feature.core.data.model.NotificationType
import com.mohamed.devz.feature.core.data.model.Question
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.stream.IntStream.range

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
                val json = buildJsonObject {
                    put("username", account.username)
                    put("full_name", account.fullName)
                    put("email", account.email)
                    put("password", account.password)
                    put("image_url", account.imageUrl)
                    put("bio", account.bio)
                    put("tech_stack", account.techStack)
                    put("github_url", account.githubUrl)
                    put("linkedin_url", account.linkedInUrl)
                    put("website_url", account.websiteUrl)
                }

                return db.from(tableName)
                    .insert(json) {
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

            override suspend fun getAllAccounts(): List<Account> {
                return db.from(tableName)
                    .select()
                    .decodeList()
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
                val json = buildJsonObject {
                    put("title", question.title)
                    put("description", question.description)
                    put("code", question.code)
                    put("likes_count", question.likesCount)
                    put("answers_count", question.answersCount)
                    put("tags", question.tags)
                    put("lang_type_id", question.langTypeId)
                    put("account_id", question.accountId)
                }

                return db.from(tableName)
                    .insert(json) {
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

            override suspend fun getAllQuestions(
                offset: Int,
                limit: Int,
                orderBy: String,
                ascending: Boolean,
            ): List<Question> {
                return db.from(tableName)
                    .select {
                        range(offset, offset + limit - 1)
                        order(column = orderBy, order = if (ascending) Order.ASCENDING else Order.DESCENDING)
                    }
                    .decodeList()
            }

            override suspend fun searchQuestions(
                query: String,
                offset: Int,
                limit: Int,
            ): List<Question> {
                return db.from(tableName)
                    .select {
                        range(offset, offset + limit - 1)
                        order(column = "created_at", order = Order.DESCENDING)
                        filter {
                            or {
                                like("title", "%$query%")
                                like("description", "%$query%")
                                like("code", "%$query%")
                                like("tags", "%$query%")
                            }
                        }
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
                val json = buildJsonObject {
                    put("description", answer.description)
                    put("accepted", answer.accepted)
                    put("voted_ids", answer.votedIds)
                    put("question_id", answer.questionId)
                    put("account_id", answer.accountId)
                }

                return db.from(tableName)
                    .insert(json) {
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
                val json = buildJsonObject {
                    put("description", notification.description)
                    put("account_id", notification.accountId)
                    put("type_id", notification.typeId)
                    put("seen", notification.seen)
                }

                return db.from(tableName)
                    .insert(json) {
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