package com.example.docswap.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.docswap.navigation.Screen
import com.example.docswap.ui.theme.LimeGreen
import com.example.docswap.viewmodel.ConversionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfToImageScreen(navController: NavController, viewModel: ConversionViewModel) {
    var selectedPdfUri by remember { mutableStateOf<String?>(null) }
    var quality by remember { mutableStateOf(90f) }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedPdfUri = uri?.toString()
    }

    val conversionResult by viewModel.conversionResult.collectAsState()
    val isConverting by viewModel.isConverting.collectAsState()

    LaunchedEffect(conversionResult) {
        if (conversionResult?.isSuccess == true) {
            navController.navigate(Screen.Result.route)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PDF to Images", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            Button(
                onClick = { launcher.launch("application/pdf") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(16.dp))
                    Text(
                        selectedPdfUri?.substringAfterLast("/") ?: "Select PDF File",
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                }
            }

            if (selectedPdfUri != null) {
                Spacer(Modifier.height(32.dp))
                Text("Quality: ${quality.toInt()}%", color = MaterialTheme.colorScheme.onBackground, fontSize = 16.sp)
                Slider(
                    value = quality,
                    onValueChange = { quality = it },
                    valueRange = 10f..100f,
                    colors = SliderDefaults.colors(
                        thumbColor = LimeGreen,
                        activeTrackColor = LimeGreen,
                        inactiveTrackColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.pdfToImage(selectedPdfUri!!)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isConverting
                ) {
                    if (isConverting) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Extract Images", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}
