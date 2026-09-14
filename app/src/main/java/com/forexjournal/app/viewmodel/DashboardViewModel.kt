package com.forexjournal.app.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.forexjournal.app.data.Analysis
import com.forexjournal.app.data.FetchResult
import com.forexjournal.app.data.Metrics
import com.forexjournal.app.data.MetricsCalculator
import com.forexjournal.app.data.Trade
import com.forexjournal.app.data.TradeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,
    val trades: List<Trade> = emptyList(),
    val analysis: Analysis? = null,
    val metrics: Metrics? = null,
    val generatedAt: String? = null,
    val url: String = DashboardViewModel.DEFAULT_URL
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val repository = TradeRepository()

    private val _uiState = MutableStateFlow(UiState(url = prefs.getString(KEY_URL, null) ?: DEFAULT_URL))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun setUrl(newUrl: String) {
        if (newUrl.isBlank()) return
        prefs.edit().putString(KEY_URL, newUrl).apply()
        _uiState.update { it.copy(url = newUrl) }
        loadData()
    }

    fun loadData(isManualRefresh: Boolean = false) {
        val url = _uiState.value.url
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = !isManualRefresh, isRefreshing = isManualRefresh, error = null) }

            when (val result = repository.fetchData(url)) {
                is FetchResult.Success -> {
                    val metrics = MetricsCalculator.compute(result.trades)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isRefreshing = false,
                            trades = result.trades,
                            analysis = result.analysis,
                            metrics = metrics,
                            generatedAt = result.generatedAt,
                            error = null
                        )
                    }
                }
                is FetchResult.Error -> {
                    _uiState.update { it.copy(isLoading = false, isRefreshing = false, error = result.message) }
                }
            }
        }
    }

    fun submitTrade(trade: Trade, onResult: (success: Boolean, error: String?) -> Unit) {
        val url = _uiState.value.url
        viewModelScope.launch {
            val result = repository.postTrade(url, trade)
            result.onSuccess {
                onResult(true, null)
                loadData()
            }.onFailure { e ->
                onResult(false, e.message)
            }
        }
    }

    companion object {
        const val DEFAULT_URL =
            "https://script.google.com/macros/s/AKfycby9953ycUqa3gLBzxxG8nNcxKOPp4MMxoRH9BxKpy-CNCGdHprtm3E1H3JZQMmTZs_FlQ/exec"
        private const val PREFS_NAME = "trade_journal_prefs"
        private const val KEY_URL = "web_app_url"
    }
}
