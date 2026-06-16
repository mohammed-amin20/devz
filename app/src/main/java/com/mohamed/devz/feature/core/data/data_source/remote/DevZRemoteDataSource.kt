package com.mohamed.devz.feature.core.data.data_source.remote

import com.mohamed.devz.feature.core.data.model.Answer
import com.mohamed.devz.feature.core.data.model.Account
import com.mohamed.devz.feature.core.data.model.LanguageType
import com.mohamed.devz.feature.core.data.model.Notification
import com.mohamed.devz.feature.core.data.model.NotificationType
import com.mohamed.devz.feature.core.data.model.Question

interface DevZRemoteDataSource {

    val account: AccountTable
    val question: QuestionTable
    val languageType: LanguageTypeTable
    val answer: AnswerTable
    val notification: NotificationTable
    val notificationType: NotificationTypeTable

    interface AccountTable {
        suspend fun uploadImage(imageBytes: ByteArray, fileName: String): String

        suspend fun insertAccount(account: Account): Account

        suspend fun getAccountById(id: Int): Account
        suspend fun getAllAccounts(): List<Account>
        suspend fun getAccountByUsernameAndPassword(
            username: String,
            password: String,
        ): Account?

        suspend fun updateAccount(account: Account)
    }

    interface QuestionTable {
        suspend fun insertQuestion(question: Question): Question

        suspend fun getQuestionById(id: Int): Question
        suspend fun getQuestionsByAccountId(accountId: Int): List<Question>
        suspend fun getQuestionsByTag(tag: String): List<Question>
        suspend fun getAllQuestions(
            offset: Int,
            limit: Int,
            orderBy: String = "created_at",
            ascending: Boolean = false,
        ): List<Question>

        suspend fun searchQuestions(
            query: String,
            offset: Int,
            limit: Int,
        ): List<Question>

        suspend fun updateQuestion(question: Question)

        suspend fun toggleQuestionLike(id: Int, likedAccountIds: String, likesCount: Int)

        suspend fun incrementAnswerCount(questionId: Int, answersCount: Int)

        suspend fun deleteQuestion(id: Int)
    }

    interface LanguageTypeTable {
        suspend fun getAllLanguageTypes(): List<LanguageType>
    }

    interface AnswerTable {
        suspend fun insertAnswer(answer: Answer): Answer

        suspend fun getAnswerById(id: Int): Answer
        suspend fun getAnswersByQuestionId(questionId: Int): List<Answer>
        suspend fun getAnswersByAccountId(accountId: Int): List<Answer>

        suspend fun updateAnswer(answer: Answer)

        suspend fun deleteAnswer(answer: Answer)
    }

    interface NotificationTable {
        suspend fun insertNotification(notification: Notification): Notification

        suspend fun getAllNotificationsByAccountId(accountId: Int): List<Notification>

        suspend fun updateNotification(notification: Notification)
    }

    interface NotificationTypeTable {
        suspend fun getAllNotificationTypes(): List<NotificationType>
    }
}