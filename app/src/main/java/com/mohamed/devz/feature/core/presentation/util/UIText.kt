package com.mohamed.devz.feature.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed class UIText {
    data class StringValue(val value: String) : UIText()
    data class StringResource(
        val resId: Int,
        val args: List<Any>,
    ) : UIText()

    @Composable
    fun toUIText(): String {
        return when (this) {
            is StringValue -> value
            is StringResource -> stringResource(resId, *args.toTypedArray())
        }
    }
}