package com.mohamed.devz.feature.question.presentation.view_questions

import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.model.LanguageType
import com.mohamed.devz.feature.core.domain.model.Question
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class QuestionFeedUiModel(
    val id: Int,
    val authorName: String,
    val authorAvatarUrl: String,
    val timeAgo: String,
    val category: String,
    val title: String,
    val preview: String,
    val codeSnippet: String?,
    val tags: List<String>,
    val likes: Int,
    val answers: Int,
    val isBookmarked: Boolean = false,
)

private fun computeTimeAgo(createdAt: String): String {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[XXX]")
        val parsed = LocalDateTime.parse(createdAt, formatter)
        val minutes = Duration.between(parsed, LocalDateTime.now()).toMinutes()
        when {
            minutes < 1 -> "just now"
            minutes < 60 -> "${minutes}m ago"
            minutes < 1440 -> "${minutes / 60}h ago"
            else -> "${minutes / 1440}d ago"
        }
    } catch (_: Exception) {
        createdAt
    }
}

private val accountCache = mutableMapOf<Int, Account>()
private var languageTypeCache: Map<Int, LanguageType> = emptyMap()

fun updateAccountCache(accounts: List<Account>) {
    accountCache.putAll(accounts.associateBy { it.id })
}

fun updateLanguageTypeCache(types: List<LanguageType>) {
    languageTypeCache = types.associateBy { it.id }
}

fun Question.toFeedUiModel(
    bookmarkedIds: Set<Int> = emptySet(),
): QuestionFeedUiModel {
    val account = accountCache[accountId]
    val langType = languageTypeCache[langTypeId]
    return QuestionFeedUiModel(
        id = id,
        authorName = account?.fullName ?: "Unknown",
        authorAvatarUrl = account?.imageUrl ?: "",
        timeAgo = computeTimeAgo(createdAt),
        category = langType?.type ?: "Other",
        title = title,
        preview = description,
        codeSnippet = code.ifEmpty { null },
        tags = tags.split(",").filter { it.isNotBlank() },
        likes = likesCount,
        answers = answersCount,
        isBookmarked = id in bookmarkedIds,
    )
}
