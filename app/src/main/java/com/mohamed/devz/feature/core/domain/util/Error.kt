package com.mohamed.devz.feature.core.domain.util

import com.mohamed.devz.feature.core.presentation.util.UIText

sealed interface Error {
    data object NotFound : Error
    data object Conflict : Error
    data object Unauthorized : Error
    data object Network : Error
    data object Storage : Error
    data object Unknown : Error
}

fun Error.toUIText(): UIText {
    return when (this) {
        Error.NotFound -> UIText.StringValue("Resource not found")
        Error.Conflict -> UIText.StringValue("Data already exists")
        Error.Unauthorized -> UIText.StringValue("Please log in again")
        Error.Network -> UIText.StringValue("Check your internet connection")
        Error.Storage -> UIText.StringValue("Something went wrong saving data")
        Error.Unknown -> UIText.StringValue("An unexpected error occurred")
    }
}