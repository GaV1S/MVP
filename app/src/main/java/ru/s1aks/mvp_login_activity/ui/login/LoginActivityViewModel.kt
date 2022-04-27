package ru.s1aks.mvp_login_activity.ui.login

import ru.s1aks.mvp_login_activity.data.db.UserEntity
import ru.s1aks.mvp_login_activity.domain.interactor.login.IUserLoginInteractor
import ru.s1aks.mvp_login_activity.domain.interactor.remindpassword.IRemindPasswordInteractor
import ru.s1aks.mvp_login_activity.domain.repository.IUserDatabaseRepository
import ru.s1aks.mvp_login_activity.ui.utils.MessageMapper
import ru.s1aks.mvp_login_activity.ui.utils.Publisher

private const val DEFAULT_ADMIN_LOGIN = "admin"

class LoginActivityViewModel(
    private val userRepository: IUserDatabaseRepository,
    private val userLoginInteractor: IUserLoginInteractor,
    private val userRemindPasswordInteractor: IRemindPasswordInteractor,
) :
    LoginViewModelContract {
    private val messageMapper = MessageMapper()
    private var currentLogin: String? = null
    private var isAppStart = true

    override val showProgress: Publisher<Boolean> = Publisher()

    override val isLoginSuccess: Publisher<String> = Publisher()

    override val isLogout: Publisher<Boolean> = Publisher()

    override val receivedUser: Publisher<UserEntity> = Publisher()

    override val receivedUserList: Publisher<String> = Publisher()

    override val messenger: Publisher<Pair<Int, Any?>> = Publisher(true)

    override fun onCheckOnAppStartAuthorization() {
        if (isAppStart) {
            showProgress.post(true)
            userRepository.getAllUsers { userList ->
                for (user in userList) {
                    if (user.isAuthorized) {
                        isLoginSuccess.post(user.userLogin)
                        currentLogin = user.userLogin
                        break
                    }
                }
                showProgress.post(false)
                isAppStart = false
            }
        }
    }

    override fun onLogin(login: String, password: String) {
        if (login.isBlank()) {
            messenger.post(Pair(
                messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_LOGIN_CANNOT_BE_BLANK.code),
                null))
        } else {
            showProgress.post(true)
            userLoginInteractor.login(login, password) { response ->
                when (response) {
                    MessageMapper.ResponseCodes.RESPONSE_SUCCESS.code -> {
                        showProgress.post(false)
                        isLoginSuccess.post(login)
                        currentLogin = login
                    }

                    MessageMapper.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                        showProgress.post(false)
                        messenger.post(Pair(messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code),
                            login))
                    }

                    MessageMapper.ResponseCodes.RESPONSE_INVALID_PASSWORD.code -> {
                        showProgress.post(false)
                        messenger.post(Pair(messageMapper.getStringResource(response), null))
                    }
                }
            }
        }
    }

    override fun onLogout() {
        showProgress.post(true)
        userLoginInteractor.logout(currentLogin) { response ->
            when (response) {
                MessageMapper.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                    showProgress.post(false)
                    messenger.post(Pair(messageMapper.getStringResource(response), currentLogin))
                }

                MessageMapper.ResponseCodes.RESPONSE_SUCCESS.code -> {
                    isLogout.post(true)
                    currentLogin = null
                    showProgress.post(false)
                }
            }
        }
    }

    override fun onPasswordRemind(login: String) {
        if (login.isBlank()) {
            messenger.post(Pair(
                messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_LOGIN_CANNOT_BE_BLANK.code),
                null))
        } else {
            showProgress.post(true)
            userRemindPasswordInteractor.remindUserPassword(login) { password ->
                when (password) {
                    is Int -> {
                        messenger.post(Pair(
                            messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code),
                            login))
                    }
                    is String -> {
                        messenger.post(Pair(
                            messageMapper.getStringResource(MessageMapper.ResponseCodes.REMINDED_PASSWORD.code),
                            password))
                    }
                }
                showProgress.post(false)
            }
        }
    }

    override fun onGetUser(login: String) {
        if (login.isBlank()) {
            messenger.post(Pair(
                messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_LOGIN_CANNOT_BE_BLANK.code),
                null))
        } else {
            showProgress.post(true)
            userRepository.getUser(login) { user ->
                if (user == null) {
                    messenger.post(Pair(
                        messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code),
                        login))
                    showProgress.post(false)
                } else {
                    receivedUser.post(user)
                    showProgress.post(false)
                }
            }
        }
    }

    override fun onGetUserList() {
        showProgress.post(true)
        userRepository.getAllUsers { mUserList ->
            if (mUserList.isNotEmpty()) {
                val userList = StringBuilder()
                for (user in mUserList) {
                    userList.append(user.userLogin)
                    userList.append(" : ")
                    userList.append(user.userPassword)
                    userList.append(" : ")
                    userList.append(user.isAuthorized)
                    userList.append("\n")
                    userList.append("----------\n")
                }
                receivedUserList.post(userList.toString())
                showProgress.post(false)
            } else {
                receivedUserList.post("")
                showProgress.post(false)
            }
        }
    }

    override fun onDeleteUser(login: String) {
        when {
            login.isBlank() -> {
                messenger.post(Pair(
                    messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_LOGIN_CANNOT_BE_BLANK.code),
                    null))
            }

            login == DEFAULT_ADMIN_LOGIN -> {
                messenger.post(Pair(
                    messageMapper.getStringResource(MessageMapper.ResponseCodes.FORBIDDEN_TO_DELETE.code),
                    DEFAULT_ADMIN_LOGIN))
            }

            else -> {
                showProgress.post(true)
                userRepository.deleteUser(login) { response ->
                    when (response) {
                        MessageMapper.ResponseCodes.RESPONSE_LOGIN_NOT_REGISTERED.code -> {
                            messenger.post(Pair(messageMapper.getStringResource(response), login))
                            showProgress.post(false)
                        }
                        MessageMapper.ResponseCodes.RESPONSE_SUCCESS.code -> {
                            onGetUserList()
                            messenger.post(Pair(
                                messageMapper.getStringResource(MessageMapper.ResponseCodes.ACCOUNT_DELETED.code),
                                login))
                            showProgress.post(false)
                        }
                        MessageMapper.ResponseCodes.RESPONSE_USER_DELETE_FAILED.code -> {
                            messenger.post(Pair(messageMapper.getStringResource(response), null))
                            showProgress.post(false)
                        }
                    }
                }
            }
        }
    }

    override fun onUpdateUser(userId: String, login: String, password: String) {
        when {
            userId.isBlank() -> {
                messenger.post(Pair(
                    messageMapper.getStringResource(MessageMapper.ResponseCodes.LOAD_USER_DATA.code),
                    null))
            }

            login.isBlank() -> {
                messenger.post(Pair(
                    messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_LOGIN_CANNOT_BE_BLANK.code),
                    null))
            }

            password.isBlank() -> {
                messenger.post(Pair(
                    messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_PASSWORD_CANNOT_BE_BLANK.code),
                    null))
            }

            login == DEFAULT_ADMIN_LOGIN -> {
                messenger.post(Pair(
                    messageMapper.getStringResource(MessageMapper.ResponseCodes.FORBIDDEN_TO_UPDATE.code),
                    DEFAULT_ADMIN_LOGIN))
            }

            else -> {
                showProgress.post(true)
                userRepository.updateUser(userId.toInt(), login, password, false) { response ->
                    when (response) {
                        MessageMapper.ResponseCodes.RESPONSE_SUCCESS.code -> {
                            onGetUserList()
                            messenger.post(Pair(
                                messageMapper.getStringResource(MessageMapper.ResponseCodes.ACCOUNT_UPDATED.code),
                                login))
                            receivedUser.post(UserEntity(userId.toInt(), login, password))
                            showProgress.post(false)
                        }
                        MessageMapper.ResponseCodes.RESPONSE_USER_UPDATE_FAILED.code -> {
                            messenger.post(Pair(
                                messageMapper.getStringResource(MessageMapper.ResponseCodes.RESPONSE_USER_UPDATE_FAILED.code),
                                null))
                            showProgress.post(false)
                        }
                    }
                }
            }
        }
    }
}