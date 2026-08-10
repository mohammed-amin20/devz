package com.mohamed.devz.feature.job.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged

@Composable
fun onLostFocus(onLostFocus: () -> Unit): Modifier {
    var wasFocused by remember { mutableStateOf(false) }
    return Modifier.onFocusChanged { state ->
        if (wasFocused && !state.isFocused) onLostFocus()
        wasFocused = state.isFocused
    }
}
