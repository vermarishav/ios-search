package com.example.iossearch.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.iossearch.data.RecentSearchStore

class SearchViewModelFactory(private val store: RecentSearchStore) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SearchViewModel(store) as T
    }
}
