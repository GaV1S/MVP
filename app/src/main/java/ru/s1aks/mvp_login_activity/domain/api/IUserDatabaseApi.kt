package ru.s1aks.mvp_login_activity.domain.api

interface IUserDatabaseApi {
    fun login(login: String, password: String) : Int
    fun logout(login: String) : Int
    fun remindUserPassword(login: String) : String
}