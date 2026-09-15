package com.example.iossearch.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.iossearch.data.RecentSearch
import com.example.iossearch.ui.theme.IOSPalette
import com.example.iossearch.ui.theme.IOSType

/**
 * Mirrors iOS Search's "Recents" list: a header with an inline "Clear" action,
 * and a scrollable list of past queries with a history-clock glyph.
 */
@Composable
fun RecentSearchesSection(
    recents: List<RecentSearch>,
    palette: IOSPalette,
    onSelect: (RecentSearch) -> Unit,
    onRemove: (Long) -> Unit,
    onClearAll: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Recents",
            style = IOSType.Headline.copy(color = palette.label)
        )
        if (recents.isNotEmpty()) {
            Text(
                text = "Clear",
                style = IOSType.Callout.copy(color = palette.accent),
                modifier = Modifier.clickable { onClearAll() }
            )
        }
    }

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(recents, key = { it.id }) { recent ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect(recent) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.History,
                        contentDescription = null,
                        tint = palette.secondaryLabel,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = recent.query,
                        style = IOSType.Body.copy(color = palette.label),
                        modifier = Modifier.padding(start = 12.dp),
                        maxLines = 1
                    )
                }
                Text(
                    text = "\u2715",
                    style = IOSType.Footnote.copy(color = palette.tertiaryLabel),
                    modifier = Modifier
                        .clickable { onRemove(recent.id) }
                        .padding(4.dp)
                )
            }
            androidx.compose.material3.Divider(
                color = palette.separator,
                thickness = 0.5.dp,
                modifier = Modifier.padding(start = 46.dp)
            )
        }
    }
}
