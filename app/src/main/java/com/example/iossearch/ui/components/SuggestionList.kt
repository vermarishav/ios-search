package com.example.iossearch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.iossearch.data.SearchSuggestion
import com.example.iossearch.data.SuggestionCategory
import com.example.iossearch.ui.theme.IOSPalette
import com.example.iossearch.ui.theme.IOSType

@Composable
fun SuggestionList(
    suggestions: List<SearchSuggestion>,
    palette: IOSPalette,
    onSelect: (SearchSuggestion) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(suggestions, key = { it.id }) { suggestion ->
            SuggestionRow(suggestion, palette, onClick = { onSelect(suggestion) })
        }
    }
}

@Composable
private fun SuggestionRow(
    suggestion: SearchSuggestion,
    palette: IOSPalette,
    onClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(categoryTint(suggestion.category, palette)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon(suggestion.category),
                    contentDescription = null,
                    tint = androidx.compose.ui.graphics.Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            Column(modifier = Modifier.padding(start = 12.dp).weight(1f)) {
                Text(
                    text = suggestion.title,
                    style = IOSType.Body.copy(color = palette.label),
                    maxLines = 1
                )
                Text(
                    text = suggestion.category.label,
                    style = IOSType.Footnote.copy(color = palette.secondaryLabel)
                )
            }
        }
        androidx.compose.material3.Divider(
            color = palette.separator,
            thickness = 0.5.dp,
            modifier = Modifier.padding(start = 58.dp)
        )
    }
}

private fun categoryIcon(category: SuggestionCategory) = when (category) {
    SuggestionCategory.APP -> Icons.Filled.Apps
    SuggestionCategory.CONTACT -> Icons.Filled.Person
    SuggestionCategory.SETTINGS -> Icons.Filled.Settings
    SuggestionCategory.WEB, SuggestionCategory.TOP_HIT -> Icons.Filled.Search
}

private fun categoryTint(category: SuggestionCategory, palette: IOSPalette) = when (category) {
    SuggestionCategory.APP -> androidx.compose.ui.graphics.Color(0xFF34C759)
    SuggestionCategory.CONTACT -> androidx.compose.ui.graphics.Color(0xFFFF9500)
    SuggestionCategory.SETTINGS -> palette.gray
    SuggestionCategory.WEB, SuggestionCategory.TOP_HIT -> palette.accent
}
