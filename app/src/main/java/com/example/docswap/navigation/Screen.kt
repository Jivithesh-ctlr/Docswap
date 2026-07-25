package com.example.docswap.navigation

sealed class Screen(val route: String, val showBottomBar: Boolean = true) {
    object Splash : Screen("splash", showBottomBar = false)
    object Onboarding : Screen("onboarding", showBottomBar = false)
    object Login : Screen("login", showBottomBar = false)
    object Signup : Screen("signup", showBottomBar = false)
    object Home : Screen("home")
    object Conversion : Screen("conversion/{type}", showBottomBar = false) {
        fun createRoute(type: String) = "conversion/$type"
    }
    object Result : Screen("result", showBottomBar = false)
    object RecentConversions : Screen("recent_conversions")
    
    // New screens
    object Tools : Screen("tools")
    object Settings : Screen("settings")
    object MergePdf : Screen("merge_pdf", showBottomBar = false)
    object SplitPdf : Screen("split_pdf", showBottomBar = false)
    object CompressPdf : Screen("compress_pdf", showBottomBar = false)
    
    // Phase 2 screens
    object ImageToPdf : Screen("image_to_pdf", showBottomBar = false)
    object PdfToImage : Screen("pdf_to_image", showBottomBar = false)
    object Ocr : Screen("ocr", showBottomBar = false)
    object PasswordTools : Screen("password_tools", showBottomBar = false)
    
    // Phase 3 screens
    object ESignature : Screen("e_signature", showBottomBar = false)
    object Watermark : Screen("watermark", showBottomBar = false)
    object Annotation : Screen("annotation", showBottomBar = false)
    object EditProfile : Screen("edit_profile", showBottomBar = false)

    companion object {
        val allScreens = listOf(
            Splash, Login, Signup, Home, Conversion, Result, RecentConversions,
            Tools, Settings, MergePdf, SplitPdf, CompressPdf,
            ImageToPdf, PdfToImage, Ocr, PasswordTools,
            ESignature, Watermark, Annotation, EditProfile
        )
    }
}
