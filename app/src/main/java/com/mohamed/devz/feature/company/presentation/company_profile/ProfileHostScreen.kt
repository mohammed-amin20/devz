package com.mohamed.devz.feature.company.presentation.company_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.util.Result
import com.mohamed.devz.feature.profile.presentation.view_profile.ProfileScreen
import com.mohamed.devz.ui.theme.CyanPrimary
import com.mohamed.devz.ui.theme.QBg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileHostViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val accountRepository: AccountRepository,
) : ViewModel() {

    private val _accountType = MutableStateFlow<String?>(null)
    val accountType = _accountType.asStateFlow()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val accountId = savedStateHandle.get<Int>("accountId")
            if (accountId == null) {
                _accountType.value = null
                return@launch
            }
            when (val result = accountRepository.getById(accountId)) {
                is Result.Success -> _accountType.value = result.data.accountType
                is Result.Error -> _accountType.value = "developer"
            }
        }
    }
}

@Composable
fun ProfileHostScreen(
    onEditProfile: () -> Unit,
    onQuestionClick: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onAnswerClick: (Int) -> Unit = onQuestionClick,
    refreshTrigger: Int = 0,
    onFullScreenChanged: (Boolean) -> Unit = {},
    onDialogVisibilityChanged: (Boolean) -> Unit = {},
    onProfileClick: (Int) -> Unit = {},
    onAdminPanelClick: () -> Unit = {},
    onJobClick: (Int) -> Unit = {},
    onPostJob: () -> Unit = {},
    navAccountId: Int? = null,
    navigateUp: () -> Unit,
    viewModel: ProfileHostViewModel = hiltViewModel(),
) {
    val accountType by viewModel.accountType.collectAsState()

    when (accountType) {
        "company" -> CompanyProfileScreen(
            onJobClick = onJobClick,
            onPostJob = onPostJob,
            modifier = modifier,
            navAccountId = navAccountId,
            navigateUp = navigateUp,
        )

        null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(QBg)
                    .then(modifier),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = CyanPrimary)
            }
        }

        else -> ProfileScreen(
            onEditProfile = onEditProfile,
            onQuestionClick = onQuestionClick,
            onLogout = onLogout,
            modifier = modifier,
            onAnswerClick = onAnswerClick,
            refreshTrigger = refreshTrigger,
            onFullScreenChanged = onFullScreenChanged,
            onDialogVisibilityChanged = onDialogVisibilityChanged,
            onProfileClick = onProfileClick,
            onAdminPanelClick = onAdminPanelClick,
            onJobClick = onJobClick,
            navAccountId = navAccountId,
            navigateUp = navigateUp,
        )
    }
}
