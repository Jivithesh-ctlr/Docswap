package com.example.docswap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.docswap.ui.theme.LimeGreen
import com.example.docswap.viewmodel.ConversionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatermarkScreen(
    navController: NavController,
    conversionViewModel: ConversionViewModel
) {
    var text by remember { mutableStateOf("") }
    var opacity by remember { mutableFloatStateOf(0.5f) }
    var rotation by remember { mutableFloatStateOf(45f) }
    val isConverting by conversionViewModel.isConverting.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Watermark", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
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
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Watermark Text") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = LimeGreen,
                    unfocusedBorderColor = androidx.compose.ui.graphics.Color.Gray
                )
            )

            Column {
                Text(text = "Opacity: ${(opacity * 100).toInt()}%", color = MaterialTheme.colorScheme.onBackground)
                Slider(
                    value = opacity,
                    onValueChange = { opacity = it },
                    colors = SliderDefaults.colors(thumbColor = LimeGreen, activeTrackColor = LimeGreen)
                )
            }

            Column {
                Text(text = "Rotation: ${rotation.toInt()}°", color = MaterialTheme.colorScheme.onBackground)
                Slider(
                    value = rotation,
                    onValueChange = { rotation = it },
                    valueRange = 0f..360f,
                    colors = SliderDefaults.colors(thumbColor = LimeGreen, activeTrackColor = LimeGreen)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { 
                    conversionViewModel.watermarkPdf("input_path_placeholder", text, opacity, rotation)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                enabled = !isConverting && text.isNotBlank()
            ) {
                if (isConverting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Apply Watermark", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
