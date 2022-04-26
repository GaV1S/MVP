package ru.s1aks.mvp_login_activity.data.utils

class MockDatabaseConstants {
    enum class DefaultUsers(val value: String) {
        DEFAULT_ADMIN_LOGIN("admin"),
        DEFAULT_ADMIN_PASSWORD("admin"),
        DEFAULT_USER_LOGIN("User_"),
        DEFAULT_USER_PASSWORD("pass")
    }

    enum class ResponseCodes(val code: Int) {
        RESPONSE_SUCCESS(200),
        RESPONSE_INVALID_PASSWORD(403),
        RESPONSE_LOGIN_NOT_REGISTERED(404),
        RESPONSE_LOGIN_REGISTERED_YET(444),
        RESPONSE_USER_UPDATE_FAILED(454),
        RESPONSE_USER_DELETE_FAILED(464)
    }

    enum class StringResources(val value: String) {
        MESSAGE_LOGIN_NOT_REGISTERED("Логин не зарегистрирован"),
        MESSAGE_YOUR_PASSWORD_IS("Пароль: ")
    }
}