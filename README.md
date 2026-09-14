# Trade Journal Analyst — Android App (Native Jetpack Compose)

A fully native Android app — no WebView, no HTML/JS anywhere. Built with
Kotlin and Jetpack Compose end to end: real state management (StateFlow +
ViewModel), real Canvas-drawn charts, real native navigation.

## Architecture

```
data/
  Trade.kt              - data model matching the Google Sheet columns
  Analysis.kt            - data model matching the Gemini analysis JSON
  Metrics.kt              - pure calculation functions (win rate, profit factor,
                            Kelly criterion, drawdown, streaks, pair/session stats)
  TradeRepository.kt      - GET (fetch trades+analysis) and POST (log a trade)
                            against your Apps Script Web App, using plain
                            HttpURLConnection + Android's built-in org.json
                            (deliberately no Retrofit/OkHttp/Gson, to keep the
                            dependency surface small)

viewmodel/
  DashboardViewModel.kt   - owns UI state, persists your Web App URL via
                            SharedPreferences, exposes loadData()/submitTrade()

ui/
  theme/                  - dark color scheme matching the original dashboard
                            (teal/green/red/amber on near-black)
  components/             - WinRateGauge (Canvas arc), EquityChart (Canvas
                            path), KpiCard, AiBriefingCard, TradeRow,
                            LogTradeDialog, TradeDetailDialog
  screens/                - OverviewScreen, TradesScreen, CalendarScreen

MainActivity.kt           - Scaffold with bottom navigation, pull-to-refresh,
                            floating "+ Log Execution" button
```

## What's included

- **Overview tab** — win-rate gauge, AI analyst briefing (real Gemini output,
  not placeholder text), KPI grid (Net P&L, Profit Factor, Expectancy, Kelly
  Criterion, Max Drawdown, Discipline Score), equity curve with underwater
  drawdown, pair attribution, session performance
- **Trades tab** — filterable ledger (by session), tap any trade for detail
- **Calendar tab** — month grid colored by daily P&L, tap a day to see that
  day's trades
- **Log Execution** — floating action button opens a form that POSTs a new
  trade straight to your Google Sheet via the Apps Script `doPost` endpoint
- **Pull to refresh**, persisted Web App URL, loading/error/empty states

## What's intentionally trimmed from the original web dashboard

To ship something real rather than something half-finished, these were left
out of this first native version — they're straightforward to add later if
you want them:
- CSV import/export
- The deeper "Analytics" tab (monthly bars, day-of-week breakdown, long/short
  donut chart) — the core numbers all still appear elsewhere (Overview +
  Trades + Calendar cover the same underlying data)
- Local/offline trade caching when the network is unreachable

## How to open and run this

1. Install [Android Studio](https://developer.android.com/studio) if you
   don't have it.
2. Open Android Studio → **Open** → select this `ForexTradeAnalyst` folder.
3. Let it sync (downloads Gradle + dependencies automatically the first time).
4. Plug in a phone with USB debugging enabled, or start an emulator.
5. Click **Run ▶**.

## A note on reliability

This project was written without the ability to compile or run it in the
environment that produced it — there's a real chance Android Studio flags a
small issue on first build (an import, an API signature mismatch). If that
happens, copy the exact error text back and it can be fixed immediately —
these are typically one-line fixes once the compiler points at them.

## Command-line builds and CI

`gradlew` / `gradlew.bat` are included, along with a GitHub Actions workflow
at `.github/workflows/android-ci.yml` that builds a debug APK on every push
and pull request to `main`.

`gradle/wrapper/gradle-wrapper.jar` (a small compiled binary Gradle normally
generates) isn't included — see `gradle/wrapper/README_MISSING_JAR.md` for
why, and how to generate it in one click from Android Studio if you want
`./gradlew` to work from a terminal. The CI workflow installs Gradle directly
instead, so it works without that file.

To push this to GitHub:
```
cd ForexTradeAnalyst
git init
git add .
git commit -m "Initial commit — native Compose rewrite"
git branch -M main
git remote add origin <your-repo-url>
git push -u origin main
```
