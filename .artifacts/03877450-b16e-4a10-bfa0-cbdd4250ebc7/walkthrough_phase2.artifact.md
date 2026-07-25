# DocSwap - Phase 2 Extension Walkthrough

We have completed the **Phase 2: Medium Features**, adding significant productivity tools and fixing a critical database-related crash.

## Crash Fix (Critical)
- **Database Schema Sync:** Incremented `DocSwapDatabase` version to `2` and enabled `destructiveMigration`. This resolves the crash reported after the Phase 1 update by ensuring the local database matches the new internal code structure.

## New Features Implemented

### 1. Image & OCR Workflows
- **Image to PDF:** Convert multiple JPG/PNG images into a single professional PDF. Includes a reorderable thumbnail preview.
- **PDF to Image:** Export every page of a PDF as a high-quality JPEG image, saved to a dedicated `DocSwap_Images` gallery folder.
- **OCR (Text Recognition):** Extract text from images or scanned PDFs using **ML Kit**. Supports copying to clipboard or exporting as a `.txt` file.

### 2. PDF Security
- **Password Protection:** Lock your PDFs with a secure user password using AES-128 encryption.
- **Unlock PDF:** Remove passwords from protected PDFs for easier access.

### 3. Usability Enhancements
- **Search History:** A new search bar on the **Recent** tab lets you filter conversions by filename in real-time.
- **Dynamic Renaming:** You can now rename your converted file directly on the **Result** screen before finalizing the save.
- **Expanded Tools:** The **Tools** tab now includes a comprehensive grid of all 6 PDF utilities.

---

## Technical Updates

- **ML Kit Integration:** Integrated Google's on-device Text Recognition API.
- **Coil Library:** Added Coil for high-performance image loading and thumbnail rendering.
- **PdfRenderer:** Leveraged Android's native `PdfRenderer` for converting PDF pages to Bitmaps.
- **State Management:** `ConversionViewModel` now tracks OCR results and multi-image selections.

---

## How to Test

1. **OCR:** Navigate to **Tools > OCR**, select a screenshot, and verify the text is extracted correctly. Try the "Copy" button.
2. **Image to PDF:** Go to **Tools > Image to PDF**, select 2-3 images, reorder them by tapping the arrows, and convert.
3. **Locking:** Protect a sensitive PDF with a password and try opening it in an external viewer.
4. **Search:** On the **Recent** tab, type a partial filename to see the list filter instantly.
5. **Rename:** After any conversion, change the filename in the text field on the Result screen and click "Open File" to verify the name changed.

> [!TIP]
> The OCR feature works entirely offline! No data is sent to the cloud, ensuring your documents remain private.
