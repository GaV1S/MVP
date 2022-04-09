package ru.s1aks.mvp_login_activity.domain

import java.io.Serializable

data class LoginData(
    val login: String,
    val password: String
) : Serializable