# Trade Journal Analyst — Android App

A native Android app that wraps your Trade Journal dashboard in a Jetpack
Compose shell (top bar, loading progress, pull-to-refresh) with a WebView
rendering the actual dashboard.

The dashboard HTML is bundled inside the app (`app/src/main/assets/dashboard.html`)
— no external hosting needed. It still fetches your live trade data from your
Google Apps Script Web App URL over the network, exactly like it does in a
browser.

## Why WebView + Compose instead of a full React Native rewrite

Everything in the dashboard — the equity curve, the win-rate gauge, the AI
analyst briefing, the execution ledger, the "Log Execution" form that writes
back to your sheet — already exists and works as HTML/CSS/JS. Rebuilding all
of that natively in React Native would mean re-implementing every chart and
component from scratch in a different framework, plus setting up a Node/Metro
toolchain. WebView + Compose reuses everything you already have and gets you
a real native app today. If you outgrow this later, React Native is still an
option — just a much bigger separate project.

## How to open and run this

1. Install [Android Studio](https://developer.android.com/studio) (free) if
   you don't have it.
2. Open Android Studio → **Open** → select this `ForexTradeAnalyst` folder.
3. Let it sync (Android Studio will download Gradle and dependencies
   automatically the first time — this can take a few minutes).
4. Plug in an Android phone with USB debugging enabled, or start an emulator
   from **Device Manager**.
5. Click the green **Run ▶** button.

That's it — the app installs and opens straight to your dashboard.

## Updating the dashboard inside the app

If you make changes to `dashboard.html` later (new features, design tweaks),
just replace `app/src/main/assets/dashboard.html` with the new version and
re-run the app. No other code changes needed.

## Building a shareable APK (without Google Play)

1. In Android Studio: **Build → Build App Bundle(s) / APK(s) → Build APK(s)**
2. Once it finishes, click **locate** in the popup notification
3. That `.apk` file can be sent directly to any Android phone and installed
   (the phone will need "Install from unknown sources" allowed once)

## Notes

- Minimum Android version supported: Android 8.0 (API 26) — covers the vast
  majority of active Android devices.
- The app needs internet access to load live trade data, same as the web
  version — this is already declared in `AndroidManifest.xml`.
- Your Apps Script Web App URL is remembered via the WebView's local storage,
  same as in a regular browser tab.

## Command-line builds and CI

This project includes `gradlew` / `gradlew.bat` (the wrapper scripts) and a
GitHub Actions workflow at `.github/workflows/android-ci.yml` that builds a
debug APK on every push and pull request to `main`, and uploads it as a
downloadable artifact on the workflow run page.

One thing to know: `gradle/wrapper/gradle-wrapper.jar` (a small compiled
binary Gradle normally generates for you) isn't included yet — see
`gradle/wrapper/README_MISSING_JAR.md` for why and how to generate it in one
click from Android Studio if you want `./gradlew` to work from a terminal.
The CI workflow doesn't need it — it installs Gradle directly instead.

To push this to GitHub:
```
cd ForexTradeAnalyst
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin <your-repo-url>
git push -u origin main
```
The Actions tab on GitHub will then show the build running automatically.

