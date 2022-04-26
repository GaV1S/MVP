package ru.s1aks.mvp_login_activity.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class UserEntity(
    @PrimaryKey
    val userId: String = UUID.randomUUID().toString(),
    val userLogin: String,
    val userPassword: String,
    val isAuthorized: Boolean = false
)