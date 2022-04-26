package ru.s1aks.mvp_login_activity.data.api

import ru.s1aks.mvp_login_activity.data.db.UserDao
import ru.s1aks.mvp_login_activity.data.utils.MockDatabaseConstants
import ru.s1aks.mvp_login_activity.data.utils.fakeDelay
import ru.s1aks.mvp_login_activity.domain.api.IUserDatabaseApi

class MockUserDatabaseApi(private val roomDataSource: UserDao) : IUserDatabaseApi {

    override fun login(login: String, password: String): Int {
        Thread.sleep(fakeDelay())
        val user = roomDataSource.getUser(login)
        return when {
            user == null -> {
                MockDatabaseConstants.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code
            }
            user.userPassword != password -> {
                MockDatabaseConstants.ResponseCodes.RESPONSE_INVALID_PASSWORD.code
            }
            else -> {
                roomDataSource.userLogin(login)
                MockDatabaseConstants.ResponseCodes.RESPONSE_SUCCESS.code
            }
        }
    }

    override fun logout(login: String): Int {
        Thread.sleep(fakeDelay())
        return when (roomDataSource.getUser(login)) {
            null -> {
                MockDatabaseConstants.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code
            }
            else -> {
                roomDataSource.userLogout(login)
                MockDatabaseConstants.ResponseCodes.RESPONSE_SUCCESS.code
            }
        }
    }

    override fun remindUserPassword(login: String): String {
        Thread.sleep(fakeDelay())
        return if (roomDataSource.getUser(login)?.userPassword == null) {
            MockDatabaseConstants.StringResources.MESSAGE_LOGIN_NOT_REGISTERED.value
        } else {
            MockDatabaseConstants.StringResources
                .MESSAGE_YOUR_PASSWORD_IS.value + roomDataSource.getUser(login)?.userPassword
        }
    }
}