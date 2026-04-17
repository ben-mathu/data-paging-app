package com.benatt.datapagingapp.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.benatt.datapagingapp.data.models.Business
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author ben-mathu
 * @since 2/23/25
 */
class DataPagingSource(
    private val limit: Int,
    private val businessApiService: BusinessApiService,
    private val businessesDao: BusinessesDao
) : PagingSource<Int, Business>() {
    companion object {
        private val TAG = DataPagingSource::class.java.simpleName
    }

    override fun getRefreshKey(state: PagingState<Int, Business>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Business> {
        // The LoadParams object contains information about the load operation to be performed.
        // This includes the key to be loaded and the number of items to be loaded.
        return try {
            val response = businessApiService.getBusinesses(limit, params.key?:1)
            val links = response.headers()["Link"]
            val state = getPageConfig(links)

            val businesses = response.body() ?: emptyList()
            businessesDao.insertOrReplace(businesses)

            val nextKey = state.first
            val prevKey = state.second
            LoadResult.Page(data = businesses, prevKey = prevKey, nextKey = nextKey)
        } catch (e: Exception) {
            Log.e(TAG, "load -> Error retrieving data", e)
            LoadResult.Error(Throwable("Could not retrieve data"))
        }
    }

    fun getPageConfig(source: String?): Pair<Int?, Int?> {
        val pattern = Pattern.compile("""_page=([0-9]+)>; rel="prev"""")
        val patternNext = Pattern.compile("""_page=([0-9]+)>; rel="next"""")
        val matcher = source?.let { pattern.matcher(it) }
        val matcherNext = source?.let { patternNext.matcher(it) }
        return Pair(if (matcher?.find() == true) matcher.group().toInt() else null, if (matcherNext?.find() == true) matcherNext.group().toInt() else null)
    }
}