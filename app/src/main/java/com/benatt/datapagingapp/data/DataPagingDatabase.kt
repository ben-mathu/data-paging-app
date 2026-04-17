package com.benatt.datapagingapp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.benatt.datapagingapp.data.models.Business

/**
 * @author ben-mathu
 * @since 2/23/25
 */
@Database(entities = [Business::class], version = 1, exportSchema = false)
abstract class DataPagingDatabase: RoomDatabase() {
    abstract fun dataPagingDao(): BusinessesDao
}