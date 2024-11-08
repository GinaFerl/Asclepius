package com.dicoding.asclepius.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.api.NewsItem
import com.dicoding.asclepius.api.NewsRepository

class NewsViewModel: ViewModel() {
    private val newsRepository = NewsRepository()

    private val _news = MutableLiveData<List<NewsItem?>>()
    val news: LiveData<List<NewsItem?>> get() = _news

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        fetchHealthNews()
    }

    fun fetchHealthNews() {
        _isLoading.value = true
        newsRepository.getHealthNews(
            onSuccess = { newsItems ->
                _news.value = newsItems
                _isLoading.value = false
            },
            onFailure = { errorMessage ->
                _isLoading.value = false
            }
        )
    }
}