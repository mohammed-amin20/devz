package com.mohamed.devz.feature.core.data.data_source.remote

import com.mohamed.devz.feature.core.data.model.Answer
import com.mohamed.devz.feature.core.data.model.Account
import com.mohamed.devz.feature.core.data.model.LanguageType
import com.mohamed.devz.feature.core.data.model.Notification
import com.mohamed.devz.feature.core.data.model.NotificationType
import com.mohamed.devz.feature.core.data.model.Question
import com.mohamed.devz.feature.core.data.model.JobPosting
import com.mohamed.devz.feature.core.data.model.JobApplication
import com.mohamed.devz.feature.core.data.model.CompanyProfile
import com.mohamed.devz.feature.core.data.model.Report

interface DevZRemoteDataSource {

    val account: AccountTable
    val question: QuestionTable
    val languageType: LanguageTypeTable
    val answer: AnswerTable
    val notification: NotificationTable
    val notificationType: NotificationTypeTable
    val jobPosting: JobPostingTable
    val jobApplication: JobApplicationTable
    val companyProfile: CompanyProfileTable
    val report: ReportTable

    interface AccountTable {
        suspend fun uploadImage(imageBytes: ByteArray, fileName: String): String

        suspend fun insertAccount(account: Account): Account

        suspend fun getAccountById(id: Int): Account
        suspend fun getAccountsByIds(ids: List<Int>): List<Account>
        suspend fun getAllAccounts(): List<Account>
        suspend fun getAccountByUsernameAndPassword(
            username: String,
            password: String,
        ): Account?

        suspend fun searchAccounts(query: String): List<Account>

        suspend fun updateAccount(account: Account)
    }

    interface QuestionTable {
        suspend fun insertQuestion(question: Question): Question

        suspend fun getQuestionById(id: Int): Question
        suspend fun getQuestionsByAccountId(accountId: Int): List<Question>
        suspend fun getQuestionsByTag(tag: String): List<Question>
        suspend fun getQuestionsByAccountIds(
            accountIds: List<Int>,
            offset: Int,
            limit: Int,
        ): List<Question>
        suspend fun getQuestionsByTags(
            tags: List<String>,
            offset: Int,
            limit: Int,
        ): List<Question>
        suspend fun getAllQuestions(
            offset: Int,
            limit: Int,
            orderBy: String = "created_at",
            ascending: Boolean = false,
        ): List<Question>

        suspend fun getPinnedQuestions(): List<Question>

        suspend fun searchQuestions(
            query: String,
            offset: Int,
            limit: Int,
        ): List<Question>

        suspend fun updateQuestion(question: Question)

        suspend fun toggleQuestionLike(id: Int, likedAccountIds: String, likesCount: Int)

        suspend fun incrementAnswerCount(questionId: Int, answersCount: Int)

        suspend fun decrementAnswerCount(questionId: Int, answersCount: Int)

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
        suspend fun getAllAnswers(): List<Answer>

        suspend fun updateAnswer(answer: Answer)

        suspend fun deleteAnswer(answer: Answer)

        suspend fun getVotesForAnswerIds(answerIds: List<Int>): List<String>
    }

    interface NotificationTable {
        suspend fun insertNotification(notification: Notification): Notification

        suspend fun getAllNotificationsByAccountId(accountId: Int): List<Notification>

        suspend fun getSystemNotifications(): List<Notification>

        suspend fun updateNotification(notification: Notification)

        suspend fun deleteNotification(id: Int)

        suspend fun deleteSystemNotificationCopies(message: String)
    }

    interface NotificationTypeTable {
        suspend fun getAllNotificationTypes(): List<NotificationType>
    }

    interface JobPostingTable {
        suspend fun getApprovedJobPostings(): List<JobPosting>
        suspend fun getJobPostingById(id: Int): JobPosting
        suspend fun getAllJobPostings(): List<JobPosting>
        suspend fun getJobPostingsByAccountId(accountId: Int): List<JobPosting>
        suspend fun insertJobPosting(posting: JobPosting): JobPosting
        suspend fun updateJobPosting(posting: JobPosting)
    }

    interface JobApplicationTable {
        suspend fun insertJobApplication(application: JobApplication): JobApplication
        suspend fun getJobApplicationsByApplicantId(applicantId: Int): List<JobApplication>
        suspend fun getJobApplicationsByJobId(jobId: Int): List<JobApplication>
        suspend fun updateJobApplicationStatus(id: Int, status: String)
    }

    interface CompanyProfileTable {
        suspend fun getByAccountId(accountId: Int): CompanyProfile?
        suspend fun insert(profile: CompanyProfile): CompanyProfile
        suspend fun update(profile: CompanyProfile)
        suspend fun getAll(): List<CompanyProfile>
    }

    interface ReportTable {
        suspend fun insertReport(report: Report): Report
        suspend fun getReportsByStatus(status: String?): List<Report>
        suspend fun getReportByReporterAndTarget(
            reporterId: Int,
            reportedType: String,
            reportedId: Int,
        ): Report?
        suspend fun updateReportStatus(id: Int, status: String)
    }
}