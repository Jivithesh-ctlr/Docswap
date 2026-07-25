package com.example.docswap.repository

import com.example.docswap.data.local.SessionManager
import com.example.docswap.data.local.UserDao
import com.example.docswap.data.local.UserEntity
import com.example.docswap.utils.PasswordHasher

class AuthRepository(
    private val userDao: UserDao,
    private val sessionManager: SessionManager
) {
    suspend fun login(email: String, password: String): Result<UserEntity> {
        val user = userDao.findByEmail(email) ?: return Result.failure(Exception("User not found"))
        val hashedPassword = PasswordHasher.hashPassword(password)
        
        return if (user.hashedPassword == hashedPassword) {
            sessionManager.saveSession(user.id, user.name, user.email)
            Result.success(user)
        } else {
            Result.failure(Exception("Invalid password"))
        }
    }

    suspend fun signup(name: String, email: String, password: String): Result<UserEntity> {
        if (userDao.findByEmail(email) != null) {
            return Result.failure(Exception("Email already exists"))
        }
        
        val hashedPassword = PasswordHasher.hashPassword(password)
        val user = UserEntity(name = name, email = email, hashedPassword = hashedPassword)
        userDao.signup(user)
        
        val savedUser = userDao.findByEmail(email)!!
        sessionManager.saveSession(savedUser.id, savedUser.name, savedUser.email)
        return Result.success(savedUser)
    }

    suspend fun updateProfile(name: String, email: String, password: String): Result<Unit> {
        return try {
            val hashedPassword = PasswordHasher.hashPassword(password)
            userDao.updateProfile(email, name, hashedPassword)
            val userId = sessionManager.getUserId()
            sessionManager.saveSession(userId, name, email)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    fun isLoggedIn(): Boolean {
        return sessionManager.isLoggedIn()
    }

    fun isFirstLaunch(): Boolean {
        return sessionManager.isFirstLaunch()
    }

    fun setFirstLaunchComplete() {
        sessionManager.setFirstLaunchComplete()
    }

    fun getUserName(): String {
        return sessionManager.getUserName() ?: "User"
    }

    fun getUserEmail(): String {
        return sessionManager.getUserEmail() ?: ""
    }
}
