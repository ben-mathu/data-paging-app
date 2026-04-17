package com.benatt.datapagingapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.benatt.datapagingapp.data.models.Business

/**
 * @author ben-mathu
 * @since 2/23/25
 */
@Dao
interface BusinessesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrReplace(businesses: List<Business>): List<Long>
}