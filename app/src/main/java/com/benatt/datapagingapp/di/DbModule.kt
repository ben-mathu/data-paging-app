package com.benatt.datapagingapp.di

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import com.benatt.datapagingapp.data.BusinessesDao
import com.benatt.datapagingapp.data.DataPagingDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @author ben-mathu
 * @since 2/23/25
 */
@Module
@InstallIn(SingletonComponent::class)
class DbModule {

    @Provides
    fun provideDatabase(application: Application): DataPagingDatabase {
        return Room.databaseBuilder(application, DataPagingDatabase::class.java, "paging.db")
            .build()
    }

    @Provides
    fun provideBusinessesDao(database: DataPagingDatabase): BusinessesDao {
        return database.dataPagingDao()
    }
}