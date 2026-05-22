package com.mohamed.devz.feature.core.domain.util

import com.mohamed.devz.feature.core.domain.util.Error as DomainError

sealed interface Result<out D, out E : DomainError> {
    data class Success<D, E : DomainError>(val data: D) : Result<D, E>
    data class Error<D, E : DomainError>(val error: E, val data: D? = null) : Result<D, E>
}
