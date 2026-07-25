# DocSwap - Complete Android App Walkthrough

DocSwap is a high-performance, minimalist Android application for converting PDF files to Word documents and vice versa. It features a dark-mode-first "Nothing OS" aesthetic, local Room-based authentication, and a zero-dependency custom conversion engine.

## Core Features Implemented

### 1. Zero-Dependency Conversion Engine
- **Word (.docx) to PDF:** Directly parses OOXML (`word/document.xml`) from the ZIP package and renders text using Android's native `StaticLayout` and `PdfDocument` canvas.
- **PDF to Word (.docx):** Extracts text using the lightweight `iTextG` library and packages it into a valid OpenXML ZIP structure.
- **Location:** [FileConverter.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/utils/FileConverter.kt)

### 2. Secure Local Authentication
- **Room Database:** Stores user credentials securely.
- **Hashing:** Passwords are saved as SHA-256 hashes using [PasswordHasher.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/utils/PasswordHasher.kt).
- **Session Management:** Persistent login using [SessionManager.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/data/local/SessionManager.kt).

### 3. Minimalist Jetpack Compose UI
- **Dark-Mode First:** Deep black backgrounds (#000000) with a vibrant LimeGreen accent (#C5FC41).
- **Navigation:** Smooth transitions between Splash, Auth, Home, and Conversion screens using [NavGraph.kt](file:///Users/Jivithesh/Desktop/Applications/app/src/main/java/com/example/docswap/navigation/NavGraph.kt).
- **Custom Progress:** A branded pulsing animation replaces generic loaders.

### 4. Modern Scoped Storage
- Uses **Storage Access Framework (SAF)** for permissionless file picking.
- Saves files to `Downloads/DocSwap` using `MediaStore`, ensuring compatibility with Android 10-14 without legacy storage permissions.

---

## Technical Architecture

The app follows a clean **MVVM architecture**:
- **UI:** Jetpack Compose screens in `com.example.docswap.ui.screens`.
- **ViewModel:** `AuthViewModel` and `ConversionViewModel` managing state via `StateFlow`.
- **Repository:** Abstracting data sources (Room + File System).
- **Data:** Room entities and DAOs for Users and Recent Conversions.

---

## How to Test

1. **Build & Run:** Deploy to an Android device or emulator (API 26+).
2. **Splash & Signup:** Watch the logo animation and create a new account.
3. **Convert:**
   - Select "Word to PDF" on the Home screen.
   - Pick a `.docx` file from your device.
   - Click "Convert" and watch the animation.
4. **View Results:** Open or share the resulting PDF. Check your device's `Downloads/DocSwap` folder.
5. **History:** Go to "Recent Conversions" to see your conversion log.

> [!TIP]
> The app is optimized for large documents by processing conversions on a background Coroutine Dispatcher, keeping the UI perfectly responsive.
