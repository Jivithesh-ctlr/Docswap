package com.example.docswap.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.docswap.navigation.Screen
import com.example.docswap.ui.theme.LimeGreen
import com.example.docswap.utils.FileUtils
import com.example.docswap.viewmodel.ConversionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplitPdfScreen(
    navController: NavController,
    viewModel: ConversionViewModel
) {
    val context = LocalContext.current
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var pageRange by remember { mutableStateOf("") }
    val isConverting by viewModel.isConverting.collectAsState()
    val conversionResult by viewModel.conversionResult.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedUri = uri
    }

    LaunchedEffect(conversionResult) {
        if (conversionResult?.isSuccess == true) {
            navController.navigate(Screen.Result.route)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split PDF", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = MaterialTheme.colorScheme.onBackground)
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
            if (selectedUri == null) {
                Button(
                    onClick = { launcher.launch("application/pdf") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = LimeGreen, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Select PDF File", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            } else {
                FilePreviewCard(
                    fileName = FileUtils.getFileName(context, selectedUri!!) ?: "Selected File",
                    fileSize = "",
                    onClear = { selectedUri = null }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text("Enter Page Range", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = pageRange,
                onValueChange = { pageRange = it },
                placeholder = { Text("e.g. 1-3, 5, 8-10", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = LimeGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val path = FileUtils.getPath(context, selectedUri!!) ?: return@Button
                    val ranges = parsePageRanges(pageRange)
                    viewModel.splitPdf(path, ranges)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedUri != null && pageRange.isNotBlank() && !isConverting
            ) {
                if (isConverting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text("Split PDF", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}

private fun parsePageRanges(input: String): List<IntRange> {
    return input.split(",").mapNotNull { part ->
        val range = part.trim().split("-")
        try {
            if (range.size == 1) {
                val page = range[0].toInt()
                IntRange(page, page)
            } else if (range.size == 2) {
                IntRange(range[0].toInt(), range[1].toInt())
            } else null
        } catch (_: Exception) {
            null
        }
    }
}
