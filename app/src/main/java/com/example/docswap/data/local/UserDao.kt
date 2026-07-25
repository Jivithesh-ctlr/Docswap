package com.example.docswap.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun signup(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun findByEmail(email: String): UserEntity?

    @Query("UPDATE users SET name = :name, hashedPassword = :password WHERE email = :email")
    suspend fun updateProfile(email: String, name: String, password: String)
}
