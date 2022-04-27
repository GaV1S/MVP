package ru.s1aks.mvp_login_activity.ui.utils

import ru.s1aks.mvp_login_activity.R

class MessageMapper {
    fun getStringResource(code: Int): Int {
        return when (code) {
            ResponseCodes.RESPONSE_SUCCESS.code -> {
                ResponseCodes.RESPONSE_SUCCESS.message
            }

            ResponseCodes.RESPONSE_INVALID_PASSWORD.code -> {
                ResponseCodes.RESPONSE_INVALID_PASSWORD.message
            }

            ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.message
            }

            ResponseCodes.RESPONSE_LOGIN_REGISTERED_YET.code -> {
                ResponseCodes.RESPONSE_LOGIN_REGISTERED_YET.message
            }

            ResponseCodes.RESPONSE_USER_UPDATE_FAILED.code -> {
                ResponseCodes.RESPONSE_USER_UPDATE_FAILED.message
            }

            ResponseCodes.RESPONSE_USER_DELETE_FAILED.code -> {
                ResponseCodes.RESPONSE_USER_DELETE_FAILED.message
            }

            ResponseCodes.RESPONSE_LOGIN_CANNOT_BE_BLANK.code -> {
                ResponseCodes.RESPONSE_LOGIN_CANNOT_BE_BLANK.message
            }

            ResponseCodes.RESPONSE_PASSWORD_CANNOT_BE_BLANK.code -> {
                ResponseCodes.RESPONSE_LOGIN_CANNOT_BE_BLANK.message
            }

            ResponseCodes.ACCOUNT_DELETED.code -> {
                ResponseCodes.ACCOUNT_DELETED.message
            }

            ResponseCodes.ACCOUNT_UPDATED.code -> {
                ResponseCodes.ACCOUNT_UPDATED.message
            }

            ResponseCodes.FORBIDDEN_TO_DELETE.code -> {
                ResponseCodes.FORBIDDEN_TO_DELETE.message
            }

            ResponseCodes.FORBIDDEN_TO_UPDATE.code -> {
                ResponseCodes.FORBIDDEN_TO_UPDATE.message
            }

            ResponseCodes.LOAD_USER_DATA.code -> {
                ResponseCodes.LOAD_USER_DATA.message
            }

            ResponseCodes.REMINDED_PASSWORD.code -> {
                ResponseCodes.REMINDED_PASSWORD.message
            }

            else -> {
                R.string.empty_text
            }
        }
    }

    enum class ResponseCodes(val code: Int, val message: Int = R.string.empty_text) {
        RESPONSE_SUCCESS(200),
        RESPONSE_INVALID_PASSWORD(403, R.string.invalid_password),
        RESPONSE_LOGIN_NOT_REGISTERED(404, R.string.login_not_registered),
        RESPONSE_LOGIN_REGISTERED_YET(444, R.string.login_registered_yet),
        RESPONSE_USER_UPDATE_FAILED(454, R.string.database_error),
        RESPONSE_USER_DELETE_FAILED(464, R.string.database_error),
        RESPONSE_LOGIN_CANNOT_BE_BLANK(701, R.string.login_can_not_be_blank),
        RESPONSE_PASSWORD_CANNOT_BE_BLANK(702, R.string.password_can_not_be_blank),
        ACCOUNT_DELETED(703, R.string.login_deleted_successful),
        ACCOUNT_UPDATED(704, R.string.login_updated_successful),
        FORBIDDEN_TO_DELETE(705, R.string.forbidden_to_delete_admin),
        FORBIDDEN_TO_UPDATE(706, R.string.forbidden_to_update_admin),
        LOAD_USER_DATA(707, R.string.you_have_to_load_data),
        REMINDED_PASSWORD(708, R.string.reminded_password)
    }
}