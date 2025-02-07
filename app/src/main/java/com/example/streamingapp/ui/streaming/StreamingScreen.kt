package com.example.streamingapp.ui.streaming

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun StreamingScreen(viewModel: StreamingViewModel = viewModel(), navController: NavController) {
    val marketData by viewModel.marketData.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Coinbase Market Data", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.navigate("perplexity") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
        ) {
            Text("Go to Perplexity Calculator")
        }

        if (marketData.isEmpty()) {
            Text("Waiting for data...", style = MaterialTheme.typography.bodyLarge)
        } else {
            LazyColumn(modifier = Modifier.weight(1f).padding(bottom = 30.dp)) {
                items(marketData) { data ->
                    Card(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                        Text(text = data, modifier = Modifier.padding(8.dp))
                    }
                }
            }
        }
    }
}


