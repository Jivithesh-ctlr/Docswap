package com.example.docswap

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.docswap.navigation.NavGraph
import com.example.docswap.navigation.Screen
import com.example.docswap.ui.components.BottomNavigationBar
import com.example.docswap.ui.theme.DocSwapTheme
import com.example.docswap.viewmodel.SettingsViewModel
import com.example.docswap.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val factory = ViewModelFactory(this)
            val settingsViewModel: SettingsViewModel = viewModel(factory = factory)
            val conversionViewModel: com.example.docswap.viewmodel.ConversionViewModel = viewModel(factory = factory)
            val isDarkMode by settingsViewModel.isDarkMode.collectAsState()
            
            // Auto-cleanup on launch
            val autoDeleteEnabled by settingsViewModel.autoDeleteEnabled.collectAsState()
            val autoDeleteDays by settingsViewModel.autoDeleteDays.collectAsState()
            
            LaunchedEffect(autoDeleteEnabled) {
                if (autoDeleteEnabled) {
                    conversionViewModel.cleanupHistory(autoDeleteDays)
                }
            }
            
            DocSwapTheme(darkTheme = isDarkMode ?: isSystemInDarkTheme()) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                val showBottomBar = Screen.allScreens.find { 
                    // Match route pattern (handling parameters like {type})
                    it.route == currentRoute || currentRoute?.startsWith(it.route.substringBefore("/{")) == true 
                }?.showBottomBar ?: true

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavGraph(navController = navController)
                    }
                }

                // Handle incoming shared files
                LaunchedEffect(intent) {
                    if (intent?.action == Intent.ACTION_SEND) {
                        val uri = intent.getParcelableExtra<android.net.Uri>(Intent.EXTRA_STREAM)
                        if (uri != null) {
                            // Navigate to conversion screen with the shared file
                            // We might need to copy the file to local storage first or just pass URI
                            navController.navigate(Screen.Conversion.createRoute("pdf")) 
                        }
                    }
                }
            }
        }
    }
}
