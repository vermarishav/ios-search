package com.example.iossearch.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iossearch.data.VoiceState
import com.example.iossearch.ui.components.IOSSearchBar
import com.example.iossearch.ui.components.RecentSearchesSection
import com.example.iossearch.ui.components.SuggestionList
import com.example.iossearch.ui.components.VoiceSearchOverlay
import com.example.iossearch.ui.theme.IOSType
import com.example.iossearch.ui.theme.rememberIOSPalette
import com.example.iossearch.viewmodel.SearchScreenState
import com.example.iossearch.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    voiceState: VoiceState,
    voicePartialText: String,
    voiceError: String?,
    isVoiceOverlayVisible: Boolean,
    onMicClick: () -> Unit,
    onDismissVoiceOverlay: () -> Unit
) {
    val palette = rememberIOSPalette()

    Scaffold(
        containerColor = palette.background,
        topBar = {
            Column(modifier = Modifier.background(palette.background)) {
                Text(
                    text = "Search",
                    style = IOSType.LargeTitle.copy(color = palette.label),
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
                )
                IOSSearchBar(
                    query = viewModel.query,
                    isActive = viewModel.isActive,
                    palette = palette,
                    onQueryChange = viewModel::onQueryChange,
                    onActivate = viewModel::onActivate,
                    onCancel = viewModel::onCancel,
                    onClear = viewModel::onClearQuery,
                    onSubmit = { viewModel.onSubmitSearch() },
                    onMicClick = onMicClick
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                !viewModel.isActive -> IdleState(palette)

                viewModel.screenState == SearchScreenState.ACTIVE_EMPTY -> {
                    RecentSearchesSection(
                        recents = viewModel.recents,
                        palette = palette,
                        onSelect = viewModel::onSelectRecent,
                        onRemove = viewModel::onRemoveRecent,
                        onClearAll = viewModel::onClearAllRecents
                    )
                }

                viewModel.screenState == SearchScreenState.ACTIVE_TYPING -> {
                    // Brief debounce window before results land — shows nothing new yet,
                    // avoiding a flash of "no results" while the user is still typing.
                }

                viewModel.screenState == SearchScreenState.RESULTS -> {
                    SuggestionList(
                        suggestions = viewModel.suggestions,
                        palette = palette,
                        onSelect = { suggestion ->
                            viewModel.onSubmitSearch(suggestion.title)
                        }
                    )
                }

                viewModel.screenState == SearchScreenState.NO_RESULTS -> {
                    NoResultsState(query = viewModel.query, palette = palette)
                }
            }

            AnimatedVisibility(
                visible = isVoiceOverlayVisible,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                VoiceSearchOverlay(
                    state = voiceState,
                    partialText = voicePartialText,
                    errorMessage = voiceError,
                    palette = palette,
                    onDismiss = onDismissVoiceOverlay
                )
            }
        }
    }
}

@Composable
private fun IdleState(palette: com.example.iossearch.ui.theme.IOSPalette) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.SearchOff,
            contentDescription = null,
            tint = palette.tertiaryLabel,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Tap the search bar to get started",
            style = IOSType.Subhead.copy(color = palette.secondaryLabel)
        )
    }
}

@Composable
private fun NoResultsState(query: String, palette: com.example.iossearch.ui.theme.IOSPalette) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Filled.SearchOff,
            contentDescription = null,
            tint = palette.tertiaryLabel,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "No Results",
            style = IOSType.Headline.copy(color = palette.label)
        )
        Text(
            text = "No results found for \u201c$query\u201d",
            style = IOSType.Subhead.copy(color = palette.secondaryLabel),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
