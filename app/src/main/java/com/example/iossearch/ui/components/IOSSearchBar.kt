package com.example.iossearch.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.iossearch.ui.theme.IOSPalette
import com.example.iossearch.ui.theme.IOSType
import kotlinx.coroutines.delay

/**
 * Replicates the iOS UISearchBar / SwiftUI .searchable() field:
 * - Rounded, tinted capsule field with centered placeholder that left-aligns on focus
 * - Magnifying glass leading icon
 * - Clear ("x-in-circle") trailing icon, shown only when text is present
 * - "Cancel" button that slides in from the right on activation
 * - Mic icon for voice dictation, shown when field is empty (as in iOS Spotlight)
 */
@Composable
fun IOSSearchBar(
    query: String,
    isActive: Boolean,
    palette: IOSPalette,
    onQueryChange: (String) -> Unit,
    onActivate: () -> Unit,
    onCancel: () -> Unit,
    onClear: () -> Unit,
    onSubmit: () -> Unit,
    onMicClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    val fieldHeight = 36.dp

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(fieldHeight)
                .clip(RoundedCornerShape(10.dp))
                .background(palette.searchField)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { if (!isActive) onActivate() },
            contentAlignment = if (query.isEmpty() && !isActive) Alignment.Center else Alignment.CenterStart
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search",
                    tint = palette.secondaryLabel,
                    modifier = Modifier.size(18.dp)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Search",
                            style = IOSType.Body.copy(
                                color = palette.secondaryLabel,
                                textAlign = if (isActive) TextAlign.Start else TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                            .onFocusChanged {
                                if (it.isFocused && !isActive) onActivate()
                            },
                        singleLine = true,
                        textStyle = IOSType.Body.copy(color = palette.label),
                        cursorBrush = SolidColor(palette.accent),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Search,
                            keyboardType = KeyboardType.Text
                        ),
                        keyboardActions = KeyboardActions(onSearch = { onSubmit() })
                    )
                }

                if (query.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Filled.Cancel,
                        contentDescription = "Clear search",
                        tint = palette.secondaryLabel,
                        modifier = Modifier
                            .size(16.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onClear() }
                    )
                } else if (isActive) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Voice search",
                        tint = palette.accent,
                        modifier = Modifier
                            .size(18.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onMicClick() }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isActive,
            enter = fadeIn(tween(220)) + expandHorizontally(tween(220)),
            exit = fadeOut(tween(180)) + shrinkHorizontally(tween(180))
        ) {
            Text(
                text = "Cancel",
                style = IOSType.Body.copy(color = palette.accent),
                modifier = Modifier
                    .padding(start = 10.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCancel() }
            )
        }
    }

    LaunchedEffect(isActive) {
        if (isActive) {
            delay(80)
            focusRequester.requestFocus()
        }
    }
}
