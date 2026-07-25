package com.example.docswap.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_conversions")
data class RecentConversionEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fileName: String,
    val sourceFormat: String,
    val targetFormat: String,
    val timestamp: Long,
    val filePath: String,
    val originalSize: Long = 0L,
    val convertedSize: Long = 0L
)
