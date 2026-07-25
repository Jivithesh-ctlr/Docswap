# Implementation Plan - DocSwap Branding, UX & Web Extension

This plan covers the creation of a professional brand identity (Logo), critical UX enhancements (Onboarding, History Export, Auto-Cleanup), and a responsive download website for DocSwap.

## Proposed Changes

### 1. Part A: App Logo & Branding (Minimalist / Lime-Green)

#### [NEW] [ic_logo.svg](file:///Users/Jivithesh/Desktop/Applications/app/src/main/res/drawable/ic_logo.xml)
- Minimalist swap icon with two document silhouettes and a central exchange arrow.
- Colors: `#C5FC41` (Lime Green) and `#000000` (Black) for dark mode.

#### [NEW] [ic_launcher_foreground.xml](file:///Users/Jivithesh/Desktop/Applications/app/src/main/res/drawable/ic_launcher_foreground.xml)
- Vector drawable for the adaptive icon foreground.

#### [NEW] [ic_launcher_background.xml](file:///Users/Jivithesh/Desktop/Applications/app/src/main/res/drawable/ic_launcher_background.xml)
- Color drawable (Black) for the adaptive icon background.

---

### 2. Part B: Additional Functional Features

#### [MODIFY] [SessionManager.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/data/local/SessionManager.kt)
- Add `isFirstLaunch` flag to manage onboarding visibility.

#### [NEW] [OnboardingScreen.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/ui/screens/OnboardingScreen.kt)
- 3-screen swipeable tutorial (using `HorizontalPager`) explaining:
  1. PDF/Word Conversion
  2. Professional Tools (Merge, Split, OCR)
  3. Offline & Secure Processing

#### [MODIFY] [NavGraph.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/navigation/NavGraph.kt) & [SplashScreen.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/ui/screens/SplashScreen.kt)
- Integrate Onboarding logic: Splash -> Onboarding (if first time) -> Auth.

#### [MODIFY] [ConversionRepository.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/repository/ConversionRepository.kt) & [ConversionViewModel.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/viewmodel/ConversionViewModel.kt)
- Implement `exportHistoryToCsv()`: Generates a CSV file in `Downloads/DocSwap/Reports`.
- Implement `cleanupOldConversions(days: Int)`: Automates deletion of history records.

#### [MODIFY] [HomeScreen.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/ui/screens/HomeScreen.kt)
- Add a "Works Offline" badge with a lime-green checkmark for user reassurance.

---

### 3. Part C: Download Landing Page

#### [NEW] [website/index.html](file:///Users/Jivithesh/Desktop/Applications/website/index.html)
- Responsive layout with Hero, Features, Screenshots, and How-to-Install sections.

#### [NEW] [website/style.css](file:///Users/Jivithesh/Desktop/Applications/website/style.css)
- Dark-mode-first styling matching the app's aesthetic.

#### [NEW] [website/script.js](file:///Users/Jivithesh/Desktop/Applications/website/script.js)
- Smooth scroll and scroll-triggered animations.

## Verification Plan

### Manual Verification
1. **App Identity:** Confirm the new adaptive icon appears correctly on the home screen.
2. **Onboarding:** Clear app data -> Launch -> Verify 3-page tutorial appears -> Verify it doesn't appear on subsequent launches.
3. **History Export:** Perform 3 conversions -> Tap "Export CSV" in Recent -> Open Downloads and verify the CSV content.
4. **Auto-Delete:** Toggle setting -> Confirm old DB entries are removed.
5. **Website:** Open `index.html` in a mobile emulator and desktop browser to test responsiveness.
