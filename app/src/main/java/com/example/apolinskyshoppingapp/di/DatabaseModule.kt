package com.example.apolinskyshoppingapp.di


import android.content.Context
import com.example.apolinskyshoppingapp.data.AppDatabase
import com.example.apolinskyshoppingapp.data.ShoppingDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideShoppingDao(appDatabase: AppDatabase): ShoppingDAO {
        return appDatabase.shoppingDAO()
    }

    @Provides
    @Singleton
    fun provideShoppingDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }
}