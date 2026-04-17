package com.benatt.datapagingapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.benatt.datapagingapp.data.BusinessApiService
import com.benatt.datapagingapp.data.BusinessesDao
import com.benatt.datapagingapp.data.DataPagingSource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author ben-mathu
 * @since 2/23/25
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val businessApiService: BusinessApiService,
    private val businessesDao: BusinessesDao
) : ViewModel() {
    companion object {
        private const val PAGE_SIZE = 30
    }

    val businessFlow = Pager(PagingConfig(pageSize = PAGE_SIZE)) {
        DataPagingSource(PAGE_SIZE, businessApiService, businessesDao)
    }.flow.cachedIn(viewModelScope)
}