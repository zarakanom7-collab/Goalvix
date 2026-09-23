package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import com.example.data.FirebasePredictionService
import com.example.data.PredictionConstants
import com.example.data.PredictionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoilApi::class)
class GoalvixViewModel(application: Application) : AndroidViewModel(application) {

    private val predictionService = FirebasePredictionService(application.applicationContext)

    // Prediction states
    private val _freePredictionState = MutableStateFlow<PredictionState>(PredictionState.Loading)
    val freePredictionState: StateFlow<PredictionState> = _freePredictionState.asStateFlow()

    private val _premiumPredictionState = MutableStateFlow<PredictionState>(PredictionState.Loading)
    val premiumPredictionState: StateFlow<PredictionState> = _premiumPredictionState.asStateFlow()

    // Local session state for Adsterra flow and Premium unlock
    private val _isAdFlowInitiated = MutableStateFlow(false)
    val isAdFlowInitiated: StateFlow<Boolean> = _isAdFlowInitiated.asStateFlow()

    private val _isPremiumUnlocked = MutableStateFlow(false)
    val isPremiumUnlocked: StateFlow<Boolean> = _isPremiumUnlocked.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadFreePrediction()
    }

    /**
     * Intent-based flow to open the Adsterra direct link externally.
     * Records local session state so Premium unlocks upon returning.
     */
    fun openAdsterraLink(context: Context) {
        _isAdFlowInitiated.value = true
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(PredictionConstants.ADSTERRA_DIRECT_LINK)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("GoalvixViewModel", "Failed to launch Adsterra intent", e)
            Toast.makeText(context, "Could not open browser link", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Called when returning to the app (e.g. Activity onResume or Lifecycle ON_RESUME).
     * Unlocks Premium content locally for this session without requiring accounts or payments.
     */
    fun onAppResumed() {
        if (_isAdFlowInitiated.value && !_isPremiumUnlocked.value) {
            unlockPremiumContent()
        }
    }

    /**
     * Fallback manual return button ("I'M BACK — VIEW PREMIUM").
     */
    fun onManualUnlockClicked() {
        unlockPremiumContent()
    }

    private fun unlockPremiumContent() {
        _isPremiumUnlocked.value = true
        loadPremiumPrediction()
    }

    suspend fun loadFreePredictionInternal(forceRefresh: Boolean = false) {
        if (!forceRefresh) {
            _freePredictionState.value = PredictionState.Loading
        }
        val result = predictionService.fetchPredictionImageUrl(
            PredictionConstants.FREE_PREDICTION_PATH,
            forceRefresh = forceRefresh
        )
        result.fold(
            onSuccess = { (url, timestamp) ->
                _freePredictionState.value = PredictionState.Success(url, timestamp)
            },
            onFailure = { error ->
                _freePredictionState.value = PredictionState.Error(
                    error.localizedMessage ?: "Prediction temporarily unavailable"
                )
            }
        )
    }

    fun loadFreePrediction(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            loadFreePredictionInternal(forceRefresh)
        }
    }

    suspend fun loadPremiumPredictionInternal(forceRefresh: Boolean = false) {
        if (!forceRefresh) {
            _premiumPredictionState.value = PredictionState.Loading
        }
        val result = predictionService.fetchPredictionImageUrl(
            PredictionConstants.PREMIUM_PREDICTION_PATH,
            forceRefresh = forceRefresh
        )
        result.fold(
            onSuccess = { (url, timestamp) ->
                _premiumPredictionState.value = PredictionState.Success(url, timestamp)
            },
            onFailure = { error ->
                _premiumPredictionState.value = PredictionState.Error(
                    error.localizedMessage ?: "Prediction temporarily unavailable"
                )
            }
        )
    }

    fun loadPremiumPrediction(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            loadPremiumPredictionInternal(forceRefresh)
        }
    }

    /**
     * Pull-to-refresh internal execution:
     * Clears local Coil image caches and fetches fresh image URLs with updated cache busters.
     */
    suspend fun refreshInternal() {
        _isRefreshing.value = true
        try {
            withContext(Dispatchers.IO) {
                val imageLoader = Coil.imageLoader(getApplication())
                imageLoader.memoryCache?.clear()
                imageLoader.diskCache?.clear()
            }
        } catch (e: Exception) {
            Log.w("GoalvixViewModel", "Failed to clear Coil cache: ${e.message}")
        }

        loadFreePredictionInternal(forceRefresh = true)
        if (_isPremiumUnlocked.value) {
            loadPremiumPredictionInternal(forceRefresh = true)
        }
        _isRefreshing.value = false
    }

    /**
     * Pull-to-refresh action invoked by UI.
     */
    fun refreshAll() {
        viewModelScope.launch {
            refreshInternal()
        }
    }
}
