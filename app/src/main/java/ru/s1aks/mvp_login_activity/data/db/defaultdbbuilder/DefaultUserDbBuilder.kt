package ru.s1aks.mvp_login_activity.data.db.defaultdbbuilder

import ru.s1aks.mvp_login_activity.data.db.UserDao
import ru.s1aks.mvp_login_activity.data.db.UserEntity

class DefaultUserDbBuilder(private val roomDataSource: UserDao) {
    fun initDefaultUserDataBase() {
        for (i in 0..9) {
            when (i) {
                0 -> {
                    roomDataSource.createUser(
                        UserEntity(
                            userLogin = DefaultUsers.DEFAULT_ADMIN_LOGIN.value,
                            userPassword = DefaultUsers.DEFAULT_ADMIN_PASSWORD.value
                        )
                    )
                }

                in 1..9 -> {
                    roomDataSource.createUser(
                        UserEntity(
                            userLogin = DefaultUsers.DEFAULT_USER_LOGIN.value + i,
                            userPassword = DefaultUsers.DEFAULT_USER_PASSWORD.value + i
                        )
                    )
                }
            }
        }
    }
}

private enum class DefaultUsers(val value: String) {
    DEFAULT_ADMIN_LOGIN("admin"),
    DEFAULT_ADMIN_PASSWORD("admin"),
    DEFAULT_USER_LOGIN("User_"),
    DEFAULT_USER_PASSWORD("pass")
}