package com.mohamed.devz.feature.core.domain.util

import com.mohamed.devz.feature.core.presentation.util.UiText

sealed interface Error {
    data object NotFound : Error
    data object Conflict : Error
    data object Unauthorized : Error
    data object Network : Error
    data object Storage : Error
    data class Unknown(val message: String) : Error
}

fun Error.toUIText(): UiText {
    return when (this) {
        Error.NotFound -> UiText.DynamicString("Resource not found")
        Error.Conflict -> UiText.DynamicString("Data already exists")
        Error.Unauthorized -> UiText.DynamicString("Please log in again")
        Error.Network -> UiText.DynamicString("Check your internet connection")
        Error.Storage -> UiText.DynamicString("Something went wrong saving data")
        is Error.Unknown -> UiText.DynamicString(message)
    }
}