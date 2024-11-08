package com.dicoding.asclepius.view

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.asclepius.R
import com.dicoding.asclepius.adapter.NewsAdapter
import com.dicoding.asclepius.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {
    private var _binding: ActivityNewsBinding? = null
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]

        newsAdapter = NewsAdapter()
        binding.rvNews.layoutManager = LinearLayoutManager(this)

        newsViewModel.isLoading.observe(this) { isLoading ->
            showLoading(isLoading)
        }

        newsViewModel.news.observe(this ) {news ->
            newsAdapter.submitList(news)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.pbNews.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}