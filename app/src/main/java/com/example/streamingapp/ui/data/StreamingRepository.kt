package com.example.streamingapp.ui.data

import android.util.Log
import com.example.streamingapp.ui.service.WebSocketService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.StateFlow

class StreamingRepository {
    private val webSocketService = WebSocketService()
    private val _marketData = MutableStateFlow<List<String>>(emptyList())
    val marketData: StateFlow<List<String>> = _marketData.asStateFlow()

    init {
        webSocketService.connect()
    }

    suspend fun startListening() {
        Log.d("StreamingRepository", "started listening in StreamingRepository")
        var isFirstMessage = true
        webSocketService.messages.collect { message ->
            if (isFirstMessage) {
                isFirstMessage = false // // I want to remove the first message as it is just a confirmation message and not the ticker update
            } else {
                _marketData.value = _marketData.value + message
            }
        }
    }

    fun stopListening() {
        webSocketService.disconnect()
    }
}
