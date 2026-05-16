package com.mohamed.devz.feature.core.di

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
    fun provideDevzSupabase(): SupabaseClient {
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
    fun provideDevzDatabase(supabaseClient: SupabaseClient): Postgrest {
        return supabaseClient.postgrest
    }

    @Provides
    @Singleton
    fun provideDevzStorage(supabaseClient: SupabaseClient): Storage {
        return supabaseClient.storage
    }
}