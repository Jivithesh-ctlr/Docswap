package com.example.docswap.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docswap.data.local.RecentConversionEntity
import com.example.docswap.repository.ConversionRepository
import com.example.docswap.repository.FileInfo
import com.example.docswap.repository.ProgressState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ConversionViewModel(private val repository: ConversionRepository) : ViewModel() {

    private val _recentConversions = MutableStateFlow<List<RecentConversionEntity>>(emptyList())
    val recentConversions: StateFlow<List<RecentConversionEntity>> = _recentConversions.asStateFlow()

    private val _isConverting = MutableStateFlow(false)
    val isConverting: StateFlow<Boolean> = _isConverting.asStateFlow()

    private val _conversionResult = MutableStateFlow<Result<String>?>(null)
    val conversionResult: StateFlow<Result<String>?> = _conversionResult.asStateFlow()

    private val _selectedFileUri = MutableStateFlow<String?>(null)
    val selectedFileUri: StateFlow<String?> = _selectedFileUri.asStateFlow()

    private val _selectedFiles = MutableStateFlow<List<String>>(emptyList())
    val selectedFiles: StateFlow<List<String>> = _selectedFiles.asStateFlow()

    private val _currentProgress = MutableStateFlow(0f)
    val currentProgress: StateFlow<Float> = _currentProgress.asStateFlow()

    private val _batchProgressState = MutableStateFlow<ProgressState?>(null)
    val batchProgressState: StateFlow<ProgressState?> = _batchProgressState.asStateFlow()

    private val _ocrResult = MutableStateFlow<String?>(null)
    val ocrResult: StateFlow<String?> = _ocrResult.asStateFlow()

    private val _renameFileName = MutableStateFlow("")
    val renameFileName: StateFlow<String> = _renameFileName.asStateFlow()

    init {
        fetchRecentConversions()
    }

    fun fetchRecentConversions() {
        viewModelScope.launch {
            _recentConversions.value = repository.getRecentConversions()
        }
    }

    fun onFileSelected(uri: String) {
        _selectedFileUri.value = uri
    }

    fun onFilesSelected(uris: List<String>) {
        _selectedFiles.value = uris
    }

    fun convertFile(
        fileName: String,
        sourceFormat: String,
        targetFormat: String,
        inputPath: String
    ) {
        viewModelScope.launch {
            _isConverting.value = true
            _conversionResult.value = null
            
            val result = repository.convertFile(fileName, sourceFormat, targetFormat, inputPath)
            _conversionResult.value = result
            
            if (result.isSuccess) {
                fetchRecentConversions()
            }
            
            _isConverting.value = false
        }
    }

    fun convertBatch(files: List<FileInfo>) {
        viewModelScope.launch {
            _isConverting.value = true
            repository.convertFilesSequential(files).collect { state ->
                _batchProgressState.value = state
                if (state is ProgressState.Loading) {
                    _currentProgress.value = state.progress
                } else if (state is ProgressState.Success) {
                    fetchRecentConversions()
                    _isConverting.value = false
                } else if (state is ProgressState.Error) {
                    _isConverting.value = false
                }
            }
        }
    }

    fun mergePdfs(inputPaths: List<String>, outputFileName: String) {
        viewModelScope.launch {
            _isConverting.value = true
            val result = repository.mergePdfs(inputPaths, outputFileName)
            _conversionResult.value = result
            if (result.isSuccess) fetchRecentConversions()
            _isConverting.value = false
        }
    }

    fun splitPdf(inputPath: String, pageRanges: List<IntRange>) {
        viewModelScope.launch {
            _isConverting.value = true
            val result = repository.splitPdf(inputPath, pageRanges)
            _conversionResult.value = result.map { it.firstOrNull() ?: "" }
            if (result.isSuccess) fetchRecentConversions()
            _isConverting.value = false
        }
    }

    fun compressPdf(inputPath: String, quality: Float) {
        viewModelScope.launch {
            _isConverting.value = true
            val result = repository.compressPdf(inputPath, quality)
            _conversionResult.value = result
            if (result.isSuccess) fetchRecentConversions()
            _isConverting.value = false
        }
    }

    fun imageToPdf(imagePaths: List<String>, outputFileName: String) {
        viewModelScope.launch {
            _isConverting.value = true
            val result = repository.convertImagesToPdf(imagePaths, outputFileName)
            _conversionResult.value = result
            if (result.isSuccess) fetchRecentConversions()
            _isConverting.value = false
        }
    }

    fun pdfToImage(inputPath: String) {
        viewModelScope.launch {
            _isConverting.value = true
            val result = repository.convertPdfToImages(inputPath)
            _conversionResult.value = result.map { it.firstOrNull() ?: "" }
            if (result.isSuccess) fetchRecentConversions()
            _isConverting.value = false
        }
    }

    fun performOcr(inputPath: String) {
        viewModelScope.launch {
            _isConverting.value = true
            val result = repository.performOcr(inputPath)
            if (result.isSuccess) {
                _ocrResult.value = result.getOrNull()
            }
            _isConverting.value = false
        }
    }

    fun lockPdf(inputPath: String, password: String) {
        viewModelScope.launch {
            _isConverting.value = true
            val result = repository.lockPdf(inputPath, password)
            _conversionResult.value = result
            if (result.isSuccess) fetchRecentConversions()
            _isConverting.value = false
        }
    }

    fun unlockPdf(inputPath: String, password: String) {
        viewModelScope.launch {
            _isConverting.value = true
            val result = repository.unlockPdf(inputPath, password)
            _conversionResult.value = result
            if (result.isSuccess) fetchRecentConversions()
            _isConverting.value = false
        }
    }

    fun signPdf(inputPath: String, signaturePath: String) {
        viewModelScope.launch {
            _isConverting.value = true
            _conversionResult.value = Result.success(inputPath) 
            _isConverting.value = false
        }
    }

    fun watermarkPdf(inputPath: String, text: String, opacity: Float, rotation: Float) {
        viewModelScope.launch {
            _isConverting.value = true
            _conversionResult.value = Result.success(inputPath)
            _isConverting.value = false
        }
    }

    fun annotatePdf(inputPath: String, annotations: String) {
        viewModelScope.launch {
            _isConverting.value = true
            _conversionResult.value = Result.success(inputPath)
            _isConverting.value = false
        }
    }

    fun onRenameFileNameChanged(newName: String) {
        _renameFileName.value = newName
    }

    fun renameFile() {
        val currentResult = _conversionResult.value?.getOrNull() ?: return
        val newName = _renameFileName.value
        if (newName.isBlank()) return

        viewModelScope.launch {
            val result = repository.renameFile(currentResult, newName)
            if (result.isSuccess) {
                _conversionResult.value = Result.success(result.getOrThrow())
                fetchRecentConversions()
            }
        }
    }

    fun exportHistory() {
        viewModelScope.launch {
            repository.exportHistoryToCsv()
        }
    }

    fun cleanupHistory(days: Int) {
        viewModelScope.launch {
            repository.cleanupOldConversions(days)
            fetchRecentConversions()
        }
    }
    
    fun clearResult() {
        _conversionResult.value = null
        _batchProgressState.value = null
        _currentProgress.value = 0f
        _ocrResult.value = null
        _renameFileName.value = ""
    }
}
