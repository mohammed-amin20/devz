package com.mohamed.devz.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data object Auth : Route

    @Serializable
    data object Home : Route

    @Serializable
    data class QuestionDetails(
        val id: Int
    ) : Route

    @Serializable
    data class AddEditQuestion(
        val id: Int?
    ) : Route

    @Serializable
    data object EditProfile : Route

    @Serializable
    data class Profile(
        val accountId: Int
    ) : Route
}