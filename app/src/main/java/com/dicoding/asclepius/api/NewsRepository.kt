package com.dicoding.asclepius.api

import com.dicoding.asclepius.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsRepository {
    fun getHealthNews(
        onSuccess: (List<NewsItem?>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        ApiConfig.getApiService().getNews("cancer", "health", "en", "f988b342152d4e1f9f1b6fa6f979f867")
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val articles = response.body()?.articles ?: emptyList()
                        val newsItems = articles.map { article ->
                            if (!article.urlToImage.isNullOrEmpty() && !article.url.isNullOrEmpty() && !article.title.isNullOrEmpty()) {
                                NewsItem(
                                    title = article.title,
                                    urlToImage = article.urlToImage,
                                    url = article.url
                                )
                            } else {
                                null
                            }
                        }
                        onSuccess(newsItems)
                    } else {
                        onFailure("Failed to fetch")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    onFailure(t.message ?: "Unknown error")
                }
            })
    }
}