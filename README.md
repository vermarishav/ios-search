# iOS-Style Search — Android (Jetpack Compose)

A native Android implementation of an iOS-style search experience: search bar
with animated Cancel button, live suggestions, persisted recent searches, and
voice dictation.

## Open this project

1. Open the `iossearch/` folder in Android Studio (Koala or newer).
2. Let Gradle sync — it needs internet access to Google's Maven and Maven
   Central the first time (this build environment's sandbox couldn't reach
   those hosts, so **this hasn't been compiled/run here** — please build it
   locally and let me know if Android Studio flags anything).
3. Run on a device or emulator running API 26+.

## What's implemented

- **Search bar**: rounded capsule field, magnifying-glass icon, clear button,
  animated "Cancel" that slides in on focus — modeled on `UISearchBar` /
  SwiftUI's `.searchable()`.
- **Suggestions-as-you-type**: debounced (180ms) matching against a small
  in-memory corpus (apps/settings/contacts/web), ranked by prefix match.
- **Recent searches**: persisted with DataStore, newest-first, individually
  removable, with a "Clear" all action — survives app restarts like iOS's
  Recents list.
- **Voice search**: uses Android's native `SpeechRecognizer` (on-device or
  Google app-backed, depending on device) behind a full-screen dictation
  overlay with a pulsing mic and live partial transcript. Requests
  `RECORD_AUDIO` at runtime.
- **Empty / no-results states**, dark mode support via system theme.

## Honest scope notes

- This is a **visual and functional homage**, not literal Apple code or
  assets. Apple's SF Pro font and SF Symbols are proprietary and aren't
  redistributed here — text uses the Android system font, and icons use
  Material icons standing in for SF Symbols equivalents.
- "Siri"-style voice search specifically is an Apple product; this uses
  Android's own `SpeechRecognizer` API, which is the native equivalent.
- The suggestion corpus is a small hardcoded dataset for demo purposes —
  wire `SearchCorpus` up to a real index/API for production use.
- I was not able to run a Gradle build in this sandbox (outbound network
  to Google's/Gradle's servers is blocked here), so please treat this as
  reviewed-but-not-compiled and report back any build errors Android Studio
  surfaces — happy to fix immediately.
