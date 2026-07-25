package com.example.docswap.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.docswap.navigation.Screen
import com.example.docswap.ui.theme.LimeGreen
import com.example.docswap.viewmodel.ConversionViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversionScreen(
    navController: NavController,
    conversionViewModel: ConversionViewModel,
    conversionType: String?
) {
    val context = LocalContext.current
    val isConverting by conversionViewModel.isConverting.collectAsState()
    val conversionResult by conversionViewModel.conversionResult.collectAsState()
    
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var fileName by remember { mutableStateOf("") }
    var fileSize by remember { mutableStateOf("") }

    val sourceFormat = if (conversionType == "pdf_to_docx") "pdf" else "docx"
    val targetFormat = if (conversionType == "pdf_to_docx") "docx" else "pdf"
    val mimeType = if (sourceFormat == "pdf") "application/pdf" else "application/vnd.openxmlformats-officedocument.wordprocessingml.document"

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            uri?.let {
                selectedUri = it
                context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                    val sizeIndex = cursor.getColumnIndex(android.provider.OpenableColumns.SIZE)
                    cursor.moveToFirst()
                    fileName = cursor.getString(nameIndex)
                    val size = cursor.getLong(sizeIndex)
                    fileSize = "${size / 1024} KB"
                }
            }
        }
    )

    LaunchedEffect(conversionResult) {
        if (conversionResult?.isSuccess == true) {
            navController.navigate(Screen.Result.route)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Convert to ${targetFormat.uppercase()}", color = MaterialTheme.colorScheme.onBackground) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onBackground)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isConverting) {
                    ConversionProgressAnimation()
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Converting your file...",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Text(
                        text = "Select ${sourceFormat.uppercase()} File",
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "We'll convert it to ${targetFormat.uppercase()} instantly",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    if (selectedUri == null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { launcher.launch(arrayOf(mimeType)) },
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    tint = LimeGreen,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Tap to choose file", color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    } else {
                        FilePreviewCard(
                            fileName = fileName,
                            fileSize = fileSize,
                            onClear = { selectedUri = null }
                        )
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = {
                            selectedUri?.let { uri ->
                                val tempFile = File(context.cacheDir, fileName)
                                context.contentResolver.openInputStream(uri)?.use { input ->
                                    FileOutputStream(tempFile).use { output ->
                                        input.copyTo(output)
                                    }
                                }
                                conversionViewModel.convertFile(
                                    fileName = fileName,
                                    sourceFormat = sourceFormat,
                                    targetFormat = targetFormat,
                                    inputPath = tempFile.absolutePath
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                        enabled = selectedUri != null
                    ) {
                        Text(text = "Convert", color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun FilePreviewCard(fileName: String, fileSize: String, onClear: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                tint = LimeGreen,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = fileName, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, maxLines = 1)
                Text(text = fileSize, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f), fontSize = 12.sp)
            }
            TextButton(onClick = onClear) {
                Text(text = "Change", color = LimeGreen)
            }
        }
    }
}

@Composable
fun ConversionProgressAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .scale(scale)
                .background(LimeGreen.copy(alpha = 0.2f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(LimeGreen, CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.background, CircleShape)
        )
    }
}
