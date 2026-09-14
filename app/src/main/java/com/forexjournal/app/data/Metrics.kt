package com.forexjournal.app.data

import kotlin.math.abs

data class PairStat(val pair: String, val pnl: Double, val count: Int, val winRate: Int)
data class SessionStat(val session: String, val pnl: Double, val count: Int, val winRate: Int)

data class Metrics(
    val count: Int,
    val totalPnL: Double,
    val winRate: Int,
    val wins: Int,
    val losses: Int,
    val profitFactor: Double, // Double.POSITIVE_INFINITY when there are no losses
    val avgWin: Double,
    val avgLoss: Double,
    val expectancy: Double,
    val kelly: Double, // fraction, e.g. 0.12 = 12%
    val equity: List<Double>,
    val drawdowns: List<Double>,
    val maxDrawdown: Double,
    val longWinRate: Int,
    val longCount: Int,
    val shortWinRate: Int,
    val shortCount: Int,
    val streakCount: Int,
    val streakIsWin: Boolean,
    val slCompliancePct: Int,
    val pairStats: List<PairStat>,
    val sessionStats: List<SessionStat>,
    val dailyPnl: Map<String, Double> // date string -> net pnl that day
)

object MetricsCalculator {

    fun compute(trades: List<Trade>): Metrics? {
        if (trades.isEmpty()) return null

        val totalPnL = trades.sumOf { it.pnl }
        val wins = trades.filter { it.pnl > 0 }
        val losses = trades.filter { it.pnl <= 0 }
        val winRate = wins.size * 100 / trades.size

        val grossProfit = wins.sumOf { it.pnl }
        val grossLoss = abs(losses.sumOf { it.pnl })
        val profitFactor = when {
            grossLoss > 0 -> grossProfit / grossLoss
            grossProfit > 0 -> Double.POSITIVE_INFINITY
            else -> 0.0
        }
        val avgWin = if (wins.isNotEmpty()) grossProfit / wins.size else 0.0
        val avgLoss = if (losses.isNotEmpty()) grossLoss / losses.size else 0.0
        val payoff = if (avgLoss > 0) avgWin / avgLoss else 0.0
        val expectancy = totalPnL / trades.size

        val wDec = winRate / 100.0
        val kelly = if (payoff > 0) (wDec - (1 - wDec) / payoff).coerceIn(-1.0, 1.0) else 0.0

        var running = 0.0
        var peak = Double.NEGATIVE_INFINITY
        val equity = mutableListOf<Double>()
        val drawdowns = mutableListOf<Double>()
        for (t in trades) {
            running += t.pnl
            if (running > peak) peak = running
            equity.add(running)
            drawdowns.add(peak - running)
        }
        val maxDD = drawdowns.maxOrNull() ?: 0.0

        val longTrades = trades.filter { it.isLong }
        val shortTrades = trades.filter { !it.isLong }
        val longWR = if (longTrades.isNotEmpty()) longTrades.count { it.pnl > 0 } * 100 / longTrades.size else 0
        val shortWR = if (shortTrades.isNotEmpty()) shortTrades.count { it.pnl > 0 } * 100 / shortTrades.size else 0

        val rev = trades.asReversed()
        val streakIsWin = rev.first().isWin
        var streakCount = 0
        for (t in rev) {
            if (t.isWin == streakIsWin) streakCount++ else break
        }

        val slCompliance = trades.count { it.stopLoss != null } * 100 / trades.size

        val pairStats = trades.groupBy { it.pair }.map { (pair, list) ->
            PairStat(
                pair = pair,
                pnl = list.sumOf { it.pnl },
                count = list.size,
                winRate = if (list.isNotEmpty()) list.count { it.pnl > 0 } * 100 / list.size else 0
            )
        }.sortedByDescending { it.pnl }

        val sessionStats = trades.groupBy { it.session }.map { (session, list) ->
            SessionStat(
                session = session,
                pnl = list.sumOf { it.pnl },
                count = list.size,
                winRate = if (list.isNotEmpty()) list.count { it.pnl > 0 } * 100 / list.size else 0
            )
        }.sortedByDescending { it.pnl }

        val dailyPnl = trades.groupBy { it.date }.mapValues { (_, list) -> list.sumOf { it.pnl } }

        return Metrics(
            count = trades.size,
            totalPnL = totalPnL,
            winRate = winRate,
            wins = wins.size,
            losses = losses.size,
            profitFactor = profitFactor,
            avgWin = avgWin,
            avgLoss = avgLoss,
            expectancy = expectancy,
            kelly = kelly,
            equity = equity,
            drawdowns = drawdowns,
            maxDrawdown = maxDD,
            longWinRate = longWR,
            longCount = longTrades.size,
            shortWinRate = shortWR,
            shortCount = shortTrades.size,
            streakCount = streakCount,
            streakIsWin = streakIsWin,
            slCompliancePct = slCompliance,
            pairStats = pairStats,
            sessionStats = sessionStats,
            dailyPnl = dailyPnl
        )
    }
}
