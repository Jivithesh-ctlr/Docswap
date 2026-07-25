package com.example.docswap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RecentConversionDao {
    @Query("SELECT * FROM recent_conversions ORDER BY timestamp DESC")
    suspend fun getAll(): List<RecentConversionEntity>

    @Insert
    suspend fun insert(conversion: RecentConversionEntity)

    @Query("UPDATE recent_conversions SET filePath = :newPath, fileName = :newName WHERE filePath = :oldPath")
    suspend fun updateFilePath(oldPath: String, newPath: String, newName: String)

    @Query("DELETE FROM recent_conversions WHERE timestamp < :threshold")
    suspend fun deleteOlderThan(threshold: Long): Int
}
