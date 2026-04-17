package com.benatt.datapagingapp.data

import com.benatt.datapagingapp.data.models.Business
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author ben-mathu
 * @since 2/23/25
 */
interface BusinessApiService {
    @GET("businesses")
    suspend fun getBusinesses(@Query("_limit") limit: Int, @Query("_page") nextPageNumber: Int): Response<List<Business>>
}