package com.example.docswap.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [UserEntity::class, RecentConversionEntity::class], version = 2)
abstract class DocSwapDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun recentConversionDao(): RecentConversionDao

    companion object {
        @Volatile
        private var INSTANCE: DocSwapDatabase? = null

        fun getDatabase(context: Context): DocSwapDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DocSwapDatabase::class.java,
                    "docswap_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
