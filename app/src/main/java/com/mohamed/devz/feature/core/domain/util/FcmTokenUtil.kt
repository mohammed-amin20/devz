package com.mohamed.devz.feature.core.domain.util

import com.google.firebase.messaging.FirebaseMessaging
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object FcmTokenUtil {
    fun saveCurrentToken(
        accountRepository: AccountRepository,
        userPreferencesRepository: UserPreferencesRepository,
    ) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result ?: return@addOnCompleteListener
                runBlocking {
                    val accountId = userPreferencesRepository.observeCurrentAccountId().first()
                        ?: return@runBlocking
                    if (accountId == 0) return@runBlocking
                    when (val result = accountRepository.getById(accountId)) {
                        is Result.Success -> {
                            accountRepository.update(result.data.copy(fcmToken = token))
                        }
                        is Result.Error -> {}
                    }
                }
            }
        }
    }
}
