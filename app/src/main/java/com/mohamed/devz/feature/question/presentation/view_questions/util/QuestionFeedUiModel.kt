package com.mohamed.devz.feature.question.presentation.view_questions.util


import com.mohamed.devz.feature.core.domain.model.Account
import com.mohamed.devz.feature.core.domain.model.LanguageType
import com.mohamed.devz.feature.core.domain.model.Question
import com.mohamed.devz.feature.core.presentation.util.formatRelativeTime

data class QuestionFeedUiModel(
    val id: Int,
    val authorAccountId: Int,
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
    val isPinned: Boolean = false,
    val isAuthorPro: Boolean = false,
)

val accountCache = mutableMapOf<Int, Account>()
private var languageTypeCache: Map<Int, LanguageType> = emptyMap()

fun updateAccountCache(accounts: List<Account>) {
    accountCache.putAll(accounts.associateBy { it.id })
}

fun updateLanguageTypeCache(types: List<LanguageType>) {
    languageTypeCache = types.associateBy { it.id }
}

fun Question.toFeedUiModel(
    bookmarkedIds: Set<Int> = emptySet(),
    isPinned: Boolean = false,
): QuestionFeedUiModel {
    val account = accountCache[accountId]
    val langType = languageTypeCache[langTypeId]
    return QuestionFeedUiModel(
        id = id,
        authorAccountId = account?.id ?: 0,
        authorName = account?.fullName ?: "Unknown",
        authorAvatarUrl = account?.imageUrl ?: "",
        timeAgo = formatRelativeTime(createdAt),
        category = langType?.type ?: "Other",
        title = title,
        preview = description,
        codeSnippet = code.ifEmpty { null },
        tags = tags.split(",").filter { it.isNotBlank() },
        likes = likesCount,
        answers = answersCount,
        isBookmarked = id in bookmarkedIds,
        isPinned = isPinned,
        isAuthorPro = account?.isPro ?: false,
    )
}
