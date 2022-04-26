package ru.s1aks.mvp_login_activity.data.repository

import android.os.Handler
import ru.s1aks.mvp_login_activity.data.db.UserDao
import ru.s1aks.mvp_login_activity.data.db.UserEntity
import ru.s1aks.mvp_login_activity.data.db.defaultdbbuilder.DefaultUserDbBuilder
import ru.s1aks.mvp_login_activity.data.utils.MockDatabaseConstants
import ru.s1aks.mvp_login_activity.data.utils.fakeDelay
import ru.s1aks.mvp_login_activity.domain.repository.IUserDatabaseRepository

class MockUserDatabaseRepository(
    private val roomDataSource: UserDao,
    private val uiHandler: Handler,
) : IUserDatabaseRepository {

    init {
        if (roomDataSource.getAllUsers().isEmpty()) {
            DefaultUserDbBuilder(roomDataSource).apply {
                initDefaultUserDataBase()
            }
        }
    }
    override fun addUser(login: String, password: String, callback: (Int) -> Unit) {
        Thread {
            Thread.sleep(fakeDelay())
            uiHandler.post {
                callback(
                    if (roomDataSource.getUser(login) == null) {
                        roomDataSource.createUser(
                            UserEntity(
                                userLogin = login,
                                userPassword = password
                            )
                        )
                        MockDatabaseConstants.ResponseCodes.RESPONSE_SUCCESS.code
                    } else {
                        MockDatabaseConstants.ResponseCodes.RESPONSE_LOGIN_REGISTERED_YET.code
                    }
                )
            }
        }.start()
    }
    override fun getUser(login: String, callback: (UserEntity?) -> Unit) {
        Thread {
            Thread.sleep(fakeDelay())
            uiHandler.post {
                callback(roomDataSource.getUser(login))
            }
        }.start()
    }
    override fun getAllUsers(callback: (List<UserEntity>) -> Unit) {
        Thread {
            Thread.sleep(fakeDelay())
            uiHandler.post {
                callback(roomDataSource.getAllUsers())
            }
        }.start()
    }
    override fun updateUser(
        userId: String,
        newLogin: String,
        newPassword: String,
        isAuthorized: Boolean,
        callback: (Int) -> Unit,
    ) {
        Thread {
            Thread.sleep(fakeDelay())
            roomDataSource.updateUser(userId, newLogin, newPassword, isAuthorized)
            uiHandler.post {
                callback(
                    when (roomDataSource.getUser(newLogin)) {
                        null -> {
                            MockDatabaseConstants.ResponseCodes.RESPONSE_USER_UPDATE_FAILED.code
                        }
                        else -> {
                            MockDatabaseConstants.ResponseCodes.RESPONSE_SUCCESS.code
                        }
                    }
                )
            }
        }.start()
    }
    override fun deleteUser(login: String, callback: (Int) -> Unit) {
        Thread {
            Thread.sleep(fakeDelay())
            uiHandler.post {
                callback(
                    when (roomDataSource.getUser(login)) {
                        null -> {
                            MockDatabaseConstants.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code
                        }
                        else -> {
                            roomDataSource.deleteUser(login)
                            when (roomDataSource.getUser(login)) {
                                null -> {
                                    MockDatabaseConstants.ResponseCodes.RESPONSE_SUCCESS.code
                                }
                                else -> {
                                    MockDatabaseConstants.ResponseCodes.RESPONSE_USER_DELETE_FAILED.code
                                }
                            }
                        }
                    }
                )
            }
        }.start()
    }
}