package com.example.streamingapp.ui.streaming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.streamingapp.ui.data.StreamingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StreamingViewModel : ViewModel() {
    private val repository = StreamingRepository()
    val marketData = repository.marketData

    init {
        viewModelScope.launch {
            repository.startListening()
        }
    }

    override fun onCleared() {
        super.onCleared()
        repository.stopListening()
    }
}

