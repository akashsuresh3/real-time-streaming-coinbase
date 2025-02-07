package com.example.streamingapp.ui.perplexity

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import kotlin.math.ln
import kotlin.math.pow
import androidx.navigation.NavController

@Composable
fun PerplexityScreen(navController: NavController) {
    var inputText by remember { mutableStateOf("") }
    val perplexityResult by remember(inputText) { derivedStateOf { calculatePerplexity(inputText) } }

    // Show error only if the user has typed something but input is invalid
    val showError = inputText.isNotEmpty() && perplexityResult == null
    val errorMessage = if (showError) "Invalid input. Enter comma-separated probabilities between 0 and 1." else null

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Perplexity Calculator", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = inputText,
            onValueChange = { inputText = it },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth().padding(8.dp).border(1.dp, Color.Gray).padding(8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display Error Message only if user has started typing
        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(8.dp))
        }

        perplexityResult?.let {
            Text(text = "Perplexity: $it", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back to Coinbase")
        }
    }
}

// Function to Calculate Perplexity
fun calculatePerplexity(input: String): Double? {
    val probabilities = input.split(",").mapNotNull {
        it.trim().toDoubleOrNull()?.takeIf { prob -> prob in 0.0..1.0 }
    }

    if (probabilities.isEmpty() || probabilities.sum() <= 0) return null

    val entropy = probabilities.sumOf { p -> if (p > 0) p * ln(p) / ln(2.0) else 0.0 }
    return 2.0.pow(-entropy)
}
