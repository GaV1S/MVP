package ru.s1aks.mvp_login_activity.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val userLogin: String,
    val userPassword: String,
    val isAuthorized: Boolean = false
)