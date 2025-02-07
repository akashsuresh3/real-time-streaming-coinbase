package com.example.streamingapp.ui.service

import okhttp3.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class WebSocketService {
    private val client = OkHttpClient.Builder()
        .readTimeout(5000, TimeUnit.MILLISECONDS)
        .build()

    private var webSocket: WebSocket? = null

    private val _messages = MutableSharedFlow<String>(replay = 0, extraBufferCapacity = 64)
    val messages = _messages.asSharedFlow()


//    private val COINBASE_WS_URL = "wss://ws-feed-public.sandbox.exchange.coinbase.com"
private val COINBASE_WS_URL = "wss://ws-feed.exchange.coinbase.com"

    fun connect() {
        val request = Request.Builder().url(COINBASE_WS_URL).build()
        webSocket = client.newWebSocket(request, object : WebSocketListener() {

            override fun onOpen(webSocket: WebSocket, response: Response) {
                println("WebSocket Connected to Coinbase")
                subscribeToTicker() // Subscribe to Bitcoin market data
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val jsonObject = JSONObject(text)
                val messageType = jsonObject.optString("type")

                when (messageType) {
                    "subscriptions" -> {
                        println("Subscription successful: $text")
                        _messages.tryEmit("Subscription confirmed for: ${jsonObject.optJSONArray("channels")}")
                    }
                    "ticker" -> {
                        val productId = jsonObject.optString("product_id")
                        val price = jsonObject.optString("price")
                        val message = "Ticker Update for $productId: $price"
                        println(message)
                        _messages.tryEmit(message)
                    }
                    "heartbeat" -> {
                        val productId = jsonObject.optString("product_id")
                        val time = jsonObject.optString("time")
                        val message = "Heartbeat for $productId at $time"
                        println(message)
                        _messages.tryEmit(message)
                    }
                    "l2update" -> {
                        val productId = jsonObject.optString("product_id")
                        val changes = jsonObject.optJSONArray("changes")
                        val message = "Level2 Update for $productId: $changes"
                        println(message)
                        _messages.tryEmit(message)
                    }
                    else -> {
                        println("Unhandled message type: $text")
                    }
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                println("WebSocket Error: ${t.localizedMessage}")
                reconnect()
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                println("WebSocket Closing: $reason")
                webSocket.close(1000, null)
            }
        })
    }


    private fun subscribeToTicker() {
        // Create a JSON array for product IDs
        val productIdsArray = JSONArray().apply {
//            put("ETH-USD")
//            put("ETH-EUR")
            put("BTC-USD")
        }

        // Create the ticker channel with its own product IDs array
        val tickerChannel = JSONObject().apply {
            put("name", "ticker")
            put("product_ids", JSONArray().apply {
//                put("ETH-USD")
                put("BTC-USD")
            })
        }

        // Create a JSON array for channels
        val channelsArray = JSONArray().apply {
//            put("level2") -> requires authentication
//            put("heartbeat")
//            put("ticker")
            put("matches")
            put(tickerChannel)
        }

        // Final subscription message
        val subscribeMessage = JSONObject().apply {
            put("type", "subscribe")
            put("product_ids", productIdsArray)
            put("channels", channelsArray)
        }

        println("Sending subscription message: $subscribeMessage")
        webSocket?.send(subscribeMessage.toString())
    }


    private fun reconnect() {
        println("Reconnecting WebSocket...")
        disconnect()
        connect()
    }

    fun disconnect() {
        webSocket?.close(1000, "Closing Connection")
        webSocket = null
    }
}

