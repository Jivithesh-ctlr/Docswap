package com.example.docswap.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.docswap.navigation.Screen
import com.example.docswap.ui.theme.LimeGreen
import com.example.docswap.viewmodel.ConversionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordToolsScreen(navController: NavController, viewModel: ConversionViewModel) {
    var isLockMode by remember { mutableStateOf(true) }
    var selectedFileUri by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedFileUri = uri?.toString()
    }

    val isConverting by viewModel.isConverting.collectAsState()
    val conversionResult by viewModel.conversionResult.collectAsState()

    LaunchedEffect(conversionResult) {
        if (conversionResult?.isSuccess == true) {
            navController.navigate(Screen.Result.route)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isLockMode) "Lock PDF" else "Unlock PDF", color = MaterialTheme.colorScheme.onBackground) },
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
            TabRow(
                selectedTabIndex = if (isLockMode) 0 else 1,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = LimeGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[if (isLockMode) 0 else 1]),
                        color = LimeGreen
                    )
                }
            ) {
                Tab(
                    selected = isLockMode,
                    onClick = { isLockMode = true },
                    text = { Text("Lock", color = if (isLockMode) LimeGreen else androidx.compose.ui.graphics.Color.Gray) }
                )
                Tab(
                    selected = !isLockMode,
                    onClick = { isLockMode = false },
                    text = { Text("Unlock", color = if (!isLockMode) LimeGreen else androidx.compose.ui.graphics.Color.Gray) }
                )
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { launcher.launch("application/pdf") },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(80.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (isLockMode) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = LimeGreen
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        selectedFileUri?.substringAfterLast("/") ?: "Select PDF File",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedBorderColor = LimeGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surface,
                    focusedLabelColor = LimeGreen,
                    unfocusedLabelColor = androidx.compose.ui.graphics.Color.Gray
                )
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (isLockMode) {
                        viewModel.lockPdf(selectedFileUri!!, password)
                    } else {
                        viewModel.unlockPdf(selectedFileUri!!, password)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LimeGreen),
                shape = RoundedCornerShape(12.dp),
                enabled = selectedFileUri != null && password.isNotBlank() && !isConverting
            ) {
                if (isConverting) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        if (isLockMode) "Lock PDF" else "Unlock PDF",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
