package com.example.iossearch.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.iossearch.data.VoiceState
import com.example.iossearch.ui.theme.IOSPalette
import com.example.iossearch.ui.theme.IOSType

/**
 * Full-bleed dictation overlay analogous to iOS's microphone dictation sheet:
 * a pulsing mic affordance, live partial transcript, and tap-to-cancel.
 */
@Composable
fun VoiceSearchOverlay(
    state: VoiceState,
    partialText: String,
    errorMessage: String?,
    palette: IOSPalette,
    onDismiss: () -> Unit
) {
    val transition = rememberInfiniteTransition(label = "mic-pulse")
    val pulse by transition.animateFloat(
        initialValue = 1f,
        targetValue = if (state == VoiceState.LISTENING) 1.25f else 1f,
        animationSpec = infiniteRepeatable(tween(700), RepeatMode.Reverse),
        label = "pulse-scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(palette.background.copy(alpha = 0.98f))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onDismiss()
            },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(pulse)
                    .background(
                        color = if (state == VoiceState.ERROR) palette.destructive.copy(alpha = 0.15f)
                        else palette.accent.copy(alpha = 0.15f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(
                            if (state == VoiceState.ERROR) palette.destructive else palette.accent,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Listening",
                        tint = Color.White,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Text(
                text = when (state) {
                    VoiceState.LISTENING -> "Listening..."
                    VoiceState.PROCESSING -> "Working on it..."
                    VoiceState.ERROR -> errorMessage ?: "Something went wrong"
                    VoiceState.IDLE -> "Tap to speak"
                },
                style = IOSType.Headline.copy(color = palette.label),
                modifier = Modifier.padding(top = 20.dp)
            )

            if (partialText.isNotBlank()) {
                Text(
                    text = partialText,
                    style = IOSType.Body.copy(color = palette.secondaryLabel),
                    modifier = Modifier.padding(top = 8.dp, start = 32.dp, end = 32.dp)
                )
            }

            Text(
                text = "Tap anywhere to cancel",
                style = IOSType.Footnote.copy(color = palette.tertiaryLabel),
                modifier = Modifier.padding(top = 28.dp)
            )
        }
    }
}
