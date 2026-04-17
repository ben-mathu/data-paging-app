package com.benatt.datapagingapp.data

import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito

/**
 * @author ben-mathu
 * @since 2/25/25
 */
class DataPagingSourceTest {
    private val businessApiService: BusinessApiService = mockk()
    private val businessesDao: BusinessesDao = mockk()
    private val firstPageLink = "\n" +
            "<http://0.0.0.0:8089/businesses?_limit=10&_page=1>; rel=\"first\", <http://0.0.0.0:8089/businesses?_limit=10&_page=2>; rel=\"next\", <http://0.0.0.0:8089/businesses?_limit=10&_page=2>; rel=\"last\"\n"
    private val secondPageLink = "\n" +
            "<http://0.0.0.0:8089/businesses?_limit=10&_page=1>; rel=\"first\", <http://0.0.0.0:8089/businesses?_limit=10&_page=1>; rel=\"prev\", <http://0.0.0.0:8089/businesses?_limit=10&_page=2>; rel=\"last\""

    @Test
    fun getPageConfig_FirstPage_ReturnsNullPrev2Next() = runTest {
        val pagingSource = DataPagingSource(10, businessApiService, businessesDao)

        val state = pagingSource.getPageConfig(firstPageLink)
        assertEquals(null, state.first)
        assertEquals(2, state.second)
    }

    @Test
    fun getPageConfig_SecondOnlyPage_Returns() = runTest {
        val pagingSource = DataPagingSource(10, businessApiService, businessesDao)

        val state = pagingSource.getPageConfig(secondPageLink)
        assertEquals(1, state.first)
        assertEquals(null, state.second)
    }

    @Test
    fun getPageConfig_EmptyLinks_ReturnNull() = runTest {
        val pagingSource = DataPagingSource(10, businessApiService, businessesDao)

        val state = pagingSource.getPageConfig(firstPageLink)
        assertEquals("first", state.second)
    }

    @Test
    fun getPageConfig_NullLink() = runTest {
        val pagingSource = DataPagingSource(10, businessApiService, businessesDao)

        val state = pagingSource.getPageConfig(null)
        assertEquals("first", state.second)
    }

    @Test
    fun getPageConfig_Page2_ReturnNext() = runTest {
        val pagingSource = DataPagingSource(10, businessApiService, businessesDao)

        val state = pagingSource.getPageConfig(firstPageLink)
        assertEquals("next", state.second)
    }

    @Test
    fun getPageConfig_LastPage_ReturnNext() = runTest {
        val pagingSource = DataPagingSource(10, businessApiService, businessesDao)

        val state = pagingSource.getPageConfig(firstPageLink)
        assertEquals("last", state.second)
    }
}