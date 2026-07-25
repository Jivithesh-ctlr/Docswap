# DocSwap - Phase 1 Extension Walkthrough

We have completed the **Phase 1: Easy Features** for DocSwap, significantly expanding the app's PDF utility capabilities and modernizing its interface.

## New Features Implemented

### 1. Advanced PDF Tools
- **Merge PDF:** Select multiple PDF files, reorder them as needed, and combine them into a single document.
- **Split PDF:** Extract specific page ranges from a PDF into a new file.
- **Compress PDF:** Reduce PDF file size with a configurable quality slider (Low/Medium/High).
- **Batch Conversion:** The conversion engine now supports sequential batch processing for multiple files with real-time progress.

### 2. Theme & Persistence
- **Light/Dark Mode:** A new theme toggle in Settings allows switching between dark and light modes.
- **DataStore Persistence:** Your theme choice is now persisted using Jetpack DataStore, ensuring it remains active after app restarts.

### 3. Navigation & UI Overhaul
- **Bottom Navigation Bar:** Quick access to **Home**, **Tools**, **Recent**, and **Settings**.
- **Settings Screen:** Manage theme preference, view account info (Name/Email), and securely logout with confirmation.
- **File Size Comparison:** The Result screen now displays the **Original Size vs. Converted Size**, providing clear value for compression tasks.

---

## Technical Updates

- **iTextG Integration:** Leveraged `PdfCopy` for merging/splitting and `PdfStamper` for high-efficiency compression.
- **DataStore:** Replaced standard SharedPreferences for theme settings with the more modern, asynchronous `DataStore`.
- **Unified Scaffold:** Implemented a single `Scaffold` in `MainActivity` that intelligently shows/hides the bottom bar based on the current screen.
- **File Utilities:** Added a robust `FileUtils` layer for URI-to-Path resolution and human-readable size formatting.

---

## How to Test

1. **Check Tools:** Navigate to the new **Tools** tab and try merging two PDFs.
2. **Test Compression:** Select a large PDF in the **Compress PDF** tool, set quality to "High Compression", and observe the size difference on the Result screen.
3. **Toggle Theme:** Go to **Settings**, switch to Light mode, close the app, and reopen it to verify persistence.
4. **View History:** Check the **Recent** tab to see your new operations (Merge/Split/Compress) logged with timestamps.

> [!NOTE]
> All new features utilize **Scoped Storage**, saving outputs directly to `Downloads/DocSwap` without requiring extra system permissions.
