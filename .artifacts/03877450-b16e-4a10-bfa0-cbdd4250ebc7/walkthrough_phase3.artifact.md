# DocSwap - Phase 3 (Advanced Features) Walkthrough

We have completed the final phase of DocSwap, transforming it into a professional PDF power-tool with cloud integration and deep OS-level features.

## New Features Implemented

### 1. Professional PDF Editing
- **eSignature:** A high-precision signature canvas allows users to draw their signature. The signature is then "stamped" onto any PDF page with customizable placement.
- **Watermarking:** Protect your documents by adding custom text watermarks. Includes real-time sliders for opacity and rotation.
- **PDF Annotation:** Add sticky notes and highlights to your documents to emphasize key information.

### 2. Cloud & System Integration
- **Google Drive Backup:** Securely upload your converted files to Google Drive directly from the Result screen.
- **Share-to-DocSwap:** You can now select a PDF or Word file in external apps (like WhatsApp or Gmail) and share it directly to DocSwap to start a conversion instantly.
- **Home Screen Widget:** A dedicated widget for your Android home screen provides quick shortcuts to start merging or splitting PDFs.

### 3. Profile Management
- **Edit Profile:** Update your name and change your login password directly within the **Settings** screen. Changes are securely persisted to the local Room database.

---

## Technical Updates

- **Google Drive REST API:** Integrated the Drive v3 library and Google Sign-In for secure cloud storage.
- **Signature Canvas:** Implemented a custom Compose `Canvas` with `detectDragGestures` to capture smooth vector-like signature paths.
- **Intent Filters:** Updated `AndroidManifest.xml` to handle `ACTION_SEND` intents for document MIME types.
- **App Widgets:** Created an `AppWidgetProvider` with `RemoteViews` for OS-level quick actions.

---

## Final Verification Checklist

1. **Test Share Intent:** Open a file in a PDF viewer and share it. DocSwap should appear in the app list.
2. **Test eSignature:** Draw a signature, navigate to a PDF, and verify it's correctly applied to the document.
3. **Verify Google Drive:** Click "Backup" on the result screen, complete the sign-in, and check your Google Drive for the file.
4. **Update Profile:** Change your name in Settings and verify the "Hi, [Name]" message on the Home screen updates immediately.

> [!IMPORTANT]
> To enable Google Drive uploads, ensure you have configured your **OAuth 2.0 Client ID** in the Google Cloud Console for the `com.example.docswap` package.
