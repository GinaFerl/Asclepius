package com.dicoding.asclepius.retrofit

import com.dicoding.asclepius.api.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top-headlines")
    fun getNews(
        @Query("q") query: String = "cancer",
        @Query("category") category: String = "health",
        @Query("language") language: String = "en",
        @Query("apiKey") apiKey: String = "f988b342152d4e1f9f1b6fa6f979f867"
    ): Call<NewsResponse>
}