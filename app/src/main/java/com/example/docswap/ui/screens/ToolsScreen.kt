package com.example.docswap.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.MergeType
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.docswap.navigation.Screen
import com.example.docswap.ui.components.ConversionCard

@Composable
fun ToolsScreen(navController: NavController) {
    val tools = listOf(
        ToolItem("Merge PDF", Screen.MergePdf.route, Icons.AutoMirrored.Filled.MergeType),
        ToolItem("Split PDF", Screen.SplitPdf.route, Icons.AutoMirrored.Filled.CallSplit),
        ToolItem("Compress PDF", Screen.CompressPdf.route, Icons.Default.Compress),
        ToolItem("Image to PDF", Screen.ImageToPdf.route, Icons.Default.Image),
        ToolItem("PDF to Image", Screen.PdfToImage.route, Icons.Default.Collections),
        ToolItem("OCR", Screen.Ocr.route, Icons.Default.TextFields),
        ToolItem("Lock/Unlock", Screen.PasswordTools.route, Icons.Default.Lock),
        ToolItem("E-Signature", Screen.ESignature.route, Icons.Default.Gesture),
        ToolItem("Watermark", Screen.Watermark.route, Icons.Default.BrandingWatermark),
        ToolItem("Annotation", Screen.Annotation.route, Icons.AutoMirrored.Filled.Notes)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text(
            text = "PDF Tools",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(tools) { tool ->
                ConversionCard(
                    title = tool.name,
                    icon = tool.icon,
                    onClick = { navController.navigate(tool.route) }
                )
            }
        }
    }
}

data class ToolItem(val name: String, val route: String, val icon: ImageVector)
