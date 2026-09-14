package com.forexjournal.app.data

/**
 * Mirrors the columns in the "Trades" tab of the Google Sheet exactly:
 * Date | Pair | Direction | Entry | Exit | Size | StopLoss | TakeProfit | PnL | Session | Notes
 */
data class Trade(
    val date: String = "",
    val pair: String = "",
    val direction: String = "Long", // "Long" or "Short"
    val entry: Double? = null,
    val exit: Double? = null,
    val size: Double? = null,
    val stopLoss: Double? = null,
    val takeProfit: Double? = null,
    val pnl: Double = 0.0,
    val session: String = "",
    val notes: String = ""
) {
    val isWin: Boolean get() = pnl >= 0
    val isLong: Boolean get() = direction.equals("Long", ignoreCase = true)
}
