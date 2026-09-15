package com.example.iossearch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.iossearch.data.RecentSearchStore
import com.example.iossearch.data.VoiceSearchController
import com.example.iossearch.ui.SearchScreen
import com.example.iossearch.ui.theme.IOSSearchTheme
import com.example.iossearch.viewmodel.SearchViewModel
import com.example.iossearch.viewmodel.SearchViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var voiceController: VoiceSearchController

    private val requestMicPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) pendingMicLaunch?.invoke()
        pendingMicLaunch = null
    }

    private var pendingMicLaunch: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        voiceController = VoiceSearchController(applicationContext)
        val store = RecentSearchStore(applicationContext)

        setContent {
            IOSSearchTheme {
                val viewModel: SearchViewModel = viewModel(factory = SearchViewModelFactory(store))
                var showVoiceOverlay by remember { mutableStateOf(false) }

                SearchScreen(
                    viewModel = viewModel,
                    voiceState = voiceController.state,
                    voicePartialText = voiceController.partialText,
                    voiceError = voiceController.errorMessage,
                    isVoiceOverlayVisible = showVoiceOverlay,
                    onMicClick = {
                        showVoiceOverlay = true
                        startVoiceSearch {
                            viewModel.onVoiceResult(it)
                            showVoiceOverlay = false
                        }
                    },
                    onDismissVoiceOverlay = {
                        showVoiceOverlay = false
                        voiceController.stop()
                    }
                )
            }
        }
    }

    private fun startVoiceSearch(onResult: (String) -> Unit) {
        val hasPermission = ContextCompat.checkSelfPermission(
            this, Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            voiceController.start(onResult)
        } else {
            pendingMicLaunch = { voiceController.start(onResult) }
            requestMicPermission.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        voiceController.stop()
    }
}
