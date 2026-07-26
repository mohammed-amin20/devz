package com.mohamed.devz.feature.splash.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import com.mohamed.devz.feature.core.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import jakarta.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    sealed interface SplashEvent {
        data object NavigateToOnboarding : SplashEvent
        data object NavigateToAuth : SplashEvent
        data object NavigateToHome : SplashEvent
        data object NavigateToBanned : SplashEvent
    }

    private val _splashEvent = MutableSharedFlow<SplashEvent>()
    val splashEvent: SharedFlow<SplashEvent> = _splashEvent.asSharedFlow()

    fun onAction(action: SplashAction) {
        when (action) {
            SplashAction.DecideWhereToGoAction -> decideWhereToGo()
        }
    }

    private fun decideWhereToGo() {
        viewModelScope.launch {
            val isFirstTime = userPreferencesRepository.observeIsFirstTime().first()
            val isLoggedIn = userPreferencesRepository.observeIsLoggedIn().first()
            _splashEvent.emit(
                when {
                    isFirstTime -> SplashEvent.NavigateToOnboarding
                    !isLoggedIn -> SplashEvent.NavigateToAuth
                    else -> {
                        val accountId = userPreferencesRepository.observeCurrentAccountId().first() ?: 0
                        if (accountId == 0) SplashEvent.NavigateToAuth
                        else when (val result = accountRepository.getById(accountId)) {
                            is Result.Success -> if (result.data.isBanned) SplashEvent.NavigateToBanned else SplashEvent.NavigateToHome
                            else -> SplashEvent.NavigateToHome
                        }
                    }
                }
            )
        }
    }
}
