package com.mohamed.devz.feature.core.di

import android.app.Application
import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSource
import com.mohamed.devz.feature.core.data.data_source.remote.DevZRemoteDataSourceImpl
import com.mohamed.devz.feature.core.data.data_source.local.preferences.UserPreferences
import com.mohamed.devz.feature.core.data.data_source.local.preferences.UserPreferencesImpl
import com.mohamed.devz.feature.core.data.repository.AccountRepositoryImpl
import com.mohamed.devz.feature.core.data.repository.AnswerRepositoryImpl
import com.mohamed.devz.feature.core.data.repository.LanguageTypeRepositoryImpl
import com.mohamed.devz.feature.core.data.repository.NotificationRepositoryImpl
import com.mohamed.devz.feature.core.data.repository.NotificationTypeRepositoryImpl
import com.mohamed.devz.feature.core.data.repository.QuestionRepositoryImpl
import com.mohamed.devz.feature.core.data.repository.UserPreferencesRepositoryImpl
import com.mohamed.devz.feature.core.domain.repository.AccountRepository
import com.mohamed.devz.feature.core.domain.repository.AnswerRepository
import com.mohamed.devz.feature.core.domain.repository.LanguageTypeRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationRepository
import com.mohamed.devz.feature.core.domain.repository.NotificationTypeRepository
import com.mohamed.devz.feature.core.domain.repository.QuestionRepository
import com.mohamed.devz.feature.core.domain.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CoreModule {

    @Provides
    @Singleton
    fun provideDevZSupabase(): SupabaseClient {
        return createSupabaseClient(
            supabaseUrl = "https://djmcmaiqrmrqwnwicqbp.supabase.co",
            supabaseKey = "sb_publishable_gVJgOxCgGV0TcycEw5DFtw_93cfObjd"
        ) {
            install(Postgrest)
            install(Storage)
        }
    }

    @Provides
    @Singleton
    fun provideDevZDatabase(supabaseClient: SupabaseClient): Postgrest {
        return supabaseClient.postgrest
    }

    @Provides
    @Singleton
    fun provideDevZStorage(supabaseClient: SupabaseClient): Storage {
        return supabaseClient.storage
    }

    @Provides
    @Singleton
    fun provideDevZRemoteDataSource(
        db : Postgrest,
        storage : Storage
    ) : DevZRemoteDataSource {
        return DevZRemoteDataSourceImpl(
            db = db,
            storage = storage
        )
    }

    @Provides
    @Singleton
    fun provideAccountRepository(
        remoteDataSource: DevZRemoteDataSource,
    ): AccountRepository {
        return AccountRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideQuestionRepository(
        remoteDataSource: DevZRemoteDataSource,
    ): QuestionRepository {
        return QuestionRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideAnswerRepository(
        remoteDataSource: DevZRemoteDataSource,
    ): AnswerRepository {
        return AnswerRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideLanguageTypeRepository(
        remoteDataSource: DevZRemoteDataSource,
    ): LanguageTypeRepository {
        return LanguageTypeRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideNotificationRepository(
        remoteDataSource: DevZRemoteDataSource,
    ): NotificationRepository {
        return NotificationRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideNotificationTypeRepository(
        remoteDataSource: DevZRemoteDataSource,
    ): NotificationTypeRepository {
        return NotificationTypeRepositoryImpl(remoteDataSource)
    }

    @Provides
    @Singleton
    fun provideUserPreferences(
        app: Application
    ): UserPreferences {
        return UserPreferencesImpl(app)
    }

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(
        userPreferences: UserPreferences,
    ): UserPreferencesRepository {
        return UserPreferencesRepositoryImpl(userPreferences)
    }
}