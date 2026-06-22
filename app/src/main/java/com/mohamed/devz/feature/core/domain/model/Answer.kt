package com.mohamed.devz.feature.core.domain.model

data class Answer(
    val id: Int,
    val description: String,
    val accepted: Boolean,
    val votedIds: String,
    val questionId: Int,
    val accountId: Int,
    val createdAt: String?
)

fun Answer.toggleVote(accountId: Int): Answer {
    val ids = votedIds.split(",").filter { it.isNotBlank() }
    return if (ids.contains(accountId.toString())) {
        copy(votedIds = ids.filterNot { it == accountId.toString() }.joinToString(","))
    } else {
        copy(votedIds = (ids + accountId.toString()).joinToString(","))
    }
}
