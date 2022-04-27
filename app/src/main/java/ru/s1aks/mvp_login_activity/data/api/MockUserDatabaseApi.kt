package ru.s1aks.mvp_login_activity.data.api

import ru.s1aks.mvp_login_activity.data.db.UserDao
import ru.s1aks.mvp_login_activity.data.utils.fakeDelay
import ru.s1aks.mvp_login_activity.domain.api.IUserDatabaseApi

class MockUserDatabaseApi(private val roomDataSource: UserDao) : IUserDatabaseApi {

    override fun login(login: String, password: String): Int {
        Thread.sleep(fakeDelay())
        val user = roomDataSource.getUser(login)
        return when {
            user == null -> {
                ApiResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code
            }
            user.userPassword != password -> {
                ApiResponseCodes.RESPONSE_INVALID_PASSWORD.code
            }
            else -> {
                roomDataSource.userLogin(login)
                ApiResponseCodes.RESPONSE_SUCCESS.code
            }
        }
    }

    override fun logout(login: String): Int {
        Thread.sleep(fakeDelay())
        return when (roomDataSource.getUser(login)) {
            null -> {
                ApiResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code
            }
            else -> {
                roomDataSource.userLogout(login)
                ApiResponseCodes.RESPONSE_SUCCESS.code
            }
        }
    }

    override fun remindUserPassword(login: String): String {
        Thread.sleep(fakeDelay())
        return if (roomDataSource.getUser(login)?.userPassword == null) {
            ApiStringResources.MESSAGE_LOGIN_NOT_REGISTERED.value
        } else {
            ApiStringResources
                .MESSAGE_YOUR_PASSWORD_IS.value + roomDataSource.getUser(login)?.userPassword
        }
    }
}

private enum class ApiStringResources(val value: String) {
    MESSAGE_LOGIN_NOT_REGISTERED("Логин не зарегистрирован"),
    MESSAGE_YOUR_PASSWORD_IS("Пароль: ")
}

private enum class ApiResponseCodes(val code: Int) {
    RESPONSE_SUCCESS(200),
    RESPONSE_INVALID_PASSWORD(403),
    RESPONSE_LOGIN_NOT_REGISTERED(404)
}