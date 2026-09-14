package com.forexjournal.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

sealed class FetchResult {
    data class Success(
        val trades: List<Trade>,
        val analysis: Analysis?,
        val generatedAt: String?
    ) : FetchResult()

    data class Error(val message: String) : FetchResult()
}

class TradeRepository {

    suspend fun fetchData(urlStr: String): FetchResult = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlStr)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 15000
                readTimeout = 15000
            }

            val code = connection.responseCode
            if (code !in 200..299) {
                return@withContext FetchResult.Error("HTTP $code")
            }

            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)

            val tradesArr = json.optJSONArray("trades") ?: JSONArray()
            val trades = mutableListOf<Trade>()
            for (i in 0 until tradesArr.length()) {
                val o = tradesArr.optJSONObject(i) ?: continue
                val pair = o.optString("Pair", "")
                if (pair.isBlank()) continue // skip blank/legend rows, same guard as the sheet-side script

                trades.add(
                    Trade(
                        date = o.optString("Date", ""),
                        pair = pair,
                        direction = o.optString("Direction", "Long"),
                        entry = o.optDoubleOrNull("Entry"),
                        exit = o.optDoubleOrNull("Exit"),
                        size = o.optDoubleOrNull("Size"),
                        stopLoss = o.optDoubleOrNull("StopLoss"),
                        takeProfit = o.optDoubleOrNull("TakeProfit"),
                        pnl = o.optDouble("PnL", 0.0),
                        session = o.optString("Session", ""),
                        notes = o.optString("Notes", "")
                    )
                )
            }

            val analysisObj = json.optJSONObject("analysis")
            val analysis = analysisObj?.let {
                Analysis(
                    summary = it.optString("summary", ""),
                    winRate = it.optDouble("winRate", 0.0),
                    bestPair = it.optString("bestPair", "—"),
                    worstPair = it.optString("worstPair", "—"),
                    bestSession = it.optString("bestSession", "—"),
                    patterns = it.optJSONArray("patterns").toStringList(),
                    riskFlags = it.optJSONArray("riskFlags").toStringList(),
                    recommendations = it.optJSONArray("recommendations").toStringList()
                )
            }

            val generatedAt = json.optString("generatedAt", "").ifBlank { null }

            FetchResult.Success(trades, analysis, generatedAt)
        } catch (e: Exception) {
            FetchResult.Error(e.message ?: "Unknown network error")
        } finally {
            connection?.disconnect()
        }
    }

    suspend fun postTrade(urlStr: String, trade: Trade): Result<Unit> = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(urlStr)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                connectTimeout = 15000
                readTimeout = 15000
            }

            val payload = JSONObject().apply {
                put("Date", trade.date)
                put("Pair", trade.pair)
                put("Direction", trade.direction)
                put("Entry", trade.entry ?: JSONObject.NULL)
                put("Exit", trade.exit ?: JSONObject.NULL)
                put("Size", trade.size ?: JSONObject.NULL)
                put("StopLoss", trade.stopLoss ?: JSONObject.NULL)
                put("TakeProfit", trade.takeProfit ?: JSONObject.NULL)
                put("PnL", trade.pnl)
                put("Session", trade.session)
                put("Notes", trade.notes)
            }

            connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }

            val code = connection.responseCode
            val stream = if (code in 200..299) connection.inputStream else connection.errorStream
            val body = stream?.bufferedReader()?.use { it.readText() } ?: ""
            val result = JSONObject(body)

            if (result.optBoolean("ok", false)) {
                Result.success(Unit)
            } else {
                Result.failure(Exception(result.optString("error", "Server rejected the trade")))
            }
        } catch (e: Exception) {
            Result.failure(e)
        } finally {
            connection?.disconnect()
        }
    }
}

// ---------- small JSON helpers ----------

private fun JSONObject.optDoubleOrNull(key: String): Double? {
    if (!has(key) || isNull(key)) return null
    val v = optDouble(key, Double.NaN)
    return if (v.isNaN()) null else v
}

private fun JSONArray?.toStringList(): List<String> {
    if (this == null) return emptyList()
    val list = mutableListOf<String>()
    for (i in 0 until length()) list.add(optString(i, ""))
    return list
}
