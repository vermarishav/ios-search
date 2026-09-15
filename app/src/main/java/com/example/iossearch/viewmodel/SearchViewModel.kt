package com.example.iossearch.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.iossearch.data.RecentSearch
import com.example.iossearch.data.RecentSearchStore
import com.example.iossearch.data.SearchCorpus
import com.example.iossearch.data.SearchSuggestion
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

enum class SearchScreenState { INACTIVE, ACTIVE_EMPTY, ACTIVE_TYPING, RESULTS, NO_RESULTS }

class SearchViewModel(private val store: RecentSearchStore) : ViewModel() {

    var query by mutableStateOf("")
        private set

    var isActive by mutableStateOf(false)
        private set

    var suggestions by mutableStateOf<List<SearchSuggestion>>(emptyList())
        private set

    var recents by mutableStateOf<List<RecentSearch>>(emptyList())
        private set

    var screenState by mutableStateOf(SearchScreenState.INACTIVE)
        private set

    private var debounceJob: Job? = null

    init {
        store.recentSearches
            .onEach { recents = it }
            .launchIn(viewModelScope)
    }

    fun onActivate() {
        isActive = true
        screenState = if (query.isBlank()) SearchScreenState.ACTIVE_EMPTY else SearchScreenState.ACTIVE_TYPING
    }

    fun onCancel() {
        isActive = false
        query = ""
        suggestions = emptyList()
        screenState = SearchScreenState.INACTIVE
        debounceJob?.cancel()
    }

    fun onQueryChange(newQuery: String) {
        query = newQuery
        debounceJob?.cancel()

        if (newQuery.isBlank()) {
            suggestions = emptyList()
            screenState = SearchScreenState.ACTIVE_EMPTY
            return
        }

        screenState = SearchScreenState.ACTIVE_TYPING
        // Debounce like iOS's live search-as-you-type, avoiding a query per keystroke.
        debounceJob = viewModelScope.launch {
            delay(180)
            val results = SearchCorpus.query(newQuery)
            suggestions = results
            screenState = if (results.isEmpty()) SearchScreenState.NO_RESULTS else SearchScreenState.RESULTS
        }
    }

    fun onClearQuery() {
        query = ""
        suggestions = emptyList()
        screenState = SearchScreenState.ACTIVE_EMPTY
    }

    fun onSubmitSearch(text: String = query) {
        if (text.isBlank()) return
        viewModelScope.launch { store.add(text) }
        screenState = SearchScreenState.RESULTS
    }

    fun onSelectRecent(recent: RecentSearch) {
        query = recent.query
        onSubmitSearch(recent.query)
    }

    fun onRemoveRecent(id: Long) {
        viewModelScope.launch { store.remove(id) }
    }

    fun onClearAllRecents() {
        viewModelScope.launch { store.clearAll() }
    }

    fun onVoiceResult(text: String) {
        query = text
        onQueryChange(text)
        onSubmitSearch(text)
    }
}
