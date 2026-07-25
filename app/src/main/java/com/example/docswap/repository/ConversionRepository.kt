package com.example.docswap.repository

import com.example.docswap.data.local.RecentConversionDao
import com.example.docswap.data.local.RecentConversionEntity
import com.example.docswap.utils.FileConverter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File

data class FileInfo(
    val uri: String,
    val name: String,
    val sourceFormat: String,
    val targetFormat: String,
    val size: Long = 0L
)

sealed class ProgressState {
    data class Loading(val progress: Float, val currentFile: String) : ProgressState()
    data class Success(val outputPaths: List<String>) : ProgressState()
    data class Error(val message: String) : ProgressState()
}

class ConversionRepository(
    private val recentConversionDao: RecentConversionDao
) {
    suspend fun getRecentConversions(): List<RecentConversionEntity> {
        return recentConversionDao.getAll()
    }

    suspend fun convertFile(
        fileName: String,
        sourceFormat: String,
        targetFormat: String,
        inputPath: String
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val originalFile = File(inputPath)
            val originalSize = originalFile.length()

            val outputPath = when {
                sourceFormat == "pdf" && targetFormat == "docx" -> FileConverter.convertPdfToDocx(inputPath)
                sourceFormat == "docx" && targetFormat == "pdf" -> FileConverter.convertDocxToPdf(inputPath)
                (sourceFormat == "jpg" || sourceFormat == "png") && targetFormat == "pdf" -> FileConverter.convertImageToPdf(inputPath)
                else -> return@withContext Result.failure(Exception("Unsupported conversion: $sourceFormat to $targetFormat"))
            }

            val convertedFile = File(outputPath)
            val convertedSize = convertedFile.length()

            val entity = RecentConversionEntity(
                fileName = fileName,
                sourceFormat = sourceFormat,
                targetFormat = targetFormat,
                timestamp = System.currentTimeMillis(),
                filePath = outputPath,
                originalSize = originalSize,
                convertedSize = convertedSize
            )
            recentConversionDao.insert(entity)

            Result.success(outputPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun convertFilesSequential(files: List<FileInfo>): Flow<ProgressState> = flow {
        val outputPaths = mutableListOf<String>()
        files.forEachIndexed { index, fileInfo ->
            emit(ProgressState.Loading((index.toFloat() / files.size), fileInfo.name))
            
            val result = convertFile(
                fileName = fileInfo.name,
                sourceFormat = fileInfo.sourceFormat,
                targetFormat = fileInfo.targetFormat,
                inputPath = fileInfo.uri
            )
            
            if (result.isSuccess) {
                outputPaths.add(result.getOrThrow())
            } else {
                emit(ProgressState.Error("Failed to convert ${fileInfo.name}: ${result.exceptionOrNull()?.message}"))
                return@flow
            }
        }
        emit(ProgressState.Success(outputPaths))
    }

    suspend fun mergePdfs(inputPaths: List<String>, outputFileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val totalOriginalSize = inputPaths.sumOf { File(it).length() }
            val outputPath = FileConverter.mergePdfs(inputPaths, outputFileName)
            val convertedSize = File(outputPath).length()

            val entity = RecentConversionEntity(
                fileName = outputFileName,
                sourceFormat = "pdf",
                targetFormat = "pdf",
                timestamp = System.currentTimeMillis(),
                filePath = outputPath,
                originalSize = totalOriginalSize,
                convertedSize = convertedSize
            )
            recentConversionDao.insert(entity)

            Result.success(outputPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun splitPdf(inputPath: String, pageRanges: List<IntRange>): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val originalFile = File(inputPath)
            val originalSize = originalFile.length()
            val outputPaths = FileConverter.splitPdf(inputPath, pageRanges)
            
            outputPaths.forEach { outputPath ->
                val convertedSize = File(outputPath).length()
                val entity = RecentConversionEntity(
                    fileName = File(outputPath).name,
                    sourceFormat = "pdf",
                    targetFormat = "pdf",
                    timestamp = System.currentTimeMillis(),
                    filePath = outputPath,
                    originalSize = originalSize,
                    convertedSize = convertedSize
                )
                recentConversionDao.insert(entity)
            }
            
            Result.success(outputPaths)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun compressPdf(inputPath: String, quality: Float): Result<String> = withContext(Dispatchers.IO) {
        try {
            val originalFile = File(inputPath)
            val originalSize = originalFile.length()
            val outputPath = FileConverter.compressPdf(inputPath, quality)
            val convertedSize = File(outputPath).length()

            val entity = RecentConversionEntity(
                fileName = File(outputPath).name,
                sourceFormat = "pdf",
                targetFormat = "pdf",
                timestamp = System.currentTimeMillis(),
                filePath = outputPath,
                originalSize = originalSize,
                convertedSize = convertedSize
            )
            recentConversionDao.insert(entity)

            Result.success(outputPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun convertImagesToPdf(imagePaths: List<String>, outputFileName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val totalOriginalSize = imagePaths.sumOf { File(it).length() }
            val outputPath = FileConverter.convertImagesToPdf(imagePaths, outputFileName)
            val convertedSize = File(outputPath).length()

            val entity = RecentConversionEntity(
                fileName = outputFileName,
                sourceFormat = "images",
                targetFormat = "pdf",
                timestamp = System.currentTimeMillis(),
                filePath = outputPath,
                originalSize = totalOriginalSize,
                convertedSize = convertedSize
            )
            recentConversionDao.insert(entity)
            Result.success(outputPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun convertPdfToImages(inputPath: String): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val originalFile = File(inputPath)
            val originalSize = originalFile.length()
            val outputPaths = FileConverter.convertPdfToImages(inputPath)
            
            outputPaths.forEach { outputPath ->
                val entity = RecentConversionEntity(
                    fileName = File(outputPath).name,
                    sourceFormat = "pdf",
                    targetFormat = "jpg",
                    timestamp = System.currentTimeMillis(),
                    filePath = outputPath,
                    originalSize = originalSize,
                    convertedSize = File(outputPath).length()
                )
                recentConversionDao.insert(entity)
            }
            Result.success(outputPaths)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun performOcr(inputPath: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val text = FileConverter.performOcr(inputPath)
            Result.success(text)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun lockPdf(inputPath: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val originalFile = File(inputPath)
            val outputPath = FileConverter.protectPdf(inputPath, password)
            val entity = RecentConversionEntity(
                fileName = File(outputPath).name,
                sourceFormat = "pdf",
                targetFormat = "pdf",
                timestamp = System.currentTimeMillis(),
                filePath = outputPath,
                originalSize = originalFile.length(),
                convertedSize = File(outputPath).length()
            )
            recentConversionDao.insert(entity)
            Result.success(outputPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun unlockPdf(inputPath: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val originalFile = File(inputPath)
            val outputPath = FileConverter.unlockPdf(inputPath, password)
            val entity = RecentConversionEntity(
                fileName = File(outputPath).name,
                sourceFormat = "pdf",
                targetFormat = "pdf",
                timestamp = System.currentTimeMillis(),
                filePath = outputPath,
                originalSize = originalFile.length(),
                convertedSize = File(outputPath).length()
            )
            recentConversionDao.insert(entity)
            Result.success(outputPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun renameFile(oldPath: String, newName: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val oldFile = File(oldPath)
            val extension = oldFile.extension
            val newFile = File(oldFile.parent, if (newName.endsWith(extension)) newName else "$newName.$extension")
            if (oldFile.renameTo(newFile)) {
                // Update in DB
                recentConversionDao.updateFilePath(oldPath, newFile.absolutePath, newFile.name)
                Result.success(newFile.absolutePath)
            } else {
                Result.failure(Exception("Failed to rename file"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun exportHistoryToCsv(): Result<String> = withContext(Dispatchers.IO) {
        try {
            val conversions = recentConversionDao.getAll()
            val reportsDir = File(FileConverter.getOutputDirectory(), "Reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()
            
            val csvFile = File(reportsDir, "DocSwap_History_${System.currentTimeMillis()}.csv")
            csvFile.bufferedWriter().use { writer ->
                writer.write("FileName,Source,Target,Timestamp,OriginalSize,ConvertedSize\n")
                conversions.forEach { c ->
                    writer.write("${c.fileName},${c.sourceFormat},${c.targetFormat},${c.timestamp},${c.originalSize},${c.convertedSize}\n")
                }
            }
            Result.success(csvFile.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cleanupOldConversions(days: Int): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val threshold = System.currentTimeMillis() - (days * 24 * 60 * 60 * 1000L)
            val deletedCount = recentConversionDao.deleteOlderThan(threshold)
            Result.success(deletedCount)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
