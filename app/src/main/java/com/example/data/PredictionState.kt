package com.example.data

sealed interface PredictionState {
    data object Loading : PredictionState
    data class Success(
        val imageUrl: String,
        val lastUpdatedMillis: Long = 0L
    ) : PredictionState
    data class Error(val message: String) : PredictionState
}

object PredictionConstants {
    const val FREE_PREDICTION_PATH = "content/free-prediction.jpg"
    const val PREMIUM_PREDICTION_PATH = "content/premium-prediction.jpg"
    const val ADSTERRA_DIRECT_LINK = "https://www.profitableratecpmnetwork.com/y1enfm0d8?key=47285005a2020c1a4a432cbd050bacf7"
    const val TELEGRAM_CHANNEL_URL = "https://t.me/goalvix"
}
