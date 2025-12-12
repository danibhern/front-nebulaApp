package com.example.appnebula.data.repository

import com.example.appnebula.data.local.User
import com.example.appnebula.data.local.UserDao
import kotlinx.coroutines.flow.Flow


open class UserRepository(private val dao: UserDao) {
    open val users: Flow<List<User>> = dao.getAllUsers()
    suspend fun insert(user: User) = dao.insertUser(user)
    suspend fun update(user: User) = dao.updateUser(user)
    suspend fun delete(user: User) = dao.deleteUser(user)
}