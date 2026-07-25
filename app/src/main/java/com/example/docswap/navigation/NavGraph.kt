package com.example.docswap.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.docswap.ui.screens.*
import com.example.docswap.viewmodel.AuthViewModel
import com.example.docswap.viewmodel.ConversionViewModel
import com.example.docswap.viewmodel.SettingsViewModel
import com.example.docswap.viewmodel.ViewModelFactory

@Composable
fun NavGraph(navController: NavHostController) {
    val context = LocalContext.current
    val factory = ViewModelFactory(context)
    val authViewModel: AuthViewModel = viewModel(factory = factory)
    val conversionViewModel: ConversionViewModel = viewModel(factory = factory)

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(navController, authViewModel)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController) {
                authViewModel.setFirstLaunchComplete()
            }
        }
        composable(Screen.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(Screen.Signup.route) {
            SignupScreen(navController, authViewModel)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController, authViewModel, conversionViewModel)
        }
        composable(Screen.Conversion.route) { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type")
            ConversionScreen(navController, conversionViewModel, type)
        }
        composable(Screen.Result.route) {
            ResultScreen(navController, conversionViewModel)
        }
        composable(Screen.RecentConversions.route) {
            RecentConversionsScreen(navController, conversionViewModel)
        }
        
        composable(Screen.Tools.route) {
            ToolsScreen(navController)
        }
        composable(Screen.Settings.route) {
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            SettingsScreen(navController, authViewModel, settingsViewModel)
        }
        composable(Screen.MergePdf.route) {
            MergePdfScreen(navController, conversionViewModel)
        }
        composable(Screen.SplitPdf.route) {
            SplitPdfScreen(navController, conversionViewModel)
        }
        composable(Screen.CompressPdf.route) {
            CompressPdfScreen(navController, conversionViewModel)
        }
        composable(Screen.ImageToPdf.route) {
            ImageToPdfScreen(navController, conversionViewModel)
        }
        composable(Screen.PdfToImage.route) {
            PdfToImageScreen(navController, conversionViewModel)
        }
        composable(Screen.Ocr.route) {
            OcrScreen(navController, conversionViewModel)
        }
        composable(Screen.PasswordTools.route) {
            PasswordToolsScreen(navController, conversionViewModel)
        }
        composable(Screen.ESignature.route) {
            ESignatureScreen(navController, conversionViewModel)
        }
        composable(Screen.Watermark.route) {
            WatermarkScreen(navController, conversionViewModel)
        }
        composable(Screen.Annotation.route) {
            AnnotationScreen(navController, conversionViewModel)
        }
        composable(Screen.EditProfile.route) {
            EditProfileScreen(navController, authViewModel)
        }
    }
}

@Composable
fun PlaceholderScreen(name: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = name)
    }
}
