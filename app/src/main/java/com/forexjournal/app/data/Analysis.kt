package com.forexjournal.app.data

/**
 * Mirrors the JSON shape returned by the Gemini analysis stored in the
 * AI_Analysis sheet tab and served by the Apps Script doGet endpoint.
 */
data class Analysis(
    val summary: String = "",
    val winRate: Double = 0.0,
    val bestPair: String = "—",
    val worstPair: String = "—",
    val bestSession: String = "—",
    val patterns: List<String> = emptyList(),
    val riskFlags: List<String> = emptyList(),
    val recommendations: List<String> = emptyList()
)
