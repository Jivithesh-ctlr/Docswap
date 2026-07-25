# Implementation Plan - DocSwap Enhancements

Enhance the DocSwap app with a new logo, additional features (History Export, Auto-delete), and a download website.

## User Review Required

> [!IMPORTANT]
> - The logo will follow a minimalist, dark-mode-first aesthetic with a lime-green accent (`#C5FC41`).
> - New features will be integrated into the existing `ViewModel` and `Compose` screens.
> - The website will be a single-page static site (HTML/CSS/JS).

## Proposed Changes

### Part A: App Logo & Icons

#### [NEW] [logo.svg](file:///Users/Jivithesh/Desktop/Applications/app/src/main/res/drawable/logo.svg)
- A minimalist SVG logo featuring two overlapping document shapes forming a swap symbol.

#### [MODIFY] [ic_launcher_foreground.xml](file:///Users/Jivithesh/Desktop/Applications/app/src/main/res/drawable/ic_launcher_foreground.xml)
- Update the foreground layer for adaptive icons with the new logo.

#### [MODIFY] [ic_launcher_background.xml](file:///Users/Jivithesh/Desktop/Applications/app/src/main/res/drawable/ic_launcher_background.xml)
- Update the background layer (Solid Black).

### Part B: Additional Features

#### [MODIFY] [SettingsRepository.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/data/local/SettingsRepository.kt)
- Add `autoDeleteEnabled` and `autoDeleteDays` to DataStore.

#### [MODIFY] [SettingsViewModel.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/viewmodel/SettingsViewModel.kt)
- Add flows and functions to manage auto-delete settings.

#### [MODIFY] [SettingsScreen.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/ui/screens/SettingsScreen.kt)
- Add UI for auto-delete settings (Toggle + Days selection).

#### [MODIFY] [RecentConversionsScreen.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/ui/screens/RecentConversionsScreen.kt)
- Add an "Export History" button to the TopAppBar.

#### [MODIFY] [OnboardingScreen.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/ui/screens/OnboardingScreen.kt)
- Replace placeholder "Logo" text with the new SVG logo.

#### [MODIFY] [MainActivity.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/MainActivity.kt)
- Trigger auto-cleanup on app launch if enabled.

### Part C: Download Website

#### [NEW] [index.html](file:///Users/Jivithesh/Desktop/Applications/website/index.html)
- Main structure of the one-page website.

#### [NEW] [styles.css](file:///Users/Jivithesh/Desktop/Applications/website/styles.css)
- Styling with dark-mode-first, minimalist, lime-green accent.

#### [NEW] [script.js](file:///Users/Jivithesh/Desktop/Applications/website/script.js)
- Interactivity (smooth scroll, animations).

## Verification Plan

### Automated Tests
- Run `gradle_build` to ensure the project still compiles.

### Manual Verification
- Verify logo rendering in Android Studio Preview.
- Check "Settings" for the new auto-delete options.
- Check "Recent Conversions" for the export button.
- Open `index.html` in a browser to verify the website layout and responsiveness.
